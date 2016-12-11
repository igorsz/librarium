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

    @Autowired
    public Elasticsearch(Configuration configuration) {
        this.configuration = configuration;
        setUpClient();
    }

//    private void setUpClient() {
//        Settings settings = Settings.builder()
//                .put("cluster.name", "librarium")
//                .put("xpack.security.user", "elastic:changeme").build();
//
//        client = new PreBuiltXPackTransportClient(settings);
//        for (String address : configuration.getElasticsearchConfiguration().getNodes().keySet()) {
//            int port = Integer.parseInt(configuration.getElasticsearchConfiguration().getNodes().get(address));
//            try {
//                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(address), port));
//            } catch (UnknownHostException e) {
//                logger.error("Elasticsearch address {} not found", address);
//                exit(1);
//            }
//        }
//    }

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

        Sniffer sniffer = Sniffer.builder(restClient)
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
                    new Hashtable(),
                    new StringEntity(query));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public void search(JSONObject search, OutputStream outputStream) {
        String query = search.toString();
        Response response = null;
        try {
            response = restClient.performRequest(
                    HTTP_POST,
                    "_search",
                    new Hashtable(),
                    new StringEntity(query));
            String stringResponse = EntityUtils.toString(response.getEntity());
            outputStream.write(stringResponse.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void search(JSONObject search, OutputStream outputStream, List<Namespace> namespacesList) {
        String query = search.toString();
        Response response = null;
        String endpoint = prepareNamespacesString(namespacesList)+"/_search";
        try {
            response = restClient.performRequest(
                    HTTP_POST,
                    endpoint,
                    new Hashtable(),
                    new StringEntity(query));
            String stringResponse = EntityUtils.toString(response.getEntity());
            outputStream.write(stringResponse.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String prepareNamespacesString(List<Namespace> namespacesList) {
        String namespaces = namespacesList.get(0).getNamespace();
        namespacesList.remove(0);
        for(Namespace namespace : namespacesList){
            namespaces+=","+namespace.getNamespace();
        }
        return namespaces;
    }
}
