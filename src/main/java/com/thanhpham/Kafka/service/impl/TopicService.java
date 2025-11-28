package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.utils.Constants;
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
    public String createNewTopic(TopicCreateRequest request) throws ExecutionException, InterruptedException {
        System.out.println(request.toString());
        NewTopic newTopic = new NewTopic(request.getTopicName(), request.getPartitionNum(), request.getReplicaFNum())
                .configs(request.getConfig());

        adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        return "Topic " + request.getTopicName() + " đã được tạo!";
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
    public String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        DeleteTopicsResult result = adminClient.deleteTopics(Collections.singleton(topicName));
        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã xóa thành công topic: " + topicName;
    }

    @Override
    public String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        Map<String, NewPartitions> newPartitions = Collections.singletonMap(
                topicName,
                NewPartitions.increaseTo(partitionNum)
        );
        CreatePartitionsResult result = adminClient.createPartitions(newPartitions);
        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã tăng partition của topic " + topicName + " lên " + partitionNum;
    }
}
