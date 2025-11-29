package com.thanhpham.Kafka.service;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ITopicService {
    String createNewTopic(TopicCreateRequest request) throws ExecutionException, InterruptedException;
    List<TopicDetailResponse> getAllTopicDetail() throws ExecutionException, InterruptedException;
    String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException;
    String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException;
    TopicDetailResponse getATopicDetail(String topicName) throws ExecutionException, InterruptedException;
}
