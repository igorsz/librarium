package com.librarium.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Igor on 04.01.2017.
 */

@Component
@PropertySource("application.properties")
public class KafkaConfiguration {

    @Value("#{${kafka.bootstrap_servers}}")
    String bootstrapServers;

    @Value("#{${kafka.acks}}")
    String acks;

    @Value("#{${kafka.retries}}")
    int retries;

    @Value("#{${kafka.batch_size}}")
    String batchSize;

    @Value("#{${kafka.key_serializer}}")
    String keySerializer;

    @Value("#{${kafka.value_serializer}}")
    String valueSerializer;

    public String getAcks() {
        return acks;
    }

    public int getRetries() {
        return retries;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }
}
