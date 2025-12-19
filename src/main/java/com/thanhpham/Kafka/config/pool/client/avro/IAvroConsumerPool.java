package com.thanhpham.Kafka.config.pool.client.avro;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;

public interface IAvroConsumerPool {
    Consumer<String, GenericRecord> get(String topicName);
}
