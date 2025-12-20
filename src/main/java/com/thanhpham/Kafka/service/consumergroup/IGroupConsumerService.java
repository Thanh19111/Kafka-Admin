package com.thanhpham.Kafka.service.consumergroup;

import com.thanhpham.Kafka.dto.request.TopicAndPartition;
import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IGroupConsumerService {
    List<GroupDetailResponse> getAllConsumerGroups() throws ExecutionException, InterruptedException;
    List<GroupPartitionResponse> checkLagByGroupId(String groupId) throws ExecutionException, InterruptedException;
    String changeOffset(String groupId, TopicAndPartition detail, long offset) throws ExecutionException, InterruptedException;
}
