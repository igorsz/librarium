package com.librarium.healthcheck.monitors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.librarium.healthcheck.messages.EventHandlerHealthResponse;
import com.librarium.healthcheck.messages.HealthCheckRequest;
import com.librarium.healthcheck.messages.HealthStatus;
import com.librarium.healthcheck.messages.MongoHealthResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Igor on 08.02.2017.
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EventHandlerHealthMonitor extends UntypedActor{

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) throws Throwable {
        if (message instanceof HealthCheckRequest) {
            log.info("Received health check request");
            getSender().tell(new EventHandlerHealthResponse(verifyEventHandlerConnection()), getSelf());
        } else
            unhandled(message);
    }

    private HealthStatus verifyEventHandlerConnection() {
        try{
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForEntity("http://localhost:8081/management/_health", String.class);
            return HealthStatus.GREEN;
        } catch (Exception e){
            return HealthStatus.RED;
        }
    }
}
