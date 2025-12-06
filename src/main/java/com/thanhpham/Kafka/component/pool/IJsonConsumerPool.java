package com.thanhpham.Kafka.component.pool;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.Consumer;

public interface IJsonConsumerPool {
    Consumer<String, JsonNode> get(String topicName);
}
