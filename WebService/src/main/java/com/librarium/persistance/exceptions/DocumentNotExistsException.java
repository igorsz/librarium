package com.librarium.persistance.exceptions;

import com.librarium.search.FullDocumentPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Igor on 31.01.2017.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Document not found")
public class DocumentNotExistsException extends Exception {

    private static final Logger logger = LogManager.getLogger(DocumentNotExistsException.class);

    public DocumentNotExistsException(FullDocumentPath path) {
        logger.info("Document: {} doesn't exist", path);
    }
}
