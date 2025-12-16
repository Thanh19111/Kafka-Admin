package com.thanhpham.Kafka.config.pool.ConsumerPool;

import com.thanhpham.Kafka.config.factory.AvroConsumerFactory;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AvroConsumerPool {
    private final AvroConsumerFactory avroFactory;
    private final Map<String, Consumer<String, GenericRecord>> pool = new ConcurrentHashMap<>();

    public Consumer<String, GenericRecord> get(String topicName) {
        return pool.computeIfAbsent(topicName, this::create);
    }

    private Consumer<String, GenericRecord> create(String topicName) {
        Consumer<String, GenericRecord> instance = avroFactory.create();
        instance.subscribe(List.of(topicName));
        return instance;
    }

    @PreDestroy
    public void shutdown() {
        pool.values().forEach(Consumer::close);
    }
}
