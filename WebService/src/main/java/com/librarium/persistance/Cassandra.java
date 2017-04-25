package com.librarium.persistance;

import com.datastax.driver.core.*;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.configuration.Configuration;
import com.librarium.healthcheck.HealthCheck;
import com.librarium.healthcheck.messages.HealthStatus;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Igor on 01.02.2017.
 */

@Component
public class Cassandra implements Persistance, HealthCheck {

    private static final Logger logger = LogManager.getLogger(Cassandra.class);


    private Configuration configuration;
    private Session session;
    private Cluster cluster;
    private String APPLIED = "[applied]";
    private MongoDB mongoDB;
    PreparedStatement insertMetadataStatement;
    PreparedStatement insertContentStatement;
    PreparedStatement deleteStatement;
    PreparedStatement updateStataement;
    PreparedStatement getContentStatement;

    Cassandra(Session session, MongoDB mongoDB) {
        this.session = session;
        this.mongoDB = mongoDB;
    }

    @Autowired
    public Cassandra(Configuration configuration, MongoDB mongoDB) {
        this.configuration = configuration;
        this.mongoDB = mongoDB;
        String host = configuration.getCassandraConfiguration().getHost();
        String keyspace = configuration.getCassandraConfiguration().getKeySpace();

        this.cluster = Cluster.builder()
                .addContactPoint(host)
                .build();
        this.session = cluster.connect(keyspace);
        prepareStatements();
    }


    void prepareStatements() {
        insertMetadataStatement = session.prepare(
                "INSERT INTO metadata (key, metadata, transformation)" +
                        "VALUES (?, ?, ?)");
        insertContentStatement = session.prepare(
                "INSERT INTO content (key, content)" +
                        "VALUES (?, ?)");
        deleteStatement = session.prepare(
                "DELETE FROM metadata WHERE key=?");
        updateStataement = session.prepare(
                "UPDATE metadata SET metadata = ? WHERE key = ?");
        getContentStatement = session.prepare(
                "SELECT content FROM content WHERE key = ?");
    }

    public boolean persistDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException, DocumentAlreadyExistsException {
        if (!mongoDB.insertPrimaryKey(fullDocumentPath))
            throw new DocumentAlreadyExistsException(fullDocumentPath);
        persistDocumentContent(fullDocumentPath, file);

        BoundStatement bind = insertMetadataStatement.bind(fullDocumentPath.getFullPath(), metadata, transformations);
        return executeStatement(bind);
    }

    boolean executeStatement(BoundStatement statement) {
        try {
            session.execute(statement);
            return true;
        } catch (Exception e) {
            logger.error("Exception caught while executing statement: {}", statement);
            return false;
        }
    }

    boolean persistDocumentContent(FullDocumentPath fullDocumentPath, MultipartFile file) throws IOException, DocumentAlreadyExistsException {
        BoundStatement bind = insertContentStatement.bind(fullDocumentPath.getFullPath(), new String(file.getBytes()));
        return executeStatement(bind);
    }

    public boolean deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException {
        if (!mongoDB.deletePrimaryKey(fullDocumentPath))
            throw new DocumentNotExistsException(fullDocumentPath);
        BoundStatement bind = deleteStatement.bind(fullDocumentPath.getFullPath());
        return executeStatement(bind);
    }

    public boolean updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException {
        if (!mongoDB.primaryKeyExists(fullDocumentPath))
            throw new DocumentNotExistsException(fullDocumentPath);
        BoundStatement bind = updateStataement.bind(metadata, fullDocumentPath.getFullPath());
        return executeStatement(bind);
    }

    public HealthStatus performHealthCheck() {
        try {
            //getting one result is enough to assume that connection is green
            session.execute("select cluster_name, release_version from system.local").one();
            return HealthStatus.GREEN;
        } catch (Exception e) {
            logger.error("Cassandra health status returned RED");
            return HealthStatus.RED;
        }
    }

    public void getDocument(FullDocumentPath fullDocumentPath, OutputStream outputStream) throws DocumentNotExistsException, IOException {
        if (!mongoDB.primaryKeyExists(fullDocumentPath))
            throw new DocumentNotExistsException(fullDocumentPath);
        BoundStatement bind = getContentStatement.bind(fullDocumentPath.getFullPath());
        ResultSet execute = session.execute(bind);
        outputStream.write(execute.one().get(0, String.class).getBytes());
    }
}
