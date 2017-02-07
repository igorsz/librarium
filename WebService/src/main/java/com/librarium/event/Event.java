package com.librarium.event;

import com.google.gson.JsonObject;
import lombok.Value;

/**
 * Created by Igor on 04.01.2017.
 */

@Value
public class Event {
    EventType eventType;
    FullDocumentPath fullDocumentPath;
    JsonObject metadata;
    JsonObject transformations;
}
