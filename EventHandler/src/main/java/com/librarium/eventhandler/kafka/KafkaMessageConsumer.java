package com.librarium.eventhandler.kafka;

import com.google.gson.Gson;
import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.event.Event;
import com.librarium.eventhandler.event.EventType;
import com.librarium.eventhandler.search.Elasticsearch;
import com.librarium.eventhandler.search.ElasticsearchEventDispatcher;
import com.librarium.eventhandler.search.exceptions.NotRecognizedEventTypeException;
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
    private ElasticsearchEventDispatcher elasticsearchEventDispatcher;
    private KafkaConsumer<String, String> consumer;
    private String topicName = "test";
    private Gson gson;

    @Autowired
    public KafkaMessageConsumer(Configuration configuration, Transformer transformer, ElasticsearchEventDispatcher elasticsearchEventDispatcher) throws NotRecognizedEventTypeException {
        this.configuration = configuration;
        this.transformer = transformer;
        this.elasticsearchEventDispatcher = elasticsearchEventDispatcher;
        this.gson = new Gson();
        Properties properties = prepareKafkaProperties(configuration);
        this.consumer = new KafkaConsumer<String, String>(properties);
        this.consumer.subscribe(Arrays.asList(topicName));
        consumeEvent();
    }

    private Properties prepareKafkaProperties(Configuration configuration) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", configuration.getKafkaConfiguration().getBootstrapServers());
        properties.put("group.id", configuration.getKafkaConfiguration().getGroupId());
        properties.put("key.deserializer", configuration.getKafkaConfiguration().getKeyDeserializer());
        properties.put("value.deserializer", configuration.getKafkaConfiguration().getValueDeserializer());
        properties.put("session.timeout.ms", configuration.getKafkaConfiguration().getSessionTimeout());
        return properties;
    }

    private void consumeEvent() throws NotRecognizedEventTypeException {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.offset() + ": " + record.value());
                    Event event = deserializeEvent(record.value());
                    if (event.getEventType().equals(EventType.CREATE))
                        event = transformer.performTransformations(event);
                    elasticsearchEventDispatcher.handleEvent(event);
                    consumer.commitSync(Collections.singletonMap(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1)));
                }
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
