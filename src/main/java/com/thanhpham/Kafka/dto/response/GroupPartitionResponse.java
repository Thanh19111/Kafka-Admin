package com.thanhpham.Kafka.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * class nay dung trong uscase xem thong tin partiton ma group dang assign
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupPartitionResponse {
    private String topic;
    private int partition;
    private long commitedOffset;
    private long latestOffset;
    private long lag;
}
