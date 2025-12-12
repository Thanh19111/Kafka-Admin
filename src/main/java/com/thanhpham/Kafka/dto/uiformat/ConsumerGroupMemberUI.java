package com.thanhpham.Kafka.dto.uiformat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerGroupMemberUI {
    private String memberId;
    private String clientId;
    private String host;
    private List<String> topicPartitions;
}
