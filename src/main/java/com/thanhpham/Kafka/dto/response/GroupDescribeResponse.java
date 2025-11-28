package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupDescribeResponse {
    private String groupId;
    private String coordinator;
    List<GroupMemberDetailResponse> members;
    List<GroupPartitionResponse> partitions;
}
