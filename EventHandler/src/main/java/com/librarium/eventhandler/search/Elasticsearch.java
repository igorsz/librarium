package com.librarium.eventhandler.search;

import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.event.Event;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.sniff.ElasticsearchHostsSniffer;
import org.elasticsearch.client.sniff.HostsSniffer;
import org.elasticsearch.client.sniff.Sniffer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Igor on 04.02.2017.
 */
public class Elasticsearch {

    private Configuration configuration;
    private RestClient restClient;


    @Autowired
    public Elasticsearch(Configuration configuration) {
        this.configuration = configuration;
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

    public void createDocument(Event event){

    }

    public void modifyDocument(Event event){

    }

    public void deleteDocument(Event event){

    }


}
