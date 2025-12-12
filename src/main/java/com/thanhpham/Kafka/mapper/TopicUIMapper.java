package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.PartitionSpecsResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.dto.uiformat.TopicDetailUI;

public class TopicUIMapper {
    public static TopicDetailUI format(TopicDetailResponse res){
        String topicName = res.getTopicName();
        int totalPartition = res.getPartitionCount();
        int replicaFactorNum = res.getPartitions().getFirst().getReplicas().size();

        int totalISR = 0;
        for (PartitionSpecsResponse par : res.getPartitions()){
            totalISR += par.getIsr().size();
        }

        return new TopicDetailUI(topicName, totalPartition, totalPartition * replicaFactorNum - totalISR, replicaFactorNum);
    }
}
