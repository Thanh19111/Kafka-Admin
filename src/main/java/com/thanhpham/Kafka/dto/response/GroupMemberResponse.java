package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupMemberResponse {
    private String memberId;
    private String clientId;
    private String host;
    List<AssignedPartitionResponse> assignPartitions;
}
