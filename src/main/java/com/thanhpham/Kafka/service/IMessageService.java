package com.thanhpham.Kafka.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

import java.io.IOException;
import java.util.List;

public interface IMessageService {
    List<JsonNode> decodeJson(String topic, int limit);
    List<String> decodeAvro(String topic, int limit);
    void checkFormat(String topic) throws RestClientException, IOException;
}
