package com.thanhpham.Kafka.dto.response;

import lombok.Data;
import org.apache.kafka.common.Node;

import java.util.List;

@Data
public class GroupDetailResponse {
    private String groupId;
    private Node coordinator;
    List<GroupMemberResponse> members;
}
