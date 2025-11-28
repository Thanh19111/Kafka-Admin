package com.thanhpham.Kafka.dto.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TopicCreateRequest {
    private String topicName;
    private int partitionNum;
    private short replicaFNum;
    private Map<String, String> config = new HashMap<>();
}
