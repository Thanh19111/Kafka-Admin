package com.thanhpham.Kafka.dto.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TopicCreateRequest {
    private String topicName;
    private int partitionNum;
    private int replicaFNum;
    private int retainTime;
    private int maxSize;
    private Map<String, String> config = new HashMap<>();
}
