package com.thanhpham.Kafka.components;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AvroConsumerPool {
    private final ConsumerFactory<String, GenericRecord> avroFactory;
    private final Map<String, KafkaConsumer<String, GenericRecord>> pool = new ConcurrentHashMap<>();

    public KafkaConsumer<String, GenericRecord> get(String topicName) {
        return pool.computeIfAbsent(topicName, this::create);
    }

    private KafkaConsumer<String, GenericRecord> create(String topicName) {
        KafkaConsumer<String, GenericRecord> instance = (KafkaConsumer<String, GenericRecord>) avroFactory.createConsumer();
        instance.subscribe(List.of(topicName));
        return instance;
    }

    @PreDestroy
    public void shutdown() {
        pool.values().forEach(KafkaConsumer::close);
    }
}
