package com.thanhpham.Kafka.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedPartitionResponse {
    private int partition;
    private String topic;
}
