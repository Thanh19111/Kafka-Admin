package com.thanhpham.Kafka.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface IMessageService {
    List<JsonNode> decodeJson(String topic, int limit);
    List<String> decodeAvro(String topic, int limit);
}
