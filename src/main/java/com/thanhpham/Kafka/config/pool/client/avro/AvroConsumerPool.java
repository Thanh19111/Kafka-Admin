package com.thanhpham.Kafka.config.pool.client.avro;

import com.thanhpham.Kafka.config.factory.AvroConsumerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AvroConsumerPool implements IAvroConsumerPool {
    private final AvroConsumerFactory avroFactory;

    @Cacheable(value = "avros", key = "#topicName")
    public Consumer<String, GenericRecord> get(String topicName) {
        System.out.println("Create new avro consumer bean for caching");
        return create(topicName);
    }

    private Consumer<String, GenericRecord> create(String topicName) {
        Consumer<String, GenericRecord> instance = avroFactory.create();
        instance.subscribe(List.of(topicName));
        return instance;
    }
}
