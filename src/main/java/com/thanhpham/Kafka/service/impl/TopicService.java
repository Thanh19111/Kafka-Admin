package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.service.ITopicService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class TopicService implements ITopicService {
    private final AdminClient adminClient;

    @Override
    public Set<String> getList() throws ExecutionException, InterruptedException {
        return adminClient.listTopics().names().get();
    }

    @Override
    public void createNewTopic(String topicName, int partitionNum, int replicaFNum) throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic(topicName, partitionNum, (short) replicaFNum);
        adminClient.createTopics(Collections.singleton(newTopic)).all().get();
    }

    @Override
    public void getAllTopicDetail() throws ExecutionException, InterruptedException {
        DescribeTopicsResult describeResult = adminClient.describeTopics(getList());
        Map<String, TopicDescription> topicDescriptions = describeResult.allTopicNames().get();

        topicDescriptions.forEach((topicName, description) -> {
            System.out.printf("Topic: %s | PartitionCount: %d | ReplicationFactor: %d | Internal: %b | TopicId: %s%n",
                    topicName,
                    description.partitions().size(),
                    description.partitions().isEmpty() ? 0 : description.partitions().getFirst().replicas().size(),
                    description.isInternal(),
                    description.topicId());
        });
    }

    @Override
    public void getTopicDetail(String topicName) throws ExecutionException, InterruptedException {
        DescribeTopicsResult describeResult = adminClient.describeTopics(Collections.singleton(topicName));
        System.out.println("=== Thông tin Topic ===");
        System.out.println(describeResult.topicNameValues().get(topicName).get().toString());
    }

    @Override
    public void deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        DeleteTopicsResult result = adminClient.deleteTopics(Collections.singleton(topicName));
        result.all().get(120, TimeUnit.SECONDS);
        System.out.println("Đã xóa thành công topic: " + topicName);
    }

    @Override
    public void increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        Map<String, NewPartitions> newPartitions = Collections.singletonMap(
                topicName,
                NewPartitions.increaseTo(partitionNum)
        );
        CreatePartitionsResult result = adminClient.createPartitions(newPartitions);
        result.all().get(120, TimeUnit.SECONDS);
        System.out.println("Đã tăng partition của topic " + topicName + " lên " + partitionNum);
    }
}
