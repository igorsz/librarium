package com.librarium.eventhandler.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 04.02.2017.
 */
@Component
@PropertySource("application.properties")
@Data
public class KafkaConfiguration {

    @Value("#{${kafka.bootstrap_servers}}")
    String bootstrapServers;

    @Value("#{${kafka.group_id}}")
    String groupId;

    @Value("#{${kafka.key_deserializer}}")
    String keyDeserializer;

    @Value("#{${kafka.value_deserializer}}")
    String valueDeserializer;

    @Value("#{${kafka.session_timeout}}")
    String sessionTimeout;
}
