package com.librarium.persistance;

import com.librarium.common.event.FullDocumentPath;
import com.librarium.healthcheck.messages.HealthStatus;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
public class MongoDBTest {

    private MongoDB mongoDB;
    private MongoCollection<BasicDBObject> primaryKeysCollection;
    private MongoDatabase database;

    @Before
    public void setUP() {
        primaryKeysCollection = mock(MongoCollection.class);
        database = mock(MongoDatabase.class);
        mongoDB = new MongoDB(database, primaryKeysCollection);
    }

    @Test
    public void primaryKeyExistsTest() {
        //given
        FindIterable<BasicDBObject> findIterable = mock(FindIterable.class);
        when(primaryKeysCollection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(new BasicDBObject());
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        //when
        boolean primaryKeyExists = mongoDB.primaryKeyExists(fullDocumentPath);
        //then
        assertTrue(primaryKeyExists);
    }

    @Test
    public void primaryKeyNotExistsTest() {
        //given
        FindIterable<BasicDBObject> findIterable = mock(FindIterable.class);
        when(primaryKeysCollection.find(any(Document.class))).thenReturn(findIterable);
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        //when
        boolean primaryKeyExists = mongoDB.primaryKeyExists(fullDocumentPath);
        //then
        assertFalse(primaryKeyExists);
    }

    @Test
    public void prepareDocumentKeyTest() {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        //when
        BasicDBObject basicDBObject = mongoDB.prepareDocumentKey(fullDocumentPath);
        //then
        assertEquals("{ \"key_id\" : \"index/type/id\"}", basicDBObject.toString());
    }

    @Test
    public void deletePrimaryKeyTest() {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        DeleteResult deleteResult = mock(DeleteResult.class);
        when(deleteResult.getDeletedCount()).thenReturn(1L);
        when(primaryKeysCollection.deleteOne(any(BasicDBObject.class))).thenReturn(deleteResult);
        //when
        boolean deletePrimaryKey = mongoDB.deletePrimaryKey(fullDocumentPath);
        //then
        assertTrue(deletePrimaryKey);
    }

    @Test
    public void deletePrimaryKeyReturnsFalseTest() {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        DeleteResult deleteResult = mock(DeleteResult.class);
        when(deleteResult.getDeletedCount()).thenReturn(0L);
        when(primaryKeysCollection.deleteOne(any(BasicDBObject.class))).thenReturn(deleteResult);
        //when
        boolean deletePrimaryKey = mongoDB.deletePrimaryKey(fullDocumentPath);
        //then
        assertFalse(deletePrimaryKey);
    }

    @Test
    public void performHealthCheckTest() {
        //given
        when(database.getName()).thenReturn("NAME");
        //when
        HealthStatus healthStatus = mongoDB.performHealthCheck();
        //then
        assertEquals(HealthStatus.GREEN, healthStatus);
    }

    @Test
    public void performFailedHealthCheckTest() {
        //given
        when(database.getName()).thenThrow(Exception.class);
        //when
        HealthStatus healthStatus = mongoDB.performHealthCheck();
        //then
        assertEquals(HealthStatus.RED, healthStatus);
    }


}
