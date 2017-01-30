package com.librarium.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 29.11.2016.
 */

@Component
@PropertySource("application.properties")
public class Configuration {

    @Autowired
    ElasticsearchConfiguration elasticsearchConfiguration;

    @Autowired
    KafkaConfiguration kafkaConfiguration;

    public ElasticsearchConfiguration getElasticsearchConfiguration() {
        return elasticsearchConfiguration;
    }

    public KafkaConfiguration getKafkaConfiguration() {
        return kafkaConfiguration;
    }
}
