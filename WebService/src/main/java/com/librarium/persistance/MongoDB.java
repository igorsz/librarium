package com.librarium.persistance;

import com.librarium.common.event.FullDocumentPath;
import com.librarium.configuration.Configuration;
import com.librarium.healthcheck.HealthCheck;
import com.librarium.healthcheck.messages.HealthStatus;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 04.01.2017.
 */

@Component
public class MongoDB implements HealthCheck{

    private static final Logger logger = LogManager.getLogger(MongoDB.class);

    @Autowired
    Configuration configuration;

    private MongoDatabase database;
    private MongoCollection<BasicDBObject> primaryKeysCollection;

    public MongoDB() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("test");
        this.primaryKeysCollection= database.getCollection("primaryKeys", BasicDBObject.class);
    }

    public MongoDB(MongoDatabase database, MongoCollection<BasicDBObject> primaryKeysCollection) {
        this.database = database;
        this.primaryKeysCollection = primaryKeysCollection;
    }

    boolean primaryKeyExists(FullDocumentPath path){
        FindIterable<BasicDBObject> key = primaryKeysCollection.find(new Document("key_id", path.getFullPath()));
        BasicDBObject first = key.first();
        return first != null;
    }

    boolean insertPrimaryKey(FullDocumentPath path){
        BasicDBObject key = prepareDocumentKey(path);
        try{
            primaryKeysCollection.insertOne(key);
        } catch (MongoWriteException e) {
            return false;
        }
        return true;
    }

    protected BasicDBObject prepareDocumentKey(FullDocumentPath path) {
        return new BasicDBObject("key_id", path.getFullPath());
    }

    boolean deletePrimaryKey(FullDocumentPath path){
        BasicDBObject key = prepareDocumentKey(path);
        DeleteResult deleteResult = primaryKeysCollection.deleteOne(key);
        return deleteResult.getDeletedCount() != 0;
    }

    public HealthStatus performHealthCheck() {
        try{
            //getting name of database is enough to assume that connection is green
            database.getName();
            return HealthStatus.GREEN;
        } catch (Exception e){
            logger.error("Mongo health status returned RED");
            return HealthStatus.RED;
        }
    }
}
