package com.thanhpham.Kafka.service;

import org.apache.kafka.clients.admin.GroupListing;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IConsumerService {
    List<GroupListing> getAllConsumerGroups() throws ExecutionException, InterruptedException;
    void readMessage(String topicName);
}
