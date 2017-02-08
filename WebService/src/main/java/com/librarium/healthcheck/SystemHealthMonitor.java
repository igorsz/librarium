package com.librarium.healthcheck;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.librarium.search.Elasticsearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static com.librarium.configuration.akka.AkkaSpringExtension.AkkaSpringExtentionProvider;

/**
 * Created by Igor on 31.01.2017.
 */
@Component
public class SystemHealthMonitor {

    private static final Logger logger = LogManager.getLogger(Elasticsearch.class);

    @Autowired
    Elasticsearch elasticsearch;

    @Autowired
    private ActorSystem system;

    private ActorRef master;

    @PostConstruct
    public void init() {
        logger.info("HEY");
        master = system.actorOf(AkkaSpringExtentionProvider.get(system).props("masterHealthMonitor"), "MasterHealthMonitor");

        FiniteDuration duration = FiniteDuration.create(30, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        Future<Object> result = ask(master, new String("John"), timeout);
        try {
            Object result1 = Await.result(result, duration);
            logger.info("received: {}", result1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanUp() {
        system.terminate();
    }
}
