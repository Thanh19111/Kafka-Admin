package com.thanhpham.Kafka.dto.request;

import lombok.Data;

@Data
public class ChangeOffsetRequest {
    private String groupId;
    private TopicAndPartition detail;
    private long offset;
}
