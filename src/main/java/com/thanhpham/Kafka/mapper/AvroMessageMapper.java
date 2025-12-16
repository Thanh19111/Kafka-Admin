package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.AvroMessage;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class AvroMessageMapper {
    public static AvroMessage toResponse(ConsumerRecord<String, GenericRecord> record) {
        return new AvroMessage(record.offset(), record.value().toString());
    }
}
