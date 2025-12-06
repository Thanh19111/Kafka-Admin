package com.thanhpham.Kafka.component.pool.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.component.factory.IJsonConsumerFactory;
import com.thanhpham.Kafka.component.pool.IJsonConsumerPool;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class JsonConsumerPool implements IJsonConsumerPool {
    private final IJsonConsumerFactory jsonFactory;
    private final Map<String, Consumer<String, JsonNode>> pool = new ConcurrentHashMap<>();

    @Override
    public Consumer<String, JsonNode> get(String topicName) {
        return pool.computeIfAbsent(topicName, this::create);
    }

    private Consumer<String, JsonNode> create(String topicName) {
        Consumer<String, JsonNode> instance = jsonFactory.createConsumer();
        instance.subscribe(List.of(topicName));
        return instance;
    }

    @PreDestroy
    public void shutdown() {
        pool.values().forEach(Consumer::close);
    }
}
