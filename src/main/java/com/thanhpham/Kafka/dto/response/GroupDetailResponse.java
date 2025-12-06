package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupDetailResponse {
    private String groupId;
    private NodeResponse coordinator;
    private String groupState;
    List<GroupMemberResponse> members;
}
