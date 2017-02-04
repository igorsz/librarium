package com.librarium.event.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Igor on 11.12.2016.
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Index not valid")
public class IndexNameException extends Exception {

    private static final Logger logger = LogManager.getLogger(IndexNameException.class);

    public IndexNameException(String index) {
        logger.info("Index: {}, is not a valid index name", index);
    }
}
