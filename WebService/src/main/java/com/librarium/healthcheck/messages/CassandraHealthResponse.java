package com.librarium.healthcheck.messages;

/**
 * Created by Igor on 08.02.2017.
 */
public class CassandraHealthResponse {

    private HealthStatus status;

    public CassandraHealthResponse(HealthStatus status) {
        this.status = status;
    }

    public HealthStatus getStatus() {
        return status;
    }
}
