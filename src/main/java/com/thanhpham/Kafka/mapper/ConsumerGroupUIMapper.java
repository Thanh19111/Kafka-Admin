package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupMemberResponse;
import com.thanhpham.Kafka.utils.uiformat.ConsumerGroupMemberUI;
import com.thanhpham.Kafka.utils.uiformat.GroupConsumerDetailUI;

import java.util.ArrayList;
import java.util.List;

// tối ưu sau
public class ConsumerGroupUIMapper {
    public static GroupConsumerDetailUI format(GroupDetailResponse res) {
        GroupConsumerDetailUI detail = new GroupConsumerDetailUI();
        detail.setConsumerGroupId(res.getGroupId());
        detail.setMemberNum(res.getMembers().size());
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
        List<String> topicPartitons = new ArrayList<>();
        for(var i : detail.getAssignPartitions()) {
            topicPartitons.add(i.getTopic() + i.getPartition());
        }
        res.setTopicPartitions(topicPartitons);
        return res;
    }
}
