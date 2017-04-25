package com.librarium.search;

import com.librarium.authentication.DummyAuthentication;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.common.event.Index;
import com.librarium.common.event.Type;
import com.librarium.configuration.Configuration;
import com.librarium.kafka.KafkaMessageProducer;
import org.apache.http.HttpHost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.sniff.ElasticsearchHostsSniffer;
import org.elasticsearch.client.sniff.HostsSniffer;
import org.elasticsearch.client.sniff.Sniffer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;


/**
 * Created by Igor on 05.12.2016.
 */

@Component
public class Elasticsearch {

    private static final Logger logger = LogManager.getLogger(Elasticsearch.class);
    private RestClient restClient;
    private Configuration configuration;
    private final String HTTP_GET = "GET";
    private final String HTTP_POST = "POST";
    private final String HTTP_PUT = "PUT";
    private final String HTTP_DELETE = "DELETE";

    @Autowired
    KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    DummyAuthentication authentication;

    @Autowired
    public Elasticsearch(Configuration configuration) {
        this.configuration = configuration;
        setUpClient();
    }

    private void setUpClient(){
        restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        HostsSniffer hostsSniffer = new ElasticsearchHostsSniffer(restClient,
                ElasticsearchHostsSniffer.DEFAULT_SNIFF_REQUEST_TIMEOUT,
                ElasticsearchHostsSniffer.Scheme.HTTP);

        Sniffer sniffer = Sniffer.builder(restClient)
                .setHostsSniffer(hostsSniffer)
                .build();
    }

    public void search(JSONObject search, OutputStream outputStream) {
        executeESRequest(HTTP_POST, "/_search", search.toString(), outputStream);
    }

    public void search(JSONObject search, OutputStream outputStream, List<Index> indexList) {
        String endpoint = prepareIndexString(indexList)+"/_search";
        executeESRequest(HTTP_POST, endpoint, search.toString(), outputStream);
    }

    public void search(JSONObject search, OutputStream outputStream, List<Index> indexList, List<Type> typeList) {
        String endpoint = prepareIndexString(indexList)+"/"+prepareTypeString(typeList)+"/_search";
        executeESRequest(HTTP_POST, endpoint, search.toString(), outputStream);
    }

    private void executeESRequest(String httpMethod, String endpoint, String query, OutputStream outputStream) {
        Response response;
        try {
            response = restClient.performRequest(
                    httpMethod,
                    endpoint,
                    new Hashtable<String, String>(),
                    new StringEntity(query));
            String stringResponse = EntityUtils.toString(response.getEntity());
            String responseEntitlment = checkResponseEntitlment(stringResponse);
            outputStream.write(responseEntitlment.getBytes());
        } catch (IOException e) {
            logger.error("Request error. Endpoint: {}, http method: {}",endpoint,httpMethod);
        }
    }

    private String checkResponseEntitlment(String stringResponse) {
        JSONParser parser = new JSONParser();
        JSONObject jsonRespone;
        JSONArray arrayToReturn = new JSONArray();
        int counter = 0;
        try {
            jsonRespone = (JSONObject) parser.parse(stringResponse);
            jsonRespone.remove("_shards");
            JSONObject hitsWrapped = (JSONObject) jsonRespone.get("hits");
            JSONArray hitsArray= (JSONArray) hitsWrapped.get("hits");
            for (Object object : hitsArray){
                JSONObject hit = (JSONObject) object;
                FullDocumentPath path = new FullDocumentPath(hit.get("_index").toString(),hit.get("_type").toString(),hit.get("_id").toString());
                if(authentication.authenticate(path)){
                    arrayToReturn.add(counter,hit);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return arrayToReturn.toString();
    }

    private void executeESRequest(String httpMethod, String endpoint, OutputStream outputStream) {
        Response response;
        try {
            response = restClient.performRequest(
                    httpMethod,
                    endpoint,
                    new Hashtable<String, String>());
            String stringResponse = EntityUtils.toString(response.getEntity());
            outputStream.write(stringResponse.getBytes());
        } catch (IOException e) {
            logger.error("Request error. Endpoint: {}, http method: {}",endpoint,httpMethod);
        }
    }

    private String prepareIndexString(List<Index> indexList) {
        String namespaces = indexList.get(0).getIndex();
        indexList.remove(0);
        for(Index index : indexList) namespaces += "," + index.getIndex();
        return namespaces;
    }

    private String prepareTypeString(List<Type> typeList) {
        String namespaces = typeList.get(0).getType();
        typeList.remove(0);
        for(Type type : typeList) namespaces += "," + type.getType();
        return namespaces;
    }

    public void createIndex(Index index, JSONObject body, OutputStream outputStream) {
        executeESRequest(HTTP_PUT, "/"+index.getIndex(), body.toString(), outputStream);
    }

    public void deleteIndex(Index index, OutputStream outputStream) {
        executeESRequest(HTTP_DELETE,"/"+index.getIndex(),outputStream);
    }

    public void listIndices(OutputStream outputStream) {
        executeESRequest(HTTP_GET,"/_cat/indices?v",outputStream);
    }
}
