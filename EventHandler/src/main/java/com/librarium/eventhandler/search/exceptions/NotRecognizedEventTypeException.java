package com.librarium.eventhandler.search.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Igor on 06.02.2017.
 */
public class NotRecognizedEventTypeException extends Exception {

    private static final Logger logger = LogManager.getLogger(NotRecognizedEventTypeException.class);

    public NotRecognizedEventTypeException(String type) {
        logger.info("Not recognized event type occurred: {}", type);
    }
}
