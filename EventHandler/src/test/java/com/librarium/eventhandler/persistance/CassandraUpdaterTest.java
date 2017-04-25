package com.librarium.eventhandler.persistance;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.google.gson.JsonObject;
import com.librarium.common.event.Event;
import com.librarium.common.event.EventType;
import com.librarium.common.event.FullDocumentPath;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
public class CassandraUpdaterTest {

    private CassandraUpdater cassandraUpdater;
    private Session session;

    @Before
    public void setUp() {
        session = mock(Session.class);
        cassandraUpdater = new CassandraUpdater(session);
    }

    @Test
    public void updateMetadataTest() {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        JsonObject metadata = new JsonObject();
        metadata.addProperty("key", "value");
        JsonObject transformations = new JsonObject();
        Event event = new Event(EventType.CREATE, fullDocumentPath, metadata, transformations);
        preparePreparedStatements();
        //when
        boolean updatedMetadata = cassandraUpdater.updateMetadata(event);
        //then
        assertTrue(updatedMetadata);
    }

    @Test
    public void updateMetadataFailedTest() {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        JsonObject metadata = new JsonObject();
        metadata.addProperty("key", "value");
        JsonObject transformations = new JsonObject();
        Event event = new Event(EventType.CREATE, fullDocumentPath, metadata, transformations);
        preparePreparedStatements();
        when(session.execute(any(BoundStatement.class))).thenThrow(Exception.class);
        //when
        boolean updatedMetadata = cassandraUpdater.updateMetadata(event);
        //then
        assertFalse(updatedMetadata);
    }

    private void preparePreparedStatements() {
        PreparedStatement insertMetadataStatement = mock(PreparedStatement.class);
        when(session.prepare(anyString()))
                .thenReturn(insertMetadataStatement);
        cassandraUpdater.prepareStatements();
    }
}
