package com.librarium.healthcheck;

import com.librarium.healthcheck.messages.HealthStatus;
import lombok.Data;

/**
 * Created by Igor on 08.02.2017.
 */
@Data
public class ApplicationHealthStatus {

    private HealthStatus cassandraHealthStatus;
    private HealthStatus mongoHealthStatus;
    private HealthStatus eventHandlerHealthStatus;

}
