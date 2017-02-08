package com.librarium.healthcheck;

import com.librarium.healthcheck.messages.HealthStatus;

/**
 * Created by Igor on 08.02.2017.
 */
public interface HealthCheck {

    HealthStatus performHealthCheck();
}
