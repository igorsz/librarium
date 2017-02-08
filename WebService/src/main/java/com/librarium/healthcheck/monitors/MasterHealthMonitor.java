package com.librarium.healthcheck.monitors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.librarium.healthcheck.ApplicationHealthStatus;
import com.librarium.healthcheck.messages.CassandraHealthResponse;
import com.librarium.healthcheck.messages.EventHandlerHealthResponse;
import com.librarium.healthcheck.messages.HealthCheckRequest;
import com.librarium.healthcheck.messages.MongoHealthResponse;
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
    private ActorRef cassandra;
    private ActorRef mongo;
    private ActorRef eventHandler;
    private ApplicationHealthStatus applicationHealthStatus;

    public MasterHealthMonitor(ActorSystem system) {
        this.system = system;
        this.applicationHealthStatus = new ApplicationHealthStatus();
        log.info("Created Master Health Monitor");
        cassandra = system.actorOf(AkkaSpringExtentionProvider.get(system).props("cassandraHealthMonitor"), "CassandraHealthMonitor");
        mongo = system.actorOf(AkkaSpringExtentionProvider.get(system).props("mongoHealthMonitor"), "MongoHealthMonitor");
        eventHandler = system.actorOf(AkkaSpringExtentionProvider.get(system).props("eventHandlerHealthMonitor"), "EventHandlerHealthMonitor");
        system.scheduler().schedule(Duration.Zero(), Duration.create(20, TimeUnit.SECONDS), cassandra, new HealthCheckRequest(), system.dispatcher(), getSelf());
        system.scheduler().schedule(Duration.Zero(), Duration.create(20, TimeUnit.SECONDS), mongo, new HealthCheckRequest(), system.dispatcher(), getSelf());
        system.scheduler().schedule(Duration.Zero(), Duration.create(20, TimeUnit.SECONDS), eventHandler, new HealthCheckRequest(), system.dispatcher(), getSelf());
    }

    public void onReceive(Object message) throws Throwable {
        log.info(String.valueOf(system));
        if (message instanceof CassandraHealthResponse) {
            applicationHealthStatus.setCassandraHealthStatus(((CassandraHealthResponse)message).getStatus());
        } else if(message instanceof MongoHealthResponse){
            applicationHealthStatus.setMongoHealthStatus(((MongoHealthResponse)message).getStatus());
        } else if(message instanceof EventHandlerHealthResponse){
            applicationHealthStatus.setEventHandlerHealthStatus(((EventHandlerHealthResponse)message).getStatus());
        } else if(message instanceof HealthCheckRequest){
            getSender().tell(applicationHealthStatus,getSelf());
        } else
            unhandled(message);
    }

}
