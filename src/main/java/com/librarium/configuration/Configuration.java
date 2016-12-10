package com.librarium.configuration;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Igor on 29.11.2016.
 */

@Component
@PropertySource("application.properties")
public class Configuration {

    @Autowired
    ElasticsearchConfiguration elasticsearchConfiguration;

    public ElasticsearchConfiguration getElasticsearchConfiguration() {
        return elasticsearchConfiguration;
    }
}
