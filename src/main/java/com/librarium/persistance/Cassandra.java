package com.librarium.persistance;

import com.datastax.driver.core.*;
import com.librarium.configuration.Configuration;
import com.librarium.search.FullDocumentPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Igor on 01.02.2017.
 */

@Component
public class Cassandra implements Persistance{

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

    public void persistDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException, DocumentAlreadyExistsException {
        persistDocumentContent(fullDocumentPath, file);
        PreparedStatement statement = session.prepare(
                "INSERT INTO metadata (key, metadata, transformation)" +
                "VALUES (?, ?, ?)" +
                "IF NOT EXISTS");
        BoundStatement bind = statement.bind(fullDocumentPath.getFullPath(), metadata, transformations);
        ResultSet resultSet = session.execute(bind);
        if(!resultSet.one().getBool(APPLIED))
            throw new DocumentAlreadyExistsException(fullDocumentPath);
    }

    private void persistDocumentContent(FullDocumentPath fullDocumentPath, MultipartFile file) throws IOException, DocumentAlreadyExistsException {
        PreparedStatement statement = session.prepare(
                "INSERT INTO content (key, content)" +
                        "VALUES (?, ?)" +
                        "IF NOT EXISTS");
        BoundStatement bind = statement.bind(fullDocumentPath.getFullPath(), new String(file.getBytes()));
        ResultSet resultSet = session.execute(bind);
        if(!resultSet.one().getBool(APPLIED))
            throw new DocumentAlreadyExistsException(fullDocumentPath);
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException {
        PreparedStatement statement = session.prepare(
                "DELETE FROM metadata WHERE key=?" +
                "IF EXISTS");
        BoundStatement bind = statement.bind(fullDocumentPath.getFullPath());
        ResultSet resultSet = session.execute(bind);
        if(!resultSet.one().getBool(APPLIED))
            throw new DocumentNotExistsException(fullDocumentPath);
    }

    public void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException {
        PreparedStatement statement = session.prepare(
                "UPDATE metadata SET metadata = ? WHERE key = ?" +
                "IF EXISTS");
        BoundStatement bind = statement.bind(metadata, fullDocumentPath.getFullPath());
        ResultSet resultSet = session.execute(bind);
        if(!resultSet.one().getBool(APPLIED))
            throw new DocumentNotExistsException(fullDocumentPath);
    }
}
