package com.librarium.configuration.akka;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;

/**
 * Created by Igor on 01.02.2017.
 */
public class AkkaSpringExtension extends AbstractExtensionId<AkkaSpringExtension.AkkaSpringExt> {

    public static final AkkaSpringExtension AkkaSpringExtentionProvider = new AkkaSpringExtension();

    public AkkaSpringExt createExtension(ExtendedActorSystem extendedActorSystem) {
        return new AkkaSpringExt();
    }

    public static class AkkaSpringExt implements Extension {
        private volatile ApplicationContext applicationContext;

        public void initialize(ApplicationContext applicationContext){
            this.applicationContext = applicationContext;
        }

        public Props props(String actorBeanName) {
            return Props.create(SpringActorProducer.class, applicationContext, actorBeanName);
        }
    }
}
