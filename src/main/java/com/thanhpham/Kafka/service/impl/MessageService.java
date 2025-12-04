package com.thanhpham.Kafka.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final ConsumerFactory<String, GenericRecord> avroFactory;
    private final ConsumerFactory<String, JsonNode> jsonFactory;

    @Override
    public List<String> decodeAvro(String topic, int limit){
        KafkaConsumer<String, GenericRecord> consumer = (KafkaConsumer<String, GenericRecord>) avroFactory.createConsumer();
        consumer.subscribe(List.of(topic));
        List<String> results = new ArrayList<>();
        ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(record.value().toString());
            if (results.size() >= limit) break;
        }

        consumer.close();
        return results;
    }

    @Override
    public List<JsonNode> decodeJson(String topic, int limit){
        KafkaConsumer<String, JsonNode> consumer = (KafkaConsumer<String, JsonNode>) jsonFactory.createConsumer();
        consumer.subscribe(List.of(topic));
        List<JsonNode> results = new ArrayList<>();
        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(record.value());
            if (results.size() >= limit) break;
        }

        consumer.close();
        return results;
    }
}
