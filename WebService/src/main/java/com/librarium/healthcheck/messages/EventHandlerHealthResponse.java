package com.librarium.healthcheck.messages;

/**
 * Created by Igor on 08.02.2017.
 */
public class EventHandlerHealthResponse {

    private HealthStatus status;

    public HealthStatus getStatus() {
        return status;
    }

    public EventHandlerHealthResponse(HealthStatus status) {

        this.status = status;
    }
}
