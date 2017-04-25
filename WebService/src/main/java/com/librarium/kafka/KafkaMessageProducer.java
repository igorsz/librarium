package com.librarium.kafka;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.librarium.common.event.Event;
import com.librarium.common.event.EventType;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.configuration.Configuration;
import com.librarium.search.Elasticsearch;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Igor on 13.12.2016.
 */
@Component
public class KafkaMessageProducer {

    private static final Logger logger = LogManager.getLogger(KafkaMessageProducer.class);
    private Configuration configuration;

    private KafkaProducer<String, String> producer;
    private String topicName = "test";
    private Gson gson;

    @Autowired
    public KafkaMessageProducer(Configuration configuration) {
        this.configuration = configuration;
        Properties properties = new Properties();
        properties.put("bootstrap.servers", configuration.getKafkaConfiguration().getBootstrapServers());
        properties.put("acks", configuration.getKafkaConfiguration().getAcks());
        properties.put("retries", configuration.getKafkaConfiguration().getRetries());
        properties.put("batch.size", configuration.getKafkaConfiguration().getBatchSize());
        properties.put("key.serializer", configuration.getKafkaConfiguration().getKeySerializer());
        properties.put("value.serializer", configuration.getKafkaConfiguration().getValueSerializer());
        producer = new KafkaProducer<String, String>(properties);
        this.gson = new Gson();
    }

    public boolean createDocument(FullDocumentPath fullDocumentPath, JsonObject metadata, JsonObject transformations) {
        Event event = new Event(EventType.CREATE, fullDocumentPath, metadata, transformations);
        return sendEventToKafka(event);
    }

    protected boolean sendEventToKafka(Event event) {
        try {
            Future<RecordMetadata> send = producer.send(new ProducerRecord<String, String>(topicName, gson.toJson(event)));
            send.get(1000, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            logger.error("Exception thrown while sending event: {} to Kafka", event);
            return false;
        }
    }

    public boolean deleteDocument(FullDocumentPath fullDocumentPath) {
        Event event = new Event(EventType.DELETE, fullDocumentPath, null, null);
        return sendEventToKafka(event);
    }

    public boolean updateDocument(FullDocumentPath fullDocumentPath, JsonObject metada) {
        Event event = new Event(EventType.MODIFY, fullDocumentPath, metada, null);
        return sendEventToKafka(event);
    }
}
