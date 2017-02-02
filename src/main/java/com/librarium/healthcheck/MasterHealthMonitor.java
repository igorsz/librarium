package com.librarium.healthcheck;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static com.librarium.configuration.akka.AkkaSpringExtension.AkkaSpringExtentionProvider;

/**
 * Created by Igor on 31.01.2017.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MasterHealthMonitor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorSystem system;
    private ActorRef kafkaMonitor;

    public MasterHealthMonitor(ActorSystem system) {
        this.system = system;
        log.info("Created Master Health Monitor");
        kafkaMonitor = system.actorOf(AkkaSpringExtentionProvider.get(system).props("kafkaHealthMonitor"), "KafkaHealthMonitor");
        system.scheduler().schedule(Duration.Zero(), Duration.create(2, TimeUnit.SECONDS), kafkaMonitor, "test", system.dispatcher(), null);
    }

    public void onReceive(Object message) throws Throwable {
        log.info(String.valueOf(system));
        if (message instanceof Greet) {
//            log.info("Received String message: {}", message);
            getSender().tell("success arrived", getSelf());
        } else
            unhandled(message);
    }

    public static class Greet {
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
