package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.service.IConsumerService;
import com.thanhpham.Kafka.utils.Constants;
import com.thanhpham.Kafka.utils.InitConsumerProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.GroupListing;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.GroupState;
import org.apache.kafka.common.GroupType;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsumerService implements IConsumerService {
    private final AdminClient adminClient;

    @Override
    public List<GroupListing> getAllConsumerGroups() throws ExecutionException, InterruptedException {
        Collection<GroupListing> groups = adminClient.listGroups().valid().get();
        for (GroupListing group : groups) {
            String groupId = group.groupId();
            Optional<GroupType> groupTypeOpt = group.type();
            Optional<GroupState> groupStateOpt = group.groupState();

            String typeStr = groupTypeOpt.isPresent() ? groupTypeOpt.get().toString() : "UNKNOWN";
            String stateStr = groupStateOpt.isPresent() ? groupStateOpt.get().toString() : "UNKNOWN";
            System.out.printf("Group ID: %s | Type: %s | State: %s%n",
                    groupId, typeStr, stateStr);
        }
        return groups.stream().toList();
    }

    @Override
    public void readMessage(String topicName) {
        Properties consumerProps = InitConsumerProps.initProps();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            // 1. Lấy thông tin partition của topic
            List<TopicPartition> topicPartitions =  getListTopicPart(consumer, topicName);

            // 2. Gán thủ công các partition (không dùng subscribe)
            consumer.assign(topicPartitions);

            // 3. Đưa con trỏ về cuối để lấy end offset
            consumer.seekToEnd(topicPartitions);

            // Lấy end offset hiện tại của từng partition
            Map<TopicPartition, Long> endOffsets = topicPartitions.stream()
                    .collect(Collectors.toMap(tp -> tp, consumer::position));

            // Seek về vị trí (end - N), nhưng không nhỏ hơn 0
            for (TopicPartition tp : topicPartitions) {
                long targetOffset = endOffsets.get(tp) - Constants.LAST_N;
                long seekTo = Math.max(0L, targetOffset);
                consumer.seek(tp, seekTo);
                System.out.printf("Partition %d: đọc từ offset %d (end=%d)%n",
                        tp.partition(), seekTo, endOffsets.get(tp));
            }

            // 4. Poll 1 lần (đủ để lấy các message từ offset đã seek)
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(30));

            System.out.println("\n=== " + records.count() + " message mới nhất trong topic '" + topicName + "' ===");
            for (ConsumerRecord<String, String> r : records) {
                System.out.printf("partition=%d | offset=%d | key=%s | value=%s%n",
                        r.partition(), r.offset(),
                        r.key() == null ? "null" : r.key(),
                        r.value() == null ? "null" : r.value());
            }

            if (records.isEmpty()) {
                System.out.println("Không có message nào trong " + Constants.LAST_N + " bản ghi cuối cùng (topic có thể trống).");
            }
        }
    }

    private List<TopicPartition> getListTopicPart (KafkaConsumer<String, String> consumer, String topicName){
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
        if (partitionInfos == null || partitionInfos.isEmpty()) {
            System.out.println("Topic không tồn tại hoặc chưa có partition!");
            return new ArrayList<>();
        }

        return partitionInfos.stream()
                .map(pi -> new TopicPartition(pi.topic(), pi.partition()))
                .collect(Collectors.toList());
    }

    private void lagChecker() throws ExecutionException, InterruptedException {
        List<GroupListing> groups = getAllConsumerGroups();
        System.out.println("=== TỔNG SỐ CONSUMER GROUP: " + groups.size() + " ===\n");
        for (GroupListing group : groups) {
            String groupId = group.groupId();
            try {
                // 2. Lấy committed offset của group này
                Map<TopicPartition, OffsetAndMetadata> committed = adminClient
                        .listConsumerGroupOffsets(groupId)
                        .partitionsToOffsetAndMetadata()
                        .get();

                if (committed.isEmpty()) {
                    System.out.printf("Group %s: không có committed offset (có thể chưa consume)%n%n", groupId);
                    continue;
                }

                // 3. Lấy end offset (log-end-offset) của các partition đó
                Set<TopicPartition> partitions = committed.keySet();
                Map<TopicPartition, Long> endOffsets = admin.endOffsets(partitions).get();
                // 4. Tính lag và in ra
                System.out.printf("Consumer Group: %s%n", groupId);
                System.out.println("Topic                    | Partition | Committed | End Offset | Lag");
                System.out.println("-------------------------+-----------+-----------+------------+-----------");

                long totalLag = 0;
                for (TopicPartition tp : partitions) {
                    long committedOffset = committed.get(tp).offset();
                    long endOffset = endOffsets.get(tp);
                    long lag = endOffset - committedOffset;
                    totalLag += lag;

                    System.out.printf("%-24s | %9d | %9d | %10d | %8d%n",
                            tp.topic(),
                            tp.partition(),
                            committedOffset,
                            endOffset,
                            lag);
                }
                System.out.printf("%nTổng lag của group %s = %,d message%n%n", groupId, totalLag);
            } catch (Exception e) {
                System.out.printf("Không thể lấy offset cho group %s: %s%n%n", groupId, e.getMessage());
            }
        }
    }
}
