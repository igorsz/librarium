package com.librarium.kafka;

import com.librarium.search.FullDocumentPath;
import lombok.Value;

/**
 * Created by Igor on 04.01.2017.
 */

@Value
public class Event {
    EventType eventType;
    FullDocumentPath fullDocumentPath;
    String metadata;
    String transformations;

}
