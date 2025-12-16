package com.thanhpham.Kafka.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.dto.response.JsonMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class JsonMessageMapper {
    public static JsonMessage toResponse(ConsumerRecord<String, JsonNode> record) {
        return new JsonMessage(record.offset(), record.value().toString());
    }
}
