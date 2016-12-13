package com.librarium.search;

import com.librarium.configuration.Configuration;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.sniff.ElasticsearchHostsSniffer;
import org.elasticsearch.client.sniff.HostsSniffer;
import org.elasticsearch.client.sniff.Sniffer;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    Sniffer sniffer;
    private Configuration configuration;
    private final String HTTP_GET = "GET";
    private final String HTTP_POST = "POST";
    private final String HTTP_PUT = "PUT";
    private final String HTTP_DELETE = "DELETE";

    @Autowired
    public Elasticsearch(Configuration configuration) {
        this.configuration = configuration;
        setUpClient();
    }

    private void setUpClient(){
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "changeme"));

        restClient = RestClient.builder(new HttpHost("localhost", 9200))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }).build();

        HostsSniffer hostsSniffer = new ElasticsearchHostsSniffer(restClient,
                ElasticsearchHostsSniffer.DEFAULT_SNIFF_REQUEST_TIMEOUT,
                ElasticsearchHostsSniffer.Scheme.HTTP);

        this.sniffer = Sniffer.builder(restClient)
                .setHostsSniffer(hostsSniffer)
                .build();
    }

    public Response search(){
        String query = "{\"query\":{\"match_all\":{}}}";
        Response response = null;
        try {
            response = restClient.performRequest(
                    HTTP_GET,
                    "_search",
                    new Hashtable<String, String>(),
                    new StringEntity(query));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
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
            outputStream.write(stringResponse.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
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
}
