package com.thanhpham.Kafka.service;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.TopicDescribeResponse;
import org.apache.kafka.clients.admin.GroupListing;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ITopicService {
    Set<String> getAllListTopic() throws ExecutionException, InterruptedException;
    String createNewTopic(TopicCreateRequest request) throws ExecutionException, InterruptedException;
    List<TopicDescribeResponse> getAllTopicDetail() throws ExecutionException, InterruptedException;
    String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException;
    String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException;
    TopicDescribeResponse getATopicDetail(String topicName) throws ExecutionException, InterruptedException;
    void getAllConsumerGroups() throws ExecutionException, InterruptedException;
}
