package com.librarium.eventhandler.kafka;

import com.google.gson.Gson;
import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.event.Event;
import com.librarium.eventhandler.transformations.Transformer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by Igor on 04.02.2017.
 */
@Component
public class KafkaMessageConsumer {

    private Transformer transformer;
    private Configuration configuration;
    private KafkaConsumer<String, String> consumer;
    private String topicName = "test";
    private Gson gson;

    @Autowired
    public KafkaMessageConsumer(Configuration configuration, Transformer transformer) {
        this.configuration = configuration;
        this.transformer = transformer;
        this.gson = new Gson();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", configuration.getKafkaConfiguration().getBootstrapServers());
        properties.put("group.id", configuration.getKafkaConfiguration().getGroupId());
        properties.put("key.deserializer", configuration.getKafkaConfiguration().getKeyDeserializer());
        properties.put("value.deserializer", configuration.getKafkaConfiguration().getValueDeserializer());
        properties.put("session.timeout.ms", configuration.getKafkaConfiguration().getSessionTimeout());
        this.consumer = new KafkaConsumer<String, String>(properties);
        this.consumer.subscribe(Arrays.asList(topicName));
        consumeEvent();
    }

    public void consumeEvent() {
        int i = 100;
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.offset() + ": " + record.value());
                    Event event = deserializeEvent(record.value());
                    transformer.performTransformations(event);
                    consumer.commitSync(Collections.singletonMap(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1)));
                }
                i--;
            }
        } finally {
            consumer.close();
        }
    }

    private Event deserializeEvent(String serialized) {
        Event event = gson.fromJson(serialized, Event.class);
        return event;
    }
}
