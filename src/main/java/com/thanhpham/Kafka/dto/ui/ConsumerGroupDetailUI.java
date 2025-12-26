package com.thanhpham.Kafka.dto.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerGroupDetailUI {
    private String consumerGroupId;
    private int memberNum;
    private int topicNum;
    private long latestOffset;
    private long messageBehind;
    private int coordinator;
    private String state;
}
