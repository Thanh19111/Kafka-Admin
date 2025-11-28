package com.thanhpham.Kafka.dto.response;

import lombok.Data;

/**
 * class nay dung trong uscase xem thong tin partiton ma group dang assign
 */
@Data
public class GroupPartitionResponse {
    private String partitionId;
    private long commitedOffset;
    private long latestOffset;
    private long lag;
}
