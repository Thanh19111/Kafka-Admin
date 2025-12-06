package com.thanhpham.Kafka.component.factory;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.Consumer;

public interface IJsonConsumerFactory {
    Consumer<String, JsonNode> createConsumer();
}
