package com.thanhpham.Kafka.service.consumergroup;

import com.thanhpham.Kafka.config.pool.admin.IAdminClientPool;
import com.thanhpham.Kafka.config.property.KafkaProperties;
import com.thanhpham.Kafka.dto.request.TopicAndPartition;
import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.mapper.GroupDetailMapper;
import com.thanhpham.Kafka.util.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ConsumerGroupService implements IGroupConsumerService {
    private final KafkaProperties properties;
    private final IAdminClientPool adminClientPool;

    @Override
    public List<GroupDetailResponse> getAllConsumerGroups() throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> groups = new ArrayList<>();

        ListGroupsResult result = adminClientPool.get(properties.getBootstrapServer()).listGroups();

        List<String> groupNames = result.all().get().stream()
                .filter(g -> !g.protocol().isBlank() && g.protocol().equals("consumer"))
                .map(GroupListing::groupId).toList();

        DescribeConsumerGroupsResult desc = adminClientPool.get(properties.getBootstrapServer())
                .describeConsumerGroups(groupNames);

        desc.all().get().forEach((groupId, description) -> {
            groups.add(GroupDetailMapper.toResponse(groupId, description));
        });

        return groups;
    }

    @Override
    public List<GroupPartitionResponse> checkLagByGroupId(String groupId) throws ExecutionException, InterruptedException {
        ListConsumerGroupOffsetsResult offsetsResult = adminClientPool.get(properties.getBootstrapServer())
                .listConsumerGroupOffsets(groupId);

        Map<TopicPartition, OffsetAndMetadata> offsets = offsetsResult.partitionsToOffsetAndMetadata().get();

        Map<TopicPartition, Long> endOffsets = adminClientPool.get(properties.getBootstrapServer())
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

    @Override
    public void changeOffset(String groupId, String topic, int partition, long offset) throws ExecutionException, InterruptedException {
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(
                new TopicPartition(topic, partition),
                new OffsetAndMetadata(offset));

        AlterConsumerGroupOffsetsResult result =
                adminClientPool.get(properties.getBootstrapServer())
                        .alterConsumerGroupOffsets(groupId, offsets);

        result.all().get();
    }
}
