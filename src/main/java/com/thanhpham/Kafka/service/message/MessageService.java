package com.thanhpham.Kafka.service.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.config.pool.client.avro.IAvroConsumerPool;
import com.thanhpham.Kafka.config.pool.client.json.IJsonConsumerPool;
import com.thanhpham.Kafka.dto.response.AvroMessage;
import com.thanhpham.Kafka.dto.response.JsonMessage;
import com.thanhpham.Kafka.mapper.AvroMessageMapper;
import com.thanhpham.Kafka.mapper.JsonMessageMapper;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final IAvroConsumerPool avroPool;
    private final IJsonConsumerPool jsonPool;

    @Override
    public List<AvroMessage> decodeAvro(String topic){
        Consumer<String, GenericRecord> consumer = avroPool.get(topic);

        List<AvroMessage> results = new ArrayList<>();
        ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(AvroMessageMapper.toResponse(record));
        }

        return results;
    }

    @Override
    public List<JsonMessage> decodeJson(String topic){
        Consumer<String, JsonNode> consumer = jsonPool.get(topic);
        consumer.subscribe(List.of(topic));
        List<JsonMessage> results = new ArrayList<>();
        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(JsonMessageMapper.toResponse(record));
        }

        return results;
    }
}
