package com.librarium.eventhandler.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Igor on 05.02.2017.
 */
@Component
@PropertySource("application.properties")
@Data
public class TransformationConfiguration {

    @Value("#{${transformation.classes}}")
    Map<String, String> transformationClasses;
}
