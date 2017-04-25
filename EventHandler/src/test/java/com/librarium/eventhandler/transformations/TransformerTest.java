package com.librarium.eventhandler.transformations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.librarium.common.event.Event;
import com.librarium.common.event.EventType;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.eventhandler.persistance.CassandraUpdater;
import com.librarium.eventhandler.transformations.exceptions.FailedToCreateTransformationClassException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
public class TransformerTest {

    private Transformer transformer;
    private CassandraUpdater cassandraUpdater;

    @Before
    public void setUp() {
        cassandraUpdater = mock(CassandraUpdater.class);
        transformer = new Transformer(cassandraUpdater);
    }

    @Test
    public void subscribeTest() {
        //given
        Transformation transformation = new Transformation() {
            public Event transform(Event event) {
                return null;
            }

            public boolean transformationEligible(List<String> requestedTransformations) {
                return false;
            }
        };
        //when
        transformer.subscribe("key", transformation);
        //then
        assertEquals(1, transformer.transformationMap.size());
    }

    @Test
    public void prepareRequestedTransformationsMapTest() {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        JsonObject metadata = new JsonObject();
        JsonObject transformations = prepareTransformations();
        Event event = new Event(EventType.CREATE, fullDocumentPath, metadata, transformations);
        //when
        ArrayList arrayList = transformer.prepareRequestedTransformationsMap(event);
        //then
        assertEquals(2, arrayList.size());
    }

    @Test
    public void createTransformationClassesTest() throws FailedToCreateTransformationClassException {
        //given
        Map<String, String > transformationsMap = new HashMap<String, String>();
        transformationsMap.put("dummy", "com.librarium.eventhandler.transformations.DummyTransformation");
        //when
        transformer.createTransformationClasses(transformationsMap);
        //then
        assertEquals(1, transformer.transformationMap.size());
    }

    @Test
    public void performTransformationsTest() throws FailedToCreateTransformationClassException {
        //given
        FullDocumentPath fullDocumentPath = new FullDocumentPath("index", "type", "id");
        JsonObject metadata = new JsonObject();
        JsonObject transformations = new JsonObject();
        JsonArray transformationsBody = new JsonArray();
        JsonElement element = new JsonPrimitive("dummy");
        transformationsBody.add(element);
        transformations.add("transformations", transformationsBody);
        Event event = new Event(EventType.CREATE, fullDocumentPath, metadata, transformations);
        createTransformationClasses();
        when(cassandraUpdater.updateMetadata(any(Event.class))).thenReturn(true);
        //when
        Event transformedEvent = transformer.performTransformations(event);
        //then
        assertTrue(transformedEvent.getMetadata().has("modified"));
        assertTrue(transformedEvent.getMetadata().get("modified").getAsBoolean());
    }

    private JsonObject prepareTransformations() {
        JsonObject transformations = new JsonObject();
        JsonArray transformationsBody = new JsonArray();
        JsonElement trans1 = new JsonPrimitive("trans1");
        JsonElement trans2 = new JsonPrimitive("trans2");
        transformationsBody.add(trans1);
        transformationsBody.add(trans2);
        transformations.add("transformations", transformationsBody);
        return transformations;
    }

    private void createTransformationClasses() throws FailedToCreateTransformationClassException {
        Map<String, String > transformationsMap = new HashMap<String, String>();
        transformationsMap.put("dummy", "com.librarium.eventhandler.transformations.DummyTransformation");
        transformer.createTransformationClasses(transformationsMap);
    }
}
