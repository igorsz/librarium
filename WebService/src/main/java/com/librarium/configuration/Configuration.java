package com.librarium.configuration;

import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 29.11.2016.
 */

@Component
@PropertySource("application.properties")
@Value
public class Configuration {

    @Autowired
    ElasticsearchConfiguration elasticsearchConfiguration;

    @Autowired
    KafkaConfiguration kafkaConfiguration;

    @Autowired
    CassandraConfiguration cassandraConfiguration;

}
