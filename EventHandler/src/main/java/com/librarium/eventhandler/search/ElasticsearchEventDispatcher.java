package com.librarium.eventhandler.search;

import com.librarium.common.event.Event;
import com.librarium.eventhandler.search.exceptions.NotRecognizedEventTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 06.02.2017.
 */

@Component
public class ElasticsearchEventDispatcher {

    private Elasticsearch elasticsearch;

    @Autowired
    public ElasticsearchEventDispatcher(Elasticsearch elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    public void handleEvent(Event event) throws NotRecognizedEventTypeException {
        switch (event.getEventType()) {
            case CREATE: case MODIFY:
                elasticsearch.upsertDocument(event);
                break;
            case DELETE:
                elasticsearch.deleteDocument(event);
                break;
            default:
                throw new NotRecognizedEventTypeException(event.getEventType().name());
        }
    }
}
