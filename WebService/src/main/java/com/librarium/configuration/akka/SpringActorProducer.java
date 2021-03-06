package com.librarium.configuration.akka;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * Created by Igor on 01.02.2017.
 */
public class SpringActorProducer implements IndirectActorProducer {

    private ApplicationContext applicationContext;
    private String beanActorName;

    public SpringActorProducer(ApplicationContext applicationContext, String beanActorName) {
        this.applicationContext = applicationContext;
        this.beanActorName = beanActorName;
    }

    public Actor produce() {
        return (Actor) applicationContext.getBean(beanActorName);
    }

    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(beanActorName);
    }
}
