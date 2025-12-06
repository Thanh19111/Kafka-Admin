package com.thanhpham.Kafka.component.pool.impl;

import com.thanhpham.Kafka.component.factory.IAvroConsumerFactory;
import com.thanhpham.Kafka.component.pool.IAvroConsumerPool;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AvroConsumerPool implements IAvroConsumerPool {
    private final IAvroConsumerFactory avroFactory;
    private final Map<String, Consumer<String, GenericRecord>> pool = new ConcurrentHashMap<>();

    @Override
    public Consumer<String, GenericRecord> get(String topicName) {
        return pool.computeIfAbsent(topicName, this::create);
    }

    private Consumer<String, GenericRecord> create(String topicName) {
        Consumer<String, GenericRecord> instance = avroFactory.create();
        instance.subscribe(List.of(topicName));
        return instance;
    }

    @PreDestroy
    public void shutdown() {
        pool.values().forEach(Consumer::close);
    }
}
