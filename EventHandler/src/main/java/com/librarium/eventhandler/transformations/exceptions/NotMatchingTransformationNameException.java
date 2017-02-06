package com.librarium.eventhandler.transformations.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Igor on 05.02.2017.
 */
public class NotMatchingTransformationNameException extends Exception {

    private static final Logger logger = LogManager.getLogger(NotMatchingTransformationNameException.class);

    public NotMatchingTransformationNameException(String transformationName){
        logger.error("Transformation name provided in configuration doesn't match transformation class with transformation name: {}", transformationName);
    }
}
