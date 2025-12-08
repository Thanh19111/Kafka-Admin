package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TopicDetailResponse {
    public String topicName;
    public String topicId;
    public int partitionCount;
    public int replicaFactorNum;
    public List<PartitionSpecsResponse> partitions;
}
