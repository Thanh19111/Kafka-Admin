package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.PartitionSpecsResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;

import java.util.List;

public class TopicDetailMapper {
    public static TopicDetailResponse toResponse(TopicDescription topicDes){
        TopicDetailResponse res = new TopicDetailResponse();
        res.setTopicName(topicDes.name());
        res.setTopicId(topicDes.topicId().toString());
        res.setReplicaFactorNum(topicDes.partitions().getFirst().replicas().size());
        res.setPartitionCount(topicDes.partitions().size());

        List<PartitionSpecsResponse> partitions = topicDes.partitions().stream().map(partition -> {
            PartitionSpecsResponse partitionInfo =  new PartitionSpecsResponse();
            partitionInfo.setPartition(partition.partition());
            partitionInfo.setLeader(partition.leader().id());
            List<Integer> replicas = partition.replicas().stream().map(Node::id).toList();
            partitionInfo.setReplicas(replicas);
            List<Integer> isr = partition.isr().stream().map(Node::id).toList();
            partitionInfo.setIsr(isr);
            return partitionInfo;
        }).toList();
        res.setPartitions(partitions);
        return res;
    }
}
