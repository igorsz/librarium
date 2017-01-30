package com.librarium.controler.api;

import com.librarium.kafka.KafkaMsgProducer;
import com.librarium.persistance.MongoDB;
import com.librarium.search.Elasticsearch;
import com.librarium.search.FullDocumentPath;
import com.librarium.search.Index;
import com.librarium.search.Type;
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
    KafkaMsgProducer kafka;

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

    public void putDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException {
        mongoDB.persistDocument(fullDocumentPath, file, metadata, transformations);
        kafka.createDocument(fullDocumentPath,metadata,transformations);
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) {
        mongoDB.deleteDocument(fullDocumentPath);
        kafka.deleteDocument(fullDocumentPath);
    }

    public void updateDocumnt(FullDocumentPath fullDocumentPath, String metadata, String transformations) {
        mongoDB.updateDocument(fullDocumentPath, metadata, transformations);
        kafka.updateDocument(fullDocumentPath);
    }
}
