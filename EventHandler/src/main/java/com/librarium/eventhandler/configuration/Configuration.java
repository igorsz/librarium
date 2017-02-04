package com.librarium.eventhandler.configuration;

import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 04.02.2017.
 */
@Component
@PropertySource("application.properties")
@Value
public class Configuration {

    @Autowired
    KafkaConfiguration kafkaConfiguration;
}
