package com.librarium.persistance;

import com.librarium.healthcheck.messages.HealthStatus;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import com.librarium.event.FullDocumentPath;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Igor on 01.02.2017.
 */
public interface Persistance {
    void persistDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException, DocumentAlreadyExistsException;
    void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException;
    void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException;
    void getDocument(FullDocumentPath fullDocumentPath, OutputStream outputStream) throws DocumentNotExistsException, IOException;
    HealthStatus performHealthCheck();
}
