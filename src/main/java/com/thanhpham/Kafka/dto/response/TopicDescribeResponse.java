package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TopicDescribeResponse {
    private String topicName;
    private String topicId;
    private int partitionCount;
    private List<PartitionDetailResponse> partitions;
}
