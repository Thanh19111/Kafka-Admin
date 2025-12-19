package com.thanhpham.Kafka.service.topic;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponseWithConfig;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ITopicService {
    String createNewTopic(TopicCreateRequest request) throws ExecutionException, InterruptedException;
    List<TopicDetailResponse> getAllTopicDetail() throws ExecutionException, InterruptedException;
    String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException;
    String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException;
    TopicDetailResponse getATopicDetail(String topicName) throws ExecutionException, InterruptedException;
    TopicDetailResponseWithConfig getATopicDetailWithConfig(String topicName) throws ExecutionException, InterruptedException;
}
