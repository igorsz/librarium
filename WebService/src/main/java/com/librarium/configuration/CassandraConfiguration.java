package com.librarium.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 01.02.2017.
 */

@Component
@PropertySource("application.properties")
public class CassandraConfiguration {

    @Value("#{${cassandra.host}}")
    String host;

    @Value("#{${cassandra.keyspace}}")
    String keySpace;

    public String getHost() {
        return host;
    }

    public String getKeySpace() {
        return keySpace;
    }
}
