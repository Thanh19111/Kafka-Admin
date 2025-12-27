package com.thanhpham.Kafka.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.dto.response.MessageSlice;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class JsonMessageMapper {
    public static MessageSlice toResponse(ConsumerRecord<String, JsonNode> record) {
        return new MessageSlice(record.offset(), record.value().toString());
    }
}
