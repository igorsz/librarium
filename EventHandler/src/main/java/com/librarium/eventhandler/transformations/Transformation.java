package com.librarium.eventhandler.transformations;

import com.librarium.eventhandler.event.Event;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Igor on 04.02.2017.
 */
interface Transformation {
    Event transform(Event event);
    boolean transformationEligible(List<String> requestedTransformations);
}
