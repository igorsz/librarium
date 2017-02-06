package com.librarium.configuration.akka;

import akka.actor.ActorSystem;
import com.librarium.persistance.Cassandra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import static com.librarium.configuration.akka.AkkaSpringExtension.AkkaSpringExtentionProvider;

/**
 * Created by Igor on 01.02.2017.
 */

@org.springframework.context.annotation.Configuration (value = "appConfiguration")
@ComponentScan
public class AppConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("health-check-system");
        AkkaSpringExtentionProvider.get(system).initialize(applicationContext);
        return system;
    }
}
