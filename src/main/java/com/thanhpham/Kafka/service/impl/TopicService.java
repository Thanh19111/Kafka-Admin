package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.TopicDescribeResponse;
import com.thanhpham.Kafka.mapper.TopicDescribeMapper;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class TopicService implements ITopicService {
    private final AdminClient adminClient;

    @Override
    public Set<String> getAllListTopic() throws ExecutionException, InterruptedException {
        return adminClient.listTopics().names().get();
    }

    @Override
    public String createNewTopic(TopicCreateRequest request) throws ExecutionException, InterruptedException {
        System.out.println(request.toString());
        NewTopic newTopic = new NewTopic(request.getTopicName(), request.getPartitionNum(), request.getReplicaFNum())
                .configs(request.getConfig());

        adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        return "Topic " + request.getTopicName() + " đã được tạo!";
    }

    @Override
    public List<TopicDescribeResponse> getAllTopicDetail() throws ExecutionException, InterruptedException {
        List<TopicDescribeResponse> res = new ArrayList<>();
        List<String> topicNames = new ArrayList<>(getAllListTopic());
        DescribeTopicsResult describeResult = adminClient.describeTopics(topicNames);
        Map<String, KafkaFuture<TopicDescription>> desc = describeResult.topicNameValues();
        for (String topic : desc.keySet()) {
            TopicDescription detail = desc.get(topic).get();
            res.add(TopicDescribeMapper.format(detail));
        }
        return res;
    }

    @Override
    public TopicDescribeResponse getATopicDetail(String topicName) throws ExecutionException, InterruptedException {
        List<String> topicNames = new ArrayList<>(List.of(topicName));
        DescribeTopicsResult describeResult = adminClient.describeTopics(topicNames);
        TopicDescription t = describeResult.topicNameValues().get(topicName).get();
        return TopicDescribeMapper.format(t);
    }

    @Override
    public void getAllConsumerGroups() throws ExecutionException, InterruptedException {
        ListGroupsResult result = adminClient.listGroups();
        List<String> groupNames = result.all().get().stream().map(GroupListing::groupId).toList();

        DescribeConsumerGroupsResult desc = adminClient.describeConsumerGroups(groupNames);

        desc.all().get().forEach((groupId, description) -> {
            System.out.println("Group ID: " + groupId);
            System.out.println("Coordinator: " + description.coordinator());
            System.out.println("Members: ");
            description.members().forEach(member -> {
                System.out.println("\tMember ID: " + member.consumerId());
                System.out.println("\tClient ID: " + member.clientId());
                System.out.println("\tHost: " + member.host());
                System.out.println("\tAssigned Partitions: " + member.assignment().topicPartitions());
            });
        });

        ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets("thanh-group1");
        Map<TopicPartition, OffsetAndMetadata> offsets = offsetsResult.partitionsToOffsetAndMetadata().get();

        Map<TopicPartition, Long> endOffsets = adminClient.listOffsets(
                        offsets.keySet().stream().collect(
                                HashMap::new,
                                (m, tp) -> m.put(tp, OffsetSpec.latest()),
                                HashMap::putAll
                        )
                ).all().get().entrySet().stream()
                .collect(HashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue().offset()),
                        HashMap::putAll);

        // 4️⃣ In thông tin offset và lag
        offsets.forEach((tp, offsetAndMeta) -> {
            long committed = offsetAndMeta.offset();
            long latest = endOffsets.getOrDefault(tp, committed);
            long lag = latest - committed;

            System.out.println("TopicPartition: " + tp);
            System.out.println("\tCommitted offset: " + committed);
            System.out.println("\tLatest offset: " + latest);
            System.out.println("\tLag: " + lag);
        });

    }

    @Override
    public String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        DeleteTopicsResult result = adminClient.deleteTopics(Collections.singleton(topicName));
        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã xóa thành công topic: " + topicName;
    }

    @Override
    public String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        NewPartitions newPartitions = NewPartitions.increaseTo(partitionNum);
        CreatePartitionsResult result = adminClient.createPartitions(
                Collections.singletonMap(topicName, newPartitions));

        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã tăng partition của topic " + topicName + " lên " + partitionNum;
    }
}
