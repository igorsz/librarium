package com.librarium.eventhandler.transformations;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 04.02.2017.
 */
@Component
public class Transformer {

    private Configuration configuration;
    private List<Transformation> transformationList;
    private JsonParser parser;

    @Autowired
    public Transformer(Configuration configuration) {
        this.configuration = configuration;
        this.parser = new JsonParser();
        transformationList = new ArrayList<Transformation>();
    }

    void subscribe(Transformation transformationClass) {
        transformationList.add(transformationClass);
    }

    public void performTransformations(Event event) {
        JsonObject jo = (JsonObject) parser.parse(event.getTransformations());
        JsonArray elements = jo.getAsJsonArray("transformations");
        ArrayList requestedTransformations = new Gson().fromJson(elements, ArrayList.class);

        for (Transformation transformation : transformationList) {
            if (transformation.transformationEligible(requestedTransformations))
                event = transformation.transform(event);
        }
    }
}
