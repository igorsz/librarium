package com.librarium.search;

import com.librarium.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.System.exit;

/**
 * Created by Igor on 05.12.2016.
 */

@Component
public class Elasticsearch {

    private static final Logger logger = LogManager.getLogger(Elasticsearch.class);
    private TransportClient client;
    private Configuration configuration;

    @Autowired
    public Elasticsearch(Configuration configuration) {
        this.configuration = configuration;
        setUpClient();
    }

    private void setUpClient() {
        Settings settings = Settings.builder()
                .put("cluster.name", "librarium")
                .put("xpack.security.user", "elastic:changeme").build();

        client = new PreBuiltXPackTransportClient(settings);
        for (String address : configuration.getElasticsearchConfiguration().getNodes().keySet()) {
            int port = Integer.parseInt(configuration.getElasticsearchConfiguration().getNodes().get(address));
            try {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(address), port));
            } catch (UnknownHostException e) {
                logger.error("Elasticsearch address {} not found", address);
                exit(1);
            }
        }
    }

    public SearchResponse search(){
        return client.prepareSearch().get();
    }


    public void search(JSONObject search, OutputStream outputStream) {
        SearchResponse searchResponse = client.prepareSearch().setQuery(QueryBuilders.simpleQueryStringQuery(search.toString())).execute().actionGet();
        logger.info(searchResponse.toString());
    }

}
