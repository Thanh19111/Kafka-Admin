package com.thanhpham.Kafka.service.ConsumerGroupService;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IGroupConsumerService {
    List<GroupDetailResponse> getAllConsumerGroups() throws ExecutionException, InterruptedException;
    List<GroupPartitionResponse> checkLagByGroupId(String groupId) throws ExecutionException, InterruptedException;
    void getAllLagAndOffset() throws ExecutionException, InterruptedException;
}
