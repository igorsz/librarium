package com.librarium.healthcheck;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 31.01.2017.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KafkaHealthMonitor extends UntypedActor{

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) throws Throwable {
        if (message instanceof String) {
            log.info("Received String message: {}", message);
            getSender().tell(message, getSelf());
        } else
            unhandled(message);
    }
}
