package com.thanhpham.Kafka.dto.uiformat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicDetailUI {
    private String topicName;
    private int totalPartition;
    private int outISR;
    private int replicaFactorNum;
}
