package com.librarium.eventhandler.search;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.event.Event;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Igor on 04.02.2017.
 */

@Component
public class Elasticsearch {

    private static final Logger logger = LogManager.getLogger(Elasticsearch.class);

    private Configuration configuration;
    private RestClient restClient;
    private JsonParser parser;
    private final String HTTP_PUT = "PUT";
    private final String HTTP_DELETE = "DELETE";


    @Autowired
    public Elasticsearch(Configuration configuration) {
        this.configuration = configuration;
        this.parser = new JsonParser();
        setUpClient();

    }

    private void setUpClient() {
        restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        HostsSniffer hostsSniffer = new ElasticsearchHostsSniffer(restClient,
                ElasticsearchHostsSniffer.DEFAULT_SNIFF_REQUEST_TIMEOUT,
                ElasticsearchHostsSniffer.Scheme.HTTP);
        Sniffer.builder(restClient)
                .setHostsSniffer(hostsSniffer)
                .build();
    }

    public void upsertDocument(Event event) {
        executeESRequest(HTTP_PUT, event.getFullDocumentPath().getFullPath(), event.getMetadata());
    }

    public void deleteDocument(Event event) {
        executeESRequest(HTTP_DELETE,event.getFullDocumentPath().getFullPath());
    }

    private void executeESRequest(String httpMethod, String endpoint) {
        Response response;
        try {
            response = restClient.performRequest(
                    httpMethod,
                    endpoint,
                    new Hashtable<String, String>());
            String stringResponse = EntityUtils.toString(response.getEntity());
            logger.info(stringResponse);
        } catch (IOException e) {
            logger.error("Request error. Endpoint: {}, http method: {}, stack: {}",endpoint,httpMethod, e.getStackTrace());
        }
    }

    private void executeESRequest(String httpMethod, String endpoint, String query) {
        Response response;
        try {
            response = restClient.performRequest(
                    httpMethod,
                    endpoint,
                    new Hashtable<String, String>(),
                    new StringEntity(query));
            String stringResponse = EntityUtils.toString(response.getEntity());
            logger.info(stringResponse);
        } catch (IOException e) {
            logger.error("Request error. Endpoint: {}, http method: {}, stack: {}", endpoint, httpMethod, e.getStackTrace());
        }
    }
}
