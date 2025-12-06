package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.components.AdminClientPool;
import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.mapper.TopicDetailMapper;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class TopicService implements ITopicService {
    private final AdminClientPool adminClientPool;

    public Set<String> getAllListTopic() throws ExecutionException, InterruptedException {
        return adminClientPool.get("localhost:9092")
                .listTopics()
                .names()
                .get();
    }

    @Override
    public String createNewTopic(TopicCreateRequest request) throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic(request.getTopicName(), request.getPartitionNum(), request.getReplicaFNum())
                .configs(request.getConfig());

        adminClientPool.get("localhost:9092")
                .createTopics(Collections.singleton(newTopic))
                .all()
                .get();
        return "Topic " + request.getTopicName() + " has been created!";
    }

    @Override
    public List<TopicDetailResponse> getAllTopicDetail() throws ExecutionException, InterruptedException {
        List<TopicDetailResponse> res = new ArrayList<>();
        List<String> topicNames = new ArrayList<>(getAllListTopic());
        DescribeTopicsResult describeResult = adminClientPool.get("localhost:9092")
                .describeTopics(topicNames);

        Map<String, KafkaFuture<TopicDescription>> desc = describeResult.topicNameValues();
        for (String topic : desc.keySet()) {
            TopicDescription detail = desc.get(topic).get();
            res.add(TopicDetailMapper.toResponse(detail));
        }
        return res;
    }

    @Override
    public TopicDetailResponse getATopicDetail(String topicName) throws ExecutionException, InterruptedException {
        List<String> topicNames = new ArrayList<>(List.of(topicName));
        DescribeTopicsResult describeResult = adminClientPool.get("localhost:9092")
                .describeTopics(topicNames);
        TopicDescription t = describeResult.topicNameValues()
                .get(topicName)
                .get();
        return TopicDetailMapper.toResponse(t);
    }

    @Override
    public String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        DeleteTopicsResult result = adminClientPool.get("localhost:9092").deleteTopics(Collections.singleton(topicName));
        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Topic " + topicName + " has been deleted!";
    }

    @Override
    public String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        NewPartitions newPartitions = NewPartitions.increaseTo(partitionNum);
        CreatePartitionsResult result = adminClientPool.get("localhost:9092").createPartitions(
                Collections.singletonMap(topicName, newPartitions));

        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã tăng partition của topic " + topicName + " lên " + partitionNum;
    }
}
