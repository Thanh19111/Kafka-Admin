package com.thanhpham.Kafka.service.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.config.factory.AvroConsumerFactory;
import com.thanhpham.Kafka.config.factory.JsonConsumerFactory;
import com.thanhpham.Kafka.config.pool.client.avro.IAvroConsumerPool;
import com.thanhpham.Kafka.config.pool.client.json.IJsonConsumerPool;
import com.thanhpham.Kafka.dto.response.AvroMessage;
import com.thanhpham.Kafka.dto.response.JsonMessage;
import com.thanhpham.Kafka.mapper.AvroMessageMapper;
import com.thanhpham.Kafka.mapper.JsonMessageMapper;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final AvroConsumerFactory avroFactory;
    private final JsonConsumerFactory jsonFactory;
    private final IAvroConsumerPool avroPool;
    private final IJsonConsumerPool jsonPool;

    @Override
    public List<AvroMessage> decodeAvro(String topic) {
        Consumer<String, GenericRecord> consumer = avroPool.get(topic);

        List<AvroMessage> results = new ArrayList<>();
        ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(AvroMessageMapper.toResponse(record));
        }

        return results;
    }

    @Override
    public List<JsonMessage> decodeJson(String topic) {
        Consumer<String, JsonNode> consumer = jsonPool.get(topic);
        consumer.subscribe(List.of(topic));
        List<JsonMessage> results = new ArrayList<>();

        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(JsonMessageMapper.toResponse(record));
        }

        return results;
    }

    @Override
    public List<AvroMessage> readAvroMessageByOffset(String topic, int partition, long startOffset, long endOffset){
        if(endOffset < startOffset) {
            return List.of();
        }

        try (Consumer<String, GenericRecord> consumer = avroFactory.create())
        {
            TopicPartition topicPartition = new TopicPartition(topic, partition);
            consumer.assign(List.of(topicPartition));
            consumer.seek(topicPartition, startOffset);

            long count = endOffset - startOffset + 1;

            List<ConsumerRecord<String, GenericRecord>> results = new ArrayList<>();

            while(count > 0) {
                ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(30));
                System.out.println(">> " + records.count());
                if(records.count() < count) {
                    results.addAll(records.records(topicPartition));
                    count = count - records.count();
                } else if(records.count() > count) {
                    Iterator<ConsumerRecord<String, GenericRecord>> it = records.iterator();
                    while(it.hasNext() && count > 0) {
                        results.add(it.next());
                        count--;
                    }
                }
            }

            return results.stream().map(AvroMessageMapper::toResponse).toList();

        } catch (Exception e) {
            System.out.println("Error to poll message");
            return List.of();
        }
    }

    @Override
    public List<JsonMessage> readJsonMessageByOffset(String topic, int partition, long startOffset, long endOffset){
        if(endOffset < startOffset) {
            return List.of();
        }

        try (Consumer<String, JsonNode> consumer = jsonFactory.create()) {
            TopicPartition topicPartition = new TopicPartition(topic, partition);
            consumer.assign(List.of(topicPartition));
            consumer.seek(topicPartition, startOffset);

            long count = endOffset - startOffset + 1;
            List<ConsumerRecord<String, JsonNode>> results = new ArrayList<>();

            while(count > 0) {
                ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofSeconds(3));
                System.out.println(">> " + records.count());
                if(records.count() < count) {
                    results.addAll(records.records(topicPartition));
                    count = count - records.count();
                } else if(records.count() > count) {
                    Iterator<ConsumerRecord<String, JsonNode>> it = records.iterator();
                    while(it.hasNext() && count > 0) {
                        results.add(it.next());
                        count--;
                    }
                }
            }
            return results.stream().map(JsonMessageMapper::toResponse).toList();
        } catch (Exception e) {
            System.out.println("Error to poll message");
            return List.of();
        }
    }
}
