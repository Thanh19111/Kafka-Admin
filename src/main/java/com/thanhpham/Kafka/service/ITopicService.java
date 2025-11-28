package com.thanhpham.Kafka.service;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import org.apache.kafka.clients.admin.GroupListing;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ITopicService {
    Set<String> getList() throws ExecutionException, InterruptedException;
    String createNewTopic(TopicCreateRequest request) throws ExecutionException, InterruptedException;
    void getAllTopicDetail() throws ExecutionException, InterruptedException;
    void getTopicDetail(String topicName) throws ExecutionException, InterruptedException;
    String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException;
    String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException;
}
