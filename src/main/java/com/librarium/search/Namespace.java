package com.librarium.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Igor on 11.12.2016.
 */
public class Namespace {

    private static final Logger logger = LogManager.getLogger(Namespace.class);
    private String namespace;

    public Namespace(String namespace) throws NamespaceNameException {
        if(!namespace.toLowerCase().equals(namespace)){
            logger.error("Namespace: {} is not lower case only",namespace);
            throw new NamespaceNameException(namespace);
        }
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }
}
