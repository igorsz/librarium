package com.librarium.eventhandler.transformations.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Igor on 05.02.2017.
 */
public class FailedToCreateTransformationClassException extends Exception {

    private static final Logger logger = LogManager.getLogger(FailedToCreateTransformationClassException.class);

    public FailedToCreateTransformationClassException(Exception e) {
        logger.info("Failed to create transformation class mentioned in configuration. Message: {}, Stack: {}", e.getMessage(), e.getStackTrace());
    }
}
