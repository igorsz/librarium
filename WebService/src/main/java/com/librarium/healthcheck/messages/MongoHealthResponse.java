package com.librarium.healthcheck.messages;

/**
 * Created by Igor on 08.02.2017.
 */
public class MongoHealthResponse {

    private HealthStatus status;

    public HealthStatus getStatus() {
        return status;
    }

    public MongoHealthResponse(HealthStatus status) {

        this.status = status;
    }
}
