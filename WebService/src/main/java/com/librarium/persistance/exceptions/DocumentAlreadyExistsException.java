package com.librarium.persistance.exceptions;

import com.librarium.event.FullDocumentPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Igor on 31.01.2017.
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = " Document already exists")
public class DocumentAlreadyExistsException extends Exception{

    private static final Logger logger = LogManager.getLogger(DocumentAlreadyExistsException.class);

    public DocumentAlreadyExistsException(FullDocumentPath path) {
        logger.info("Document: {} already exists", path);
    }
}
