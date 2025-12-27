package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.MessageSlice;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class AvroMessageMapper {
    public static MessageSlice toResponse(ConsumerRecord<String, GenericRecord> record) {
        return new MessageSlice(record.offset(), record.value().toString());
    }
}
