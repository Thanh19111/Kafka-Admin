package com.thanhpham.Kafka.controller;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.service.IGroupConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupConsumerController {
    private final IGroupConsumerService iGroupConsumerService;

    @GetMapping
    public List<GroupDetailResponse> getAllConsumerGroups() throws ExecutionException, InterruptedException {
        return iGroupConsumerService.getAllConsumerGroups();
    }

    @GetMapping("/{groupId}/lag")
    public List<GroupPartitionResponse> checkLagByGroupName(@PathVariable("groupId") String groupId) throws ExecutionException, InterruptedException {
        return iGroupConsumerService.checkLagByGroupId(groupId);
    }
}
