package com.thanhpham.Kafka.components;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class JsonConsumerPool {
    private final ConsumerFactory<String, JsonNode> jsonFactory;
    private final Map<String, KafkaConsumer<String, JsonNode>> pool = new ConcurrentHashMap<>();

    public KafkaConsumer<String, JsonNode> get(String topicName) {
        return pool.computeIfAbsent(topicName, this::create);
    }

    private KafkaConsumer<String, JsonNode> create(String topicName) {
        KafkaConsumer<String, JsonNode> instance = (KafkaConsumer<String, JsonNode>) jsonFactory.createConsumer();
        instance.subscribe(List.of(topicName));
        return instance;
    }

    @PreDestroy
    public void shutdown() {
        pool.values().forEach(KafkaConsumer::close);
    }
}
