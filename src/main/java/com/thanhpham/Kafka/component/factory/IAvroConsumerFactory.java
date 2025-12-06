package com.thanhpham.Kafka.component.factory;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;

public interface IAvroConsumerFactory {
    Consumer<String, GenericRecord> create();
}
