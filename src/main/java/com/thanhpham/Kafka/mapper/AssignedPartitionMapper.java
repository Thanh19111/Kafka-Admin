package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.AssignedPartitionResponse;
import org.apache.kafka.common.TopicPartition;

public class AssignedPartitionMapper {
    public static AssignedPartitionResponse toResponse(TopicPartition partition) {
        return new AssignedPartitionResponse(partition.partition(), partition.topic());
    }
}
