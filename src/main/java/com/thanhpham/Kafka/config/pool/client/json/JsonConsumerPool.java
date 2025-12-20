package com.thanhpham.Kafka.config.pool.client.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.config.factory.JsonConsumerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonConsumerPool implements IJsonConsumerPool {
    private final JsonConsumerFactory jsonFactory;

    @Cacheable(value = "jsons", key = "#topicName")
    public Consumer<String, JsonNode> get(String topicName) {
        System.out.println("Create new json consumer for caching");
        return create(topicName);
    }

    private Consumer<String, JsonNode> create(String topicName) {
        Consumer<String, JsonNode> instance = jsonFactory.create();
        instance.subscribe(List.of(topicName));
        return instance;
    }
}
