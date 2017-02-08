package com.librarium.healthcheck.monitors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.librarium.healthcheck.messages.HealthCheckRequest;
import com.librarium.healthcheck.messages.HealthStatus;
import com.librarium.healthcheck.messages.MongoHealthResponse;
import com.librarium.persistance.MongoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 08.02.2017.
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MongoHealthMonitor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Autowired
    MongoDB mongoDB;

    public void onReceive(Object message) throws Throwable {
        if (message instanceof HealthCheckRequest) {
            log.info("Received health check request");
            getSender().tell(new MongoHealthResponse(verifyMongoConnection()), getSelf());
        } else
            unhandled(message);
    }

    private HealthStatus verifyMongoConnection() {
        return mongoDB.performHealthCheck();
    }
}
