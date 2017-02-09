package com.librarium.persistance;

import com.datastax.driver.core.*;
import com.librarium.configuration.Configuration;
import com.librarium.healthcheck.HealthCheck;
import com.librarium.healthcheck.messages.HealthStatus;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import com.librarium.event.FullDocumentPath;
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
public class Cassandra implements Persistance, HealthCheck{

    private static final Logger logger = LogManager.getLogger(Cassandra.class);


    private Configuration configuration;
    private Session session;
    private Cluster cluster;
    private String APPLIED = "[applied]";
    private MongoDB mongoDB;
    private PreparedStatement insertMetadataStatement;
    private PreparedStatement insertContentStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement updateStataement;
    private PreparedStatement getContentStatement;

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

    private void prepareStatements() {
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

    public void persistDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException, DocumentAlreadyExistsException {
        if(!mongoDB.insertPrimaryKey(fullDocumentPath))
            throw new DocumentAlreadyExistsException(fullDocumentPath);
        persistDocumentContent(fullDocumentPath, file);

        BoundStatement bind = insertMetadataStatement.bind(fullDocumentPath.getFullPath(), metadata, transformations);
        executeBind(bind);
    }

    private void executeBind(BoundStatement bind) {
        session.execute(bind);
    }

    private void persistDocumentContent(FullDocumentPath fullDocumentPath, MultipartFile file) throws IOException, DocumentAlreadyExistsException {
        BoundStatement bind = insertContentStatement.bind(fullDocumentPath.getFullPath(), new String(file.getBytes()));
        executeBind(bind);
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException {
        if(!mongoDB.deletePrimaryKey(fullDocumentPath))
            throw new DocumentNotExistsException(fullDocumentPath);
        BoundStatement bind = deleteStatement.bind(fullDocumentPath.getFullPath());
        executeBind(bind);
    }

    public void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException {
        if(!mongoDB.primaryKeyExists(fullDocumentPath))
            throw new DocumentNotExistsException(fullDocumentPath);
        BoundStatement bind = updateStataement.bind(metadata, fullDocumentPath.getFullPath());
        executeBind(bind);
    }

    public HealthStatus performHealthCheck() {
        try {
            session.execute("select cluster_name, release_version from system.local").one();
            return HealthStatus.GREEN;
        } catch (Exception e){
            logger.error("Cassandra health status returned RED");
            return HealthStatus.RED;
        }
    }

    public void getDocument(FullDocumentPath fullDocumentPath, OutputStream outputStream) throws DocumentNotExistsException, IOException {
        if(!mongoDB.primaryKeyExists(fullDocumentPath))
            throw new DocumentNotExistsException(fullDocumentPath);
        BoundStatement bind = getContentStatement.bind(fullDocumentPath.getFullPath());
        ResultSet execute = session.execute(bind);
        outputStream.write(execute.one().get(0,String.class).getBytes());
    }
}
