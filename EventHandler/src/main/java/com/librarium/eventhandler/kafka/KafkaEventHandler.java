package com.librarium.eventhandler.kafka;

import com.google.gson.Gson;
import com.librarium.common.configuration.TestConf;
import com.librarium.common.event.Event;
import com.librarium.common.event.EventType;
import com.librarium.eventhandler.configuration.Configuration;
import com.librarium.eventhandler.search.ElasticsearchEventDispatcher;
import com.librarium.eventhandler.search.exceptions.NotRecognizedEventTypeException;
import com.librarium.eventhandler.transformations.Transformer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Igor on 04.02.2017.
 */
@Component
public class KafkaEventHandler implements ApplicationRunner{

    private Transformer transformer;
    private Configuration configuration;
    private ElasticsearchEventDispatcher elasticsearchEventDispatcher;
    private KafkaConsumer<String, String> consumer;
    private String topicName = "test";
    private Gson gson;


    @Autowired
    public KafkaEventHandler(Configuration configuration, Transformer transformer, ElasticsearchEventDispatcher elasticsearchEventDispatcher) throws NotRecognizedEventTypeException {
        this.configuration = configuration;
        this.transformer = transformer;
        this.elasticsearchEventDispatcher = elasticsearchEventDispatcher;
        this.gson = new Gson();
        Properties properties = prepareKafkaProperties(configuration);
        this.consumer = new KafkaConsumer<String, String>(properties);
        this.consumer.subscribe(Arrays.asList(topicName));
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

    private void consumeAndHandleEvent() throws NotRecognizedEventTypeException {
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
        return gson.fromJson(serialized, Event.class);
    }

    public void run(ApplicationArguments applicationArguments) throws Exception {
        consumeAndHandleEvent();
    }
}
