package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TopicDetailResponse {
    private String topicName;
    private String topicId;
    private int partitionCount;
    private int replicaFactorNum;
    private List<PartitionSpecsResponse> partitions;
}
