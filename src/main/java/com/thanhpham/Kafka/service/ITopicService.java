package com.thanhpham.Kafka.service;

import org.apache.kafka.clients.admin.GroupListing;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ITopicService {
    Set<String> getList() throws ExecutionException, InterruptedException;
    void createNewTopic(String topicName, int partitionNum, int replicaFNum) throws ExecutionException, InterruptedException;
    void getAllTopicDetail() throws ExecutionException, InterruptedException;
    void getTopicDetail(String topicName) throws ExecutionException, InterruptedException;
    void deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException;
    void increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException;
}
