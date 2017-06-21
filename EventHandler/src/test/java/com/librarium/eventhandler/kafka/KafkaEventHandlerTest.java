package com.librarium.eventhandler.kafka;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.librarium.common.event.Event;
import com.librarium.common.event.EventType;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.eventhandler.search.ElasticEventDispatcher;
import com.librarium.eventhandler.search.exceptions.NotRecognizedEventTypeException;
import com.librarium.eventhandler.transformations.Transformer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
public class KafkaEventHandlerTest {

    private KafkaEventHandler kafkaEventHandler;
    private Transformer transformer;
    private Gson gson;
    private ElasticEventDispatcher elasticEventDispatcher;
    private KafkaConsumer<String, String> kafkaConsumer;

    @Before
    public void setUp() {
        transformer = mock(Transformer.class);
        gson = new Gson();
        elasticEventDispatcher = mock(ElasticEventDispatcher.class);
        kafkaConsumer = mock(KafkaConsumer.class);
        kafkaEventHandler = new KafkaEventHandler(transformer, gson, elasticEventDispatcher, kafkaConsumer);
    }

    @Test
    public void deserializeEventTest() {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        JsonObject metadata = new JsonObject();
        metadata.addProperty("key", "value");
        JsonObject transformations = new JsonObject();
        //when
        Event deserializedEvent = kafkaEventHandler.deserializeEvent(getSerializedEvent());
        //then
        assertEquals(EventType.CREATE, deserializedEvent.getEventType());
        assertEquals(fullDocumentPath, deserializedEvent.getFullDocumentPath());
        assertEquals(metadata, deserializedEvent.getMetadata());
        assertEquals(transformations, deserializedEvent.getTransformations());
    }

    private String getSerializedEvent() {
        return "{\"eventType\":\"CREATE\",\"fullDocumentPath\":{\"fullPath\"" +
                ":\"index/type/id\", \"index\":{\"index\":\"index\"}, \"type\":{\"type\":\"type\"}, \"documentId\":\"id\"}," +
                "\"metadata\":{\"key\":\"value\"},\"transformations\":{}}";
    }

    @Test
    public void handleEventTest() throws NotRecognizedEventTypeException {
        //given
        ConsumerRecord<String, String> record = new ConsumerRecord<String, String>("topic", 0, 0L, "key", getSerializedEvent());
        //when
        kafkaEventHandler.handleEvent(record);
        //then
        verify(transformer).performTransformations(any(Event.class));
        verify(elasticEventDispatcher).handleEvent(any(Event.class));
        verify(kafkaConsumer).commitSync(anyMap());
    }
}
