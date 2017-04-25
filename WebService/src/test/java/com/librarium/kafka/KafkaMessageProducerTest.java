package com.librarium.kafka;

import com.google.gson.JsonObject;
import com.librarium.common.event.Event;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class KafkaMessageProducerTest {

    private Configuration configuration;
    private KafkaMessageProducer kafkaMessageProducer;

    @Before
    public void setUp() {
        kafkaMessageProducer = mock(KafkaMessageProducer.class);
    }

    @Test
    public void createDocumentTest() {
        //given
        when(kafkaMessageProducer.createDocument(any(FullDocumentPath.class),any(JsonObject.class),any(JsonObject.class))).thenCallRealMethod();
        when(kafkaMessageProducer.sendEventToKafka(any(Event.class))).thenReturn(true);
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        JsonObject metadata = new JsonObject();
        metadata.addProperty("key", "value");
        JsonObject transformations = new JsonObject();
        // when
        boolean documentSent = kafkaMessageProducer.createDocument(fullDocumentPath, metadata, transformations);
        //then
        assertTrue(documentSent);
    }

    @Test
    public void updateDocumentTest() {
        //given
        when(kafkaMessageProducer.updateDocument(any(FullDocumentPath.class),any(JsonObject.class))).thenCallRealMethod();
        when(kafkaMessageProducer.sendEventToKafka(any(Event.class))).thenReturn(true);
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        JsonObject metadata = new JsonObject();
        metadata.addProperty("key", "value");
        // when
        boolean documentSent = kafkaMessageProducer.updateDocument(fullDocumentPath, metadata);
        //then
        assertTrue(documentSent);
    }

    @Test
    public void deleteDocumentTest() {
        //given
        when(kafkaMessageProducer.deleteDocument(any(FullDocumentPath.class))).thenCallRealMethod();
        when(kafkaMessageProducer.sendEventToKafka(any(Event.class))).thenReturn(true);
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        // when
        boolean documentSent = kafkaMessageProducer.deleteDocument(fullDocumentPath);
        //then
        assertTrue(documentSent);
    }

}
