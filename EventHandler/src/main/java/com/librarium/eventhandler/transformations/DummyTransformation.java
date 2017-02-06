package com.librarium.eventhandler.transformations;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.librarium.eventhandler.event.Event;
import com.librarium.eventhandler.transformations.exceptions.NotMatchingTransformationNameException;

import java.util.List;

/**
 * Created by Igor on 04.02.2017.
 */
public class DummyTransformation implements Transformation {

    private final String TRANSFORMATION_NAME = "dummy";

    private Transformer transformer;

    public DummyTransformation(String transformationName, Transformer transformer) throws NotMatchingTransformationNameException {
        this.transformer = transformer;
        if (!transformationName.equals(TRANSFORMATION_NAME))
            throw new NotMatchingTransformationNameException(TRANSFORMATION_NAME);
        transformer.subscribe(TRANSFORMATION_NAME,this);
    }

    public Event transform(Event event) {
        String metadata = event.getMetadata();
        JsonParser parser = new JsonParser();
        JsonObject parsed = (JsonObject)parser.parse(metadata);
        parsed.addProperty("modified",true);
        String modifiedMetadata = parsed.toString();
        Event changedEvent = new Event(event.getEventType(),event.getFullDocumentPath(),modifiedMetadata,event.getTransformations());
        return changedEvent;
    }

    public boolean transformationEligible(List<String> requestedTransformations) {
        return requestedTransformations.contains(TRANSFORMATION_NAME);
    }

}
