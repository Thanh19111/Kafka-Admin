package com.thanhpham.Kafka.controller.api;

import com.thanhpham.Kafka.dto.request.ChangeOffsetRequest;
import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.service.consumergroup.IGroupConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/test")
    public void changeOffset(@RequestBody ChangeOffsetRequest request) throws ExecutionException, InterruptedException {
        iGroupConsumerService.changeOffset(request.getGroupId(), request.getDetail(), request.getOffset());
    }

}
