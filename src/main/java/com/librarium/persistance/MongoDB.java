package com.librarium.persistance;

import com.librarium.configuration.Configuration;
import com.librarium.search.FullDocumentPath;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Igor on 04.01.2017.
 */

@Component
public class MongoDB {

    @Autowired
    Configuration configuration;

    private MongoDatabase database;
    private MongoCollection<BasicDBObject> collection;

    public MongoDB(){
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        this.database = mongoClient.getDatabase("test");
        this.collection = database.getCollection("documents",BasicDBObject.class);

    }

    public void persistDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException {
        BasicDBObject document = new BasicDBObject("key",fullDocumentPath.getFullPath())
                .append("content",new String(file.getBytes()))
                .append("metadata", metadata)
                .append("transformations", transformations);
        collection.insertOne(document);
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) {
        BasicDBObject document = collection.findOneAndDelete(new Document("key",fullDocumentPath.getFullPath()));
    }

    public void updateDocument(FullDocumentPath fullDocumentPath, String metadata, String transformations) {
        collection.updateOne(new BasicDBObject("key", fullDocumentPath.getFullPath()),
                new BasicDBObject("$set", new BasicDBObject("metadata", metadata)));
    }
}
