package com.thanhpham.Kafka.dto.uiformat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupConsumerDetailUI {
    private String consumerGroupId;
    private int memberNum;
    private int topicNum;
    private int latestOffset;
    private int messageBehind;
    private int coordinator;
    private String state;
}
