package com.librarium.eventhandler.transformations;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.librarium.common.event.Event;
import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.persistance.CassandraUpdater;
import com.librarium.eventhandler.transformations.exceptions.FailedToCreateTransformationClassException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Igor on 04.02.2017.
 */
@Component
public class Transformer {

    private Configuration configuration;
    Map<String, Transformation> transformationMap;
    private JsonParser parser;
    private CassandraUpdater cassandra;

    @Autowired
    public Transformer(Configuration configuration, CassandraUpdater cassandra) throws FailedToCreateTransformationClassException {
        this.configuration = configuration;
        this.parser = new JsonParser();
        this.cassandra = cassandra;
        transformationMap = new HashMap<String, Transformation>();
        Map<String, String> transformationClasses = configuration.getTransformationConfiguration().getTransformationClasses();
        createTransformationClasses(transformationClasses);
    }

    public Transformer(CassandraUpdater cassandra) {
        this.transformationMap = new HashMap<String, Transformation>();
        this.cassandra = cassandra;
    }

    void createTransformationClasses(Map<String, String> transformationClasses) throws FailedToCreateTransformationClassException {
        for (Map.Entry entry : transformationClasses.entrySet()) {
            try {
                Class.forName(String.valueOf(entry.getValue())).getConstructor(String.class, Transformer.class).newInstance(entry.getKey(), this);
            } catch (Exception e) {
                throw new FailedToCreateTransformationClassException(e);
            }
        }
    }

    void subscribe(String key, Transformation transformationClass) {
        transformationMap.put(key, transformationClass);
    }

    public Event performTransformations(Event event) {
        ArrayList requestedTransformations = prepareRequestedTransformationsMap(event);
        Event transformedEvent = event;
        for (Object requestedTransformation : requestedTransformations) {
            String req = (String) requestedTransformation;
            Transformation transformation = transformationMap.get(req);
            if (transformation == null) {
                handleWrongTransformationName();
            } else {
                transformedEvent = transformation.transform(transformedEvent);
                cassandra.updateMetadata(transformedEvent);
            }
        }
        return transformedEvent;
    }

    ArrayList prepareRequestedTransformationsMap(Event event) {
        JsonArray elements = event.getTransformations().getAsJsonArray("transformations");
        return new Gson().fromJson(elements, ArrayList.class);
    }

    private void handleWrongTransformationName() {
        //use external service to inform client about transformation failure
    }
}
