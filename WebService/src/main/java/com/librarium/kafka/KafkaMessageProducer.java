package com.librarium.kafka;

import com.google.gson.Gson;
import com.librarium.configuration.Configuration;
import com.librarium.event.Event;
import com.librarium.event.EventType;
import com.librarium.event.FullDocumentPath;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Properties;

/**
 * Created by Igor on 13.12.2016.
 */
@Component
public class KafkaMessageProducer {

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

    public void produceTest() {

//        for (int i = 0; i < 10; i++) {
//            producer.send(new ProducerRecord<String, String>(topicName,
//                    Integer.toString(i), "yo, testujemy producenta: " + Integer.toString(i)));
//            System.out.println("Message sent successfully");
//        }
//        producer.close();
    }

    public void createDocument(FullDocumentPath fullDocumentPath, String metadata, String transformations) {
        Event event = new Event(EventType.CREATE, fullDocumentPath, metadata, transformations);
        producer.send(new ProducerRecord<String, String>(topicName, gson.toJson(event)));
    }

    public void deleteDocument(FullDocumentPath fullDocumentPath) {
        Event event = new Event(EventType.DELETE, fullDocumentPath, null, null);
        producer.send(new ProducerRecord<String, String>(topicName, gson.toJson(event)));
    }

    public void updateDocument(FullDocumentPath fullDocumentPath) {
        Event event = new Event(EventType.MODIFY, fullDocumentPath, null, null);
        producer.send(new ProducerRecord<String, String>(topicName, gson.toJson(event)));
    }
}