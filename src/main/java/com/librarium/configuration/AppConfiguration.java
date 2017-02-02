package com.librarium.configuration;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import static com.librarium.healthcheck.AkkaSpringExtension.AKKA_SPRING_EXTENSION_PROVIDER;

/**
 * Created by Igor on 01.02.2017.
 */

@org.springframework.context.annotation.Configuration
@ComponentScan
public class AppConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("health-check-system");
        AKKA_SPRING_EXTENSION_PROVIDER.get(system).initialize(applicationContext);
        return system;
    }
}
