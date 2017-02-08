package com.librarium.controler.api;

import com.google.gson.JsonObject;
import com.librarium.event.StringToJsonMapper;
import com.librarium.event.exceptions.StringToJsonMappingException;
import com.librarium.healthcheck.ApplicationHealthMonitorSystem;
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

    @Autowired
    StringToJsonMapper stringToJsonMapper;

    @Autowired
    ApplicationHealthMonitorSystem applicationHealthMonitorSystem;

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

    public void putDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException, DocumentAlreadyExistsException, StringToJsonMappingException {
        JsonObject metadataJson = stringToJsonMapper.getJsonFromString(metadata);
        JsonObject transformationsJson = stringToJsonMapper.getJsonFromString(transformations);
        cassandra.persistDocument(fullDocumentPath, file, metadata, transformations);
        kafka.createDocument(fullDocumentPath,metadataJson,transformationsJson);
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException {
        cassandra.deleteDocument(fullDocumentPath);
        kafka.deleteDocument(fullDocumentPath);
    }

    public void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException, StringToJsonMappingException {
        JsonObject metadataJson = stringToJsonMapper.getJsonFromString(metadata);
        cassandra.updateDocument(fullDocumentPath, metadata);
        kafka.updateDocument(fullDocumentPath, metadataJson);
    }

    public void listIndices(OutputStream outputStream) {
        elasticsearch.listIndices(outputStream);
    }

    public void getDocument(FullDocumentPath fullDocumentPath, OutputStream outputStream) throws DocumentNotExistsException, IOException {
        cassandra.getDocument(fullDocumentPath, outputStream);
    }

    public void getHealthStatus(OutputStream outputStream) {
        applicationHealthMonitorSystem.getApplicationHealthStatus(outputStream);
    }
}
