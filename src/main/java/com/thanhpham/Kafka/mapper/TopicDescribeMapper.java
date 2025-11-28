package com.thanhpham.Kafka.mapper;

import com.thanhpham.Kafka.dto.response.PartitionInfoResponse;
import com.thanhpham.Kafka.dto.response.TopicDescribeResponse;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;

import java.util.List;

public class TopicDescribeMapper {
    public static TopicDescribeResponse format(TopicDescription topicDes){
        TopicDescribeResponse res = new TopicDescribeResponse();
        res.setTopicName(topicDes.name());
        res.setTopicId(topicDes.topicId().toString());
        res.setPartitionCount(topicDes.partitions().size());
        List<PartitionInfoResponse> partitions = topicDes.partitions().stream().map(partition -> {
            PartitionInfoResponse partitionInfo =  new PartitionInfoResponse();
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
