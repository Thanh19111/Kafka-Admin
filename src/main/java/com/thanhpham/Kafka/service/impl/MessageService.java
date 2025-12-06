package com.thanhpham.Kafka.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.service.IMessageService;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final SchemaRegistryClient client;
    private final ConsumerFactory<String, GenericRecord> avroFactory;
    private final ConsumerFactory<String, JsonNode> jsonFactory;

    @Override
    public List<String> decodeAvro(String topic, int limit){
        KafkaConsumer<String, GenericRecord> consumer = (KafkaConsumer<String, GenericRecord>) avroFactory.createConsumer();
        consumer.subscribe(List.of(topic));
        List<String> results = new ArrayList<>();
        ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(record.value().toString());
            if (results.size() >= limit) break;
        }

        consumer.close();
        return results;
    }

    @Override
    public List<JsonNode> decodeJson(String topic, int limit){
        KafkaConsumer<String, JsonNode> consumer = (KafkaConsumer<String, JsonNode>) jsonFactory.createConsumer();
        consumer.subscribe(List.of(topic));
        List<JsonNode> results = new ArrayList<>();
        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(record.value());
            if (results.size() >= limit) break;
        }

        consumer.close();
        return results;
    }

    @Override
    public void checkFormat(String topic) throws RestClientException, IOException {
//        String subject = topic + "-value";
//        Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "format-checker");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        try(KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(props)){
//            consumer.subscribe(List.of(topic));
//            ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(300));
//            System.out.println("1");
//            if(records.isEmpty()) return;
//            for (ConsumerRecord<byte[], byte[]> rec : records) {
//                byte[] value = rec.value();
//                int schemaId = ByteBuffer.wrap(value, 1, 4).getInt();
//                SchemaMetadata meta = client.getSchemaMetadata(subject, schemaId);
//                String schemaType = meta.getSchemaType(); // AVRO | JSON | PROTOBUF
//
//                System.out.println(schemaType);
//            }
//
//            System.out.println("End");
//        }

    }
}
