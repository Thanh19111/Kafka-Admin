package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.components.AdminClientPool;
import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.mapper.GroupDetailMapper;
import com.thanhpham.Kafka.service.IGroupConsumerService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class GroupConsumerService implements IGroupConsumerService {
    private final AdminClientPool adminClientPool;

    @Override
    public List<GroupDetailResponse> getAllConsumerGroups() throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> groups = new ArrayList<>();

        ListGroupsResult result = adminClientPool.get("localhost:9092").listGroups();

        List<String> groupNames = result.all().get().stream()
                .filter(g -> !g.protocol().isBlank() && g.protocol().equals("consumer"))
                .map(GroupListing::groupId).toList();
        DescribeConsumerGroupsResult desc = adminClientPool.get("localhost:9092")
                .describeConsumerGroups(groupNames);

        desc.all().get().forEach((groupId, description) -> {
            groups.add(GroupDetailMapper.toResponse(groupId, description));
        });

        return groups;
    }

    @Override
    public List<GroupPartitionResponse> checkLagByGroupId(String groupId) throws ExecutionException, InterruptedException {
        ListConsumerGroupOffsetsResult offsetsResult = adminClientPool.get("localhost:9092")
                .listConsumerGroupOffsets(groupId);

        Map<TopicPartition, OffsetAndMetadata> offsets = offsetsResult.partitionsToOffsetAndMetadata().get();
        Map<TopicPartition, Long> endOffsets = adminClientPool.get("localhost:9092")
                .listOffsets(offsets.keySet().stream().collect(
                                HashMap::new, (m, tp) -> m.put(tp, OffsetSpec.latest()),
                                HashMap::putAll))
                .all()
                .get()
                .entrySet()
                .stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().offset()),
                        HashMap::putAll);

        List<GroupPartitionResponse> res = new ArrayList<>();

        offsets.forEach((tp, offsetAndMeta) -> {
            long committed = offsetAndMeta.offset();
            long latest = endOffsets.getOrDefault(tp, committed);
            long lag = latest - committed;
            res.add(new GroupPartitionResponse(tp.toString(), committed, latest, lag));
        });

        return res;
    }
}
