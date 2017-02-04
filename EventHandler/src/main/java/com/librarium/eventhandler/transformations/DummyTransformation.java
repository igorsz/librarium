package com.librarium.eventhandler.transformations;

import com.librarium.eventhandler.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Igor on 04.02.2017.
 */
@Component
@Qualifier("dummy")
public class DummyTransformation implements Transformation {

    private final String TRANSFORMATION_NAME = "dummy";
    private Transformer transformer;

    @Autowired
    public DummyTransformation(Transformer transformer) {
        this.transformer = transformer;
        transformer.subscribe(this);
    }

    public DummyTransformation(){
        System.out.println("YOOOOOOOOOOOOOO");
    }

    public Event transform(Event event) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return event;
    }

    public boolean transformationEligible(List<String> requestedTransformations) {
        return requestedTransformations.contains(TRANSFORMATION_NAME);
    }

}
