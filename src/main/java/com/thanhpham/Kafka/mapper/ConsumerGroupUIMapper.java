package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupMemberResponse;
import com.thanhpham.Kafka.dto.ui.ConsumerGroupMemberUI;
import com.thanhpham.Kafka.dto.ui.ConsumerGroupDetailUI;

import java.util.ArrayList;
import java.util.List;

public class ConsumerGroupUIMapper {
    public static ConsumerGroupDetailUI format(GroupDetailResponse res) {
        ConsumerGroupDetailUI detail = new ConsumerGroupDetailUI();
        detail.setConsumerGroupId(res.getGroupId());
        detail.setMemberNum(res.getMembers().size());

        // cac gia trị -1 se duoc thay the o controller ui
        detail.setTopicNum(-1);
        detail.setLatestOffset(-1);
        detail.setMessageBehind(-1);

        detail.setCoordinator(res.getCoordinator().getId());
        detail.setState(res.getGroupState());
        return detail;
    }

    public static ConsumerGroupMemberUI format (GroupMemberResponse detail){
        ConsumerGroupMemberUI res = new ConsumerGroupMemberUI();
        res.setMemberId(detail.getMemberId());
        res.setClientId(detail.getClientId());
        res.setHost(detail.getHost());
        List<String> topicPartitions = new ArrayList<>();

        for(var i : detail.getAssignPartitions()) {
            topicPartitions.add(i.getTopic() + i.getPartition());
        }
        res.setTopicPartitions(topicPartitions);
        return res;
    }
}
