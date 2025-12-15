package com.thanhpham.Kafka.dto.form;

import lombok.Data;

@Data
public class PartitionIncreaseRequest {
    private String topicName;
    private Integer partitionNumber;
}
