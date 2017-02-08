package com.librarium.eventhandler.persistance;

import com.datastax.driver.core.*;
import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 01.02.2017.
 */

@Component
public class Cassandra {

    private Configuration configuration;
    private Session session;
    private Cluster cluster;
    private String APPLIED = "[applied]";

    @Autowired
    public Cassandra(Configuration configuration) {
        this.configuration = configuration;
        String host = configuration.getCassandraConfiguration().getHost();
        String keyspace = configuration.getCassandraConfiguration().getKeySpace();

        this.cluster = Cluster.builder()
                .addContactPoint(host)
                .build();
        this.session = cluster.connect(keyspace);
    }

    public void updateMetadata(Event event) {
        PreparedStatement statement = session.prepare(
                "UPDATE metadata SET metadata = ? WHERE key = ?");
        BoundStatement bind = statement.bind(event.getMetadata().toString(), event.getFullDocumentPath().getFullPath());
        session.execute(bind);
    }
}