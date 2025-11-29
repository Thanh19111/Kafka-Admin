package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.AssignedPartitionResponse;
import com.thanhpham.Kafka.dto.response.GroupMemberResponse;
import org.apache.kafka.clients.admin.MemberDescription;

import java.util.List;

public class GroupMemberDetailMapper {
    public static GroupMemberResponse toResponse(MemberDescription des){
        GroupMemberResponse detail = new GroupMemberResponse();
        detail.setMemberId(des.consumerId());
        detail.setClientId(des.clientId());
        detail.setHost(des.host());
        List<AssignedPartitionResponse> assinged = des.assignment().topicPartitions().stream().map(AssignedPartitionMapper::toResponse).toList();
        detail.setAssignPartitions(assinged);
        return detail;
    }
}
