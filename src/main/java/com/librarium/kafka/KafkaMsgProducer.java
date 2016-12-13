package com.librarium.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;


import java.util.Properties;

/**
 * Created by Igor on 13.12.2016.
 */
@Component
public class KafkaMsgProducer {

    private KafkaProducer<String,String> producer;

    public KafkaMsgProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("acks", "all");
        properties.put("retries", 1);
        properties.put("batch.size", 16384);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(properties);
    }

    public void produceTest(){
        String topicName = "test";
        for(int i = 0; i < 10; i++){
            producer.send(new ProducerRecord<String, String>(topicName,
                    Integer.toString(i), "yo, testujemy producenta: "+Integer.toString(i)));
            System.out.println("Message sent successfully");
        }
        producer.close();
    }
}
