package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupDetailResponse {
    private String groupId;
    private NodeResponse coordinator;
    List<GroupMemberResponse> members;
}
