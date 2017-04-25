package com.librarium.persistance;

import com.datastax.driver.core.*;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.healthcheck.messages.HealthStatus;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
public class CassandraTest {

    private Cassandra cassandra;
    private Session session;
    private MongoDB mongoDB;

    @Before
    public void setUp() {
        session = mock(Session.class);
        mongoDB = mock(MongoDB.class);
        cassandra = new Cassandra(session, mongoDB);
    }

    @Test
    public void prepareStatementsTest() {
        //given
        PreparedStatement insertMetadataStatement = mock(PreparedStatement.class);
        PreparedStatement insertContentStatement = mock(PreparedStatement.class);
        PreparedStatement deleteStatement = mock(PreparedStatement.class);
        PreparedStatement updateStataement = mock(PreparedStatement.class);
        PreparedStatement getContentStatement = mock(PreparedStatement.class);
        when(session.prepare(anyString()))
                .thenReturn(insertMetadataStatement)
                .thenReturn(insertContentStatement)
                .thenReturn(deleteStatement)
                .thenReturn(updateStataement)
                .thenReturn(getContentStatement);
        //when
        cassandra.prepareStatements();
        //then
        assertEquals(insertMetadataStatement, cassandra.insertMetadataStatement);
        assertEquals(insertContentStatement, cassandra.insertContentStatement);
        assertEquals(deleteStatement, cassandra.deleteStatement);
        assertEquals(updateStataement, cassandra.updateStataement);
        assertEquals(getContentStatement, cassandra.getContentStatement);
    }

    @Test
    public void executeStatementTest() {
        //given
        BoundStatement statement = mock(BoundStatement.class);
        //when
        boolean executedStatement = cassandra.executeStatement(statement);
        //then
        assertTrue(executedStatement);
    }

    @Test
    public void executeFailedStatementTest() {
        //given
        BoundStatement statement = mock(BoundStatement.class);
        when(session.execute(any(BoundStatement.class))).thenThrow(Exception.class);
        //when
        boolean executedStatement = cassandra.executeStatement(statement);
        //then
        assertFalse(executedStatement);
    }

    @Test
    public void performHealthCheckTest() {
        //given
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.one()).thenReturn(mock(Row.class));
        when(session.execute(anyString())).thenReturn(resultSet);
        //when
        HealthStatus healthStatus = cassandra.performHealthCheck();
        //then
        assertEquals(HealthStatus.GREEN, healthStatus);
    }

    @Test
    public void performFailedHealthCheckTest() {
        //when
        HealthStatus healthStatus = cassandra.performHealthCheck();
        //then
        assertEquals(HealthStatus.RED, healthStatus);
    }

    @Test
    public void updateDocumentTest() throws DocumentNotExistsException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        when(mongoDB.primaryKeyExists(any(FullDocumentPath.class))).thenReturn(true);
        preparePreparedStatements();
        //when
        boolean documentUpdated = cassandra.updateDocument(fullDocumentPath, "metadata");
        //then
        assertTrue(documentUpdated);
    }

    @Test(expected = DocumentNotExistsException.class)
    public void updateDocumentThrowsDocumentNotExistsExceptionTest() throws DocumentNotExistsException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        when(mongoDB.primaryKeyExists(any(FullDocumentPath.class))).thenReturn(false);
        //when
        cassandra.updateDocument(fullDocumentPath, "metadata");

    }

    @Test
    public void deleteDocumentTest() throws DocumentNotExistsException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        when(mongoDB.deletePrimaryKey(any(FullDocumentPath.class))).thenReturn(true);
        preparePreparedStatements();
        //when
        boolean documentUpdated = cassandra.deleteDocument(fullDocumentPath);
        //then
        assertTrue(documentUpdated);
    }

    @Test(expected = DocumentNotExistsException.class)
    public void deleteDocumentThrowsDocumentNotExistsExceptionTest() throws DocumentNotExistsException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        when(mongoDB.deletePrimaryKey(any(FullDocumentPath.class))).thenReturn(false);
        preparePreparedStatements();
        //when
        cassandra.deleteDocument(fullDocumentPath);
    }

    @Test
    public void persistDocumentContentTest() throws IOException, DocumentAlreadyExistsException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("bytes".getBytes());
        preparePreparedStatements();
        //when
        boolean documentContentPersisted = cassandra.persistDocumentContent(fullDocumentPath, file);
        //then
        assertTrue(documentContentPersisted);
    }

    @Test
    public void persistDocumentTest() throws IOException, DocumentAlreadyExistsException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("bytes".getBytes());
        when(mongoDB.insertPrimaryKey(any(FullDocumentPath.class))).thenReturn(true);
        preparePreparedStatements();
        //when
        boolean persistedDocument = cassandra.persistDocument(fullDocumentPath, file, "metadata", "transformations");
        //then
        assertTrue(persistedDocument);
    }

    @Test(expected = DocumentAlreadyExistsException.class)
    public void persistDocumentThrowsDocumentAlreadyExistsExceptionTest() throws IOException, DocumentAlreadyExistsException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        MultipartFile file = mock(MultipartFile.class);
        when(mongoDB.insertPrimaryKey(any(FullDocumentPath.class))).thenReturn(false);
        //when
        cassandra.persistDocument(fullDocumentPath, file, "metadata", "transformations");
    }

    private void preparePreparedStatements() {
        PreparedStatement insertMetadataStatement = mock(PreparedStatement.class);
        PreparedStatement insertContentStatement = mock(PreparedStatement.class);
        PreparedStatement deleteStatement = mock(PreparedStatement.class);
        PreparedStatement updateStataement = mock(PreparedStatement.class);
        PreparedStatement getContentStatement = mock(PreparedStatement.class);
        when(session.prepare(anyString()))
                .thenReturn(insertMetadataStatement)
                .thenReturn(insertContentStatement)
                .thenReturn(deleteStatement)
                .thenReturn(updateStataement)
                .thenReturn(getContentStatement);
        cassandra.prepareStatements();
    }
}

