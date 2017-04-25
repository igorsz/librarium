package com.librarium.eventhandler.search;

import com.librarium.common.event.Event;
import com.librarium.eventhandler.search.exceptions.NotRecognizedEventTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 06.02.2017.
 */

@Component
public class ElasticEventDispatcher {

    private ElasticUpdater elasticUpdater;

    @Autowired
    public ElasticEventDispatcher(ElasticUpdater elasticUpdater) {
        this.elasticUpdater = elasticUpdater;
    }

    public void handleEvent(Event event) throws NotRecognizedEventTypeException {
        switch (event.getEventType()) {
            case CREATE: case MODIFY:
                elasticUpdater.upsertDocument(event);
                break;
            case DELETE:
                elasticUpdater.deleteDocument(event);
                break;
            default:
                throw new NotRecognizedEventTypeException(event.getEventType().name());
        }
    }
}
