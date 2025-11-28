package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupMemberDetailResponse {
    private String memberId;
    private String clientId;
    private String host;
    List<String> assignPartitions;
}
