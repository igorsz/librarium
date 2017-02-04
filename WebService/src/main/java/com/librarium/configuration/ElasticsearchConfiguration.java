package com.librarium.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Igor on 29.11.2016.
 */

@Component
@PropertySource("application.properties")
@Data
public class ElasticsearchConfiguration {

    @Value("#{${elasticsearch.address}}")
    Map<String, String> nodes;
}
