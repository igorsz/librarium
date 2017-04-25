package com.librarium.eventhandler.persistance;

import com.datastax.driver.core.*;
import com.librarium.common.event.Event;
import com.librarium.eventhandler.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by Igor on 01.02.2017.
 */

@Component
public class CassandraUpdater {

    private static final Logger logger = LogManager.getLogger(CassandraUpdater.class);

    private Configuration configuration;
    private Session session;
    private Cluster cluster;
    PreparedStatement statement;

    @Autowired
    public CassandraUpdater(Configuration configuration) {
        this.configuration = configuration;
        String host = configuration.getCassandraConfiguration().getHost();
        String keyspace = configuration.getCassandraConfiguration().getKeySpace();

        this.cluster = Cluster.builder()
                .addContactPoint(host)
                .build();
        this.session = cluster.connect(keyspace);
        prepareStatements();
    }

    public CassandraUpdater(Session session) {
        this.session = session;
    }

    @PreDestroy
    public void cleanUp() {
        cluster.close();
    }

    void prepareStatements() {
         statement = session.prepare(
                "UPDATE metadata SET metadata = ? WHERE key = ?");
    }

    public boolean updateMetadata(Event event) {
        BoundStatement bind = statement.bind(event.getMetadata().toString(), event.getFullDocumentPath().getFullPath());
        try {
            session.execute(bind);
            return true;
        } catch (Exception e) {
            logger.error("Exception caught while executing statement: {}", bind);
            return false;
        }
    }
}
