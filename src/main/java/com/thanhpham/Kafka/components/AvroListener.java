package com.thanhpham.Kafka.components;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AvroListener {
    @KafkaListener(topics = "avro", groupId = "avro-group")
    public void listen(GenericRecord record) {
        System.out.println("Received Avro message: " + record);
    }
}
