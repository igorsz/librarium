package com.librarium.persistance;

import com.librarium.configuration.Configuration;
import com.librarium.search.FullDocumentPath;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Igor on 04.01.2017.
 */

@Deprecated
@Component
public class MongoDB implements Persistance{

    @Autowired
    Configuration configuration;

    private MongoDatabase database;
    private MongoCollection<BasicDBObject> collection;

    public MongoDB() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("test");
        this.collection = database.getCollection("documents", BasicDBObject.class);

    }

    public void persistDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations)
            throws IOException, DocumentAlreadyExistsException {
        BasicDBObject document = new BasicDBObject("key", fullDocumentPath.getFullPath())
                .append("content", new String(file.getBytes()))
                .append("metadata", metadata)
                .append("transformations", transformations);
        try {
            collection.insertOne(document);
        } catch (MongoWriteException e) {
            throw new DocumentAlreadyExistsException(fullDocumentPath);
        }
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException {
        BasicDBObject document = collection.findOneAndDelete(new Document("key", fullDocumentPath.getFullPath()));
        if (document == null) {
            throw new DocumentNotExistsException(fullDocumentPath);
        }
    }

    public void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException {
        UpdateResult updateResult = collection.updateOne(new BasicDBObject("key", fullDocumentPath.getFullPath()),
                new BasicDBObject("$set", new BasicDBObject("metadata", metadata)));
        if (updateResult.getMatchedCount() != 1){
            throw new DocumentNotExistsException(fullDocumentPath);
        }
    }
}
