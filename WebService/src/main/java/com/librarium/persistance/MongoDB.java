package com.librarium.persistance;

import com.librarium.configuration.Configuration;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import com.librarium.event.FullDocumentPath;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Igor on 04.01.2017.
 */

@Component
public class MongoDB implements Persistance{

    @Autowired
    Configuration configuration;

    private MongoDatabase database;
    private MongoCollection<BasicDBObject> documentsCollection;
    private MongoCollection<BasicDBObject> primaryKeysCollection;

    public MongoDB() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("test");
        this.documentsCollection = database.getCollection("documents", BasicDBObject.class);
        this.primaryKeysCollection= database.getCollection("primaryKeys", BasicDBObject.class);
    }

    boolean primaryKeyExists(FullDocumentPath path){
        FindIterable<BasicDBObject> key = primaryKeysCollection.find(new Document("key_id", path.getFullPath()));
        BasicDBObject first = key.first();
        if (first==null)
            return false;
        return true;
    }

    boolean insertPrimaryKey(FullDocumentPath path){
        BasicDBObject key = new BasicDBObject("key_id", path.getFullPath());
        try{
            primaryKeysCollection.insertOne(key);
        } catch (MongoWriteException e) {
            return false;
        }
        return true;
    }

    boolean deletePrimaryKey(FullDocumentPath path){
        BasicDBObject key = new BasicDBObject("key_id", path.getFullPath());
        DeleteResult deleteResult = primaryKeysCollection.deleteOne(key);
        if(deleteResult.getDeletedCount()==0)
            return false;
        return true;
    }

    public void persistDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations)
            throws IOException, DocumentAlreadyExistsException {
        BasicDBObject document = new BasicDBObject("key", fullDocumentPath.getFullPath())
                .append("content", new String(file.getBytes()))
                .append("metadata", metadata)
                .append("transformations", transformations);
        try {
            documentsCollection.insertOne(document);
        } catch (MongoWriteException e) {
            throw new DocumentAlreadyExistsException(fullDocumentPath);
        }
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException {
        BasicDBObject document = documentsCollection.findOneAndDelete(new Document("key", fullDocumentPath.getFullPath()));
        if (document == null) {
            throw new DocumentNotExistsException(fullDocumentPath);
        }
    }

    public void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException {
        UpdateResult updateResult = documentsCollection.updateOne(new BasicDBObject("key", fullDocumentPath.getFullPath()),
                new BasicDBObject("$set", new BasicDBObject("metadata", metadata)));
        if (updateResult.getMatchedCount() != 1){
            throw new DocumentNotExistsException(fullDocumentPath);
        }
    }
}