package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupMemberResponse;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailMapper {
    public static GroupDetailResponse toResponse(String groupId, ConsumerGroupDescription des) {
        GroupDetailResponse group = new GroupDetailResponse();
        group.setGroupId(groupId);
        group.setCoordinator(des.coordinator());
        List<GroupMemberResponse> members = new ArrayList<>();
        des.members().forEach(member -> {
            members.add(GroupMemberDetailMapper.toResponse(member));
        });
        group.setMembers(members);
        return group;
    }

}
