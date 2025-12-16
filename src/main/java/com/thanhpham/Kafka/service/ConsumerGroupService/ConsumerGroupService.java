package com.thanhpham.Kafka.service.ConsumerGroupService;

import com.thanhpham.Kafka.config.pool.AdminClientPool.AdminClientPool;
import com.thanhpham.Kafka.config.pool.AdminClientPool.IAdminClientPool;
import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.mapper.GroupDetailMapper;
import com.thanhpham.Kafka.utils.Constants;
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
public class ConsumerGroupService implements IGroupConsumerService {
    private final IAdminClientPool adminClientPool;

    @Override
    public List<GroupDetailResponse> getAllConsumerGroups() throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> groups = new ArrayList<>();

        ListGroupsResult result = adminClientPool.get(Constants.BOOTSTRAP_SERVERS).listGroups();

        List<String> groupNames = result.all().get().stream()
                .filter(g -> !g.protocol().isBlank() && g.protocol().equals("consumer"))
                .map(GroupListing::groupId).toList();
        DescribeConsumerGroupsResult desc = adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .describeConsumerGroups(groupNames);

        desc.all().get().forEach((groupId, description) -> {
            groups.add(GroupDetailMapper.toResponse(groupId, description));
        });

        return groups;
    }

    @Override
    public List<GroupPartitionResponse> checkLagByGroupId(String groupId) throws ExecutionException, InterruptedException {
        ListConsumerGroupOffsetsResult offsetsResult = adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .listConsumerGroupOffsets(groupId);

        Map<TopicPartition, OffsetAndMetadata> offsets = offsetsResult.partitionsToOffsetAndMetadata().get();

        Map<TopicPartition, Long> endOffsets = adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
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
            res.add(new GroupPartitionResponse(tp.topic(), tp.partition(), committed, latest, lag));
        });

        return res;
    }
}
