package com.thanhpham.Kafka.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// class nay dung chung cho request hay response deu duoc
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicAndPartition {
    private String topic;
    private int partition;
}
