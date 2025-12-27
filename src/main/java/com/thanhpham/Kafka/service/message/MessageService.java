package com.thanhpham.Kafka.service.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.thanhpham.Kafka.config.factory.AvroConsumerFactory;
import com.thanhpham.Kafka.config.factory.JsonConsumerFactory;
import com.thanhpham.Kafka.config.pool.client.avro.IAvroConsumerPool;
import com.thanhpham.Kafka.config.pool.client.json.IJsonConsumerPool;
import com.thanhpham.Kafka.dto.response.MessageSlice;
import com.thanhpham.Kafka.mapper.AvroMessageMapper;
import com.thanhpham.Kafka.mapper.JsonMessageMapper;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final AvroConsumerFactory avroFactory;
    private final JsonConsumerFactory jsonFactory;
    private final IAvroConsumerPool avroPool;
    private final IJsonConsumerPool jsonPool;

    @Override
    public List<MessageSlice> decodeAvro(String topic) {
        Consumer<String, GenericRecord> consumer = avroPool.get(topic);

        List<MessageSlice> results = new ArrayList<>();
        ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(AvroMessageMapper.toResponse(record));
        }

        return results;
    }

    @Override
    public List<MessageSlice> decodeJson(String topic) {
        Consumer<String, JsonNode> consumer = jsonPool.get(topic);
        consumer.subscribe(List.of(topic));
        List<MessageSlice> results = new ArrayList<>();

        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofSeconds(2));

        for (var record : records) {
            results.add(JsonMessageMapper.toResponse(record));
        }

        return results;
    }

    @Override
    public List<MessageSlice> readAvroMessageByOffset(String topic, int partition, long startOffset, long endOffset){
        if(endOffset < startOffset) {
            return List.of();
        }

        try (Consumer<String, GenericRecord> consumer = avroFactory.create()) {
            TopicPartition topicPartition = new TopicPartition(topic, partition);
            consumer.assign(List.of(topicPartition));
            consumer.seek(topicPartition, startOffset);

            long count = endOffset - startOffset + 1;

            List<ConsumerRecord<String, GenericRecord>> results = new ArrayList<>();

            while(count > 0) {
                ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(30));
                System.out.println(">> " + records.count());
                if(records.count() <= count) {
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
    public List<MessageSlice> readJsonMessageByOffset(String topic, int partition, long startOffset, long endOffset){
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

    @Override
    public void pushJsonMessage() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class);

        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, true);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("id", 1);
        json.put("name", "Thanh");

        Producer<String, JsonNode> producer = new KafkaProducer<>(props);
        producer.send(new ProducerRecord<>("thanhp", json));
        producer.flush();
        producer.close();
    }

//    @Override
//    public List<AvroMessage> readAvroMessageByOffset(String topic, int partition, long startOffset, long endOffset) {
//        try {
//            return CompletableFuture
//                    .supplyAsync(() -> this.hardTask(topic, partition, startOffset, endOffset))
//                    .get(3, TimeUnit.SECONDS);
//        } catch (TimeoutException e) {
//            return List.of();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
