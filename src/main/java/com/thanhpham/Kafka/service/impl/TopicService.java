package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.mapper.GroupDetailMapper;
import com.thanhpham.Kafka.mapper.TopicDetailMapper;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class TopicService implements ITopicService {
    private final AdminClient adminClient;

    public Set<String> getAllListTopic() throws ExecutionException, InterruptedException {
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
    public List<TopicDetailResponse> getAllTopicDetail() throws ExecutionException, InterruptedException {
        List<TopicDetailResponse> res = new ArrayList<>();
        List<String> topicNames = new ArrayList<>(getAllListTopic());
        DescribeTopicsResult describeResult = adminClient.describeTopics(topicNames);
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
        DescribeTopicsResult describeResult = adminClient.describeTopics(topicNames);
        TopicDescription t = describeResult.topicNameValues().get(topicName).get();
        return TopicDetailMapper.toResponse(t);
    }

    @Override
    public String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        DeleteTopicsResult result = adminClient.deleteTopics(Collections.singleton(topicName));
        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã xóa thành công topic: " + topicName;
    }

    @Override
    public String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        NewPartitions newPartitions = NewPartitions.increaseTo(partitionNum);
        CreatePartitionsResult result = adminClient.createPartitions(
                Collections.singletonMap(topicName, newPartitions));

        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã tăng partition của topic " + topicName + " lên " + partitionNum;
    }
}
