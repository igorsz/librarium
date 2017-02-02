package com.librarium.healthcheck;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 31.01.2017.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MasterHealthMonitor extends UntypedActor{

//    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public MasterHealthMonitor() {
        System.out.println("YAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public void onReceive(Object message) throws Throwable {
        if (message instanceof Greet) {
//            log.info("Received String message: {}", message);
            getSender().tell(" DOTARLEM :D ", getSelf());
        } else
            unhandled(message);
    }

    public static class Greet{
        private String name;

        public Greet(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
