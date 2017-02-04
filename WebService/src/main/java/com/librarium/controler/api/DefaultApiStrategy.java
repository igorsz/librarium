package com.librarium.controler.api;

import com.librarium.kafka.KafkaMessageProducer;
import com.librarium.persistance.Cassandra;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import com.librarium.persistance.MongoDB;
import com.librarium.search.Elasticsearch;
import com.librarium.event.FullDocumentPath;
import com.librarium.event.Index;
import com.librarium.event.Type;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Igor on 08.12.2016.
 */

@Component
public class DefaultApiStrategy implements ApiStrategy {

    @Autowired
    Elasticsearch elasticsearch;

    @Autowired
    MongoDB mongoDB;

    @Autowired
    Cassandra cassandra;

    @Autowired
    KafkaMessageProducer kafka;

    public void search(JSONObject search, OutputStream outputStream) {
        elasticsearch.search(search,outputStream);
    }

    public void search(JSONObject search, OutputStream outputStream, List<Index> indicesList) {
        elasticsearch.search(search,outputStream,indicesList);
    }

    public void search(JSONObject search, OutputStream outputStream, List<Index> indexList, List<Type> typeList) {
        elasticsearch.search(search,outputStream,indexList,typeList);
    }

    public void createIndex(Index index, JSONObject body, OutputStream outputStream) {
        elasticsearch.createIndex(index, body, outputStream);
    }

    public void createIndex(Index index, OutputStream outputStream) {
        elasticsearch.deleteIndex(index, outputStream);
    }

    public void putDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException, DocumentAlreadyExistsException {
        cassandra.persistDocument(fullDocumentPath, file, metadata, transformations);
        kafka.createDocument(fullDocumentPath,metadata,transformations);
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException {
        cassandra.deleteDocument(fullDocumentPath);
        kafka.deleteDocument(fullDocumentPath);
    }

    public void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException {
        cassandra.updateDocument(fullDocumentPath, metadata);
        kafka.updateDocument(fullDocumentPath);
    }

    public void listIndices(OutputStream outputStream) {
        elasticsearch.listIndices(outputStream);
    }
}
