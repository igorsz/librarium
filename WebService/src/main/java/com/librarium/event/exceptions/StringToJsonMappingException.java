package com.librarium.event.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Igor on 07.02.2017.
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "String to Json mapping failed")
public class StringToJsonMappingException extends Exception {

    private static final Logger logger = LogManager.getLogger(StringToJsonMappingException.class);

    public StringToJsonMappingException(String string) {
        logger.info("Mapping from String to Json failed. String provided: {}", string);
    }
}
