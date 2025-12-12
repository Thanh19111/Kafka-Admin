package com.thanhpham.Kafka.service.impl;

import com.thanhpham.Kafka.config.pool.AdminClientPool;
import com.thanhpham.Kafka.config.pool.AvroConsumerPool;
import com.thanhpham.Kafka.service.IConsumerService;
import com.thanhpham.Kafka.utils.Constants;
import com.thanhpham.Kafka.utils.InitConsumerProps;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
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
    private final AdminClientPool adminClientPool;
    private final AvroConsumerPool avroPool;

    @Override
    public List<GroupListing> getAllConsumerGroups() throws ExecutionException, InterruptedException {
        Collection<GroupListing> groups = adminClientPool.get("localhost:9092").listGroups().valid().get();
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
            List<TopicPartition> topicPartitions =  getListTopicPartition(consumer, topicName);
            consumer.assign(topicPartitions);
            consumer.seekToEnd(topicPartitions);

            Map<TopicPartition, Long> endOffsets = topicPartitions.stream()
                    .collect(Collectors.toMap(tp -> tp, consumer::position));

            for (TopicPartition tp : topicPartitions) {
                long targetOffset = endOffsets.get(tp) - Constants.LAST_N;
                long seekTo = Math.max(0L, targetOffset);
                consumer.seek(tp, seekTo);
                System.out.printf("Partition %d: đọc từ offset %d (end=%d)%n",
                        tp.partition(), seekTo, endOffsets.get(tp));
            }

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

    private List<TopicPartition> getListTopicPartition (KafkaConsumer<String, String> consumer, String topicName){
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
        if (partitionInfos == null || partitionInfos.isEmpty()) {
            System.out.println("Topic không tồn tại hoặc chưa có partition!");
            return new ArrayList<>();
        }

        return partitionInfos.stream()
                .map(pi -> new TopicPartition(pi.topic(), pi.partition()))
                .collect(Collectors.toList());
    }

    @Override
    public void checkLag(String bootstrapServers, String topicName, String groupId) throws ExecutionException, InterruptedException {
        // lay nhieu partition hon
        Map<TopicPartition, OffsetSpec> req = Map.of(
                new TopicPartition("thanh", 0), OffsetSpec.latest(),
                new TopicPartition("thanh", 1), OffsetSpec.latest()
        );

        ListOffsetsResult latestResult = adminClientPool.get("localhost:9092").listOffsets(req);

        for (TopicPartition tp : req.keySet()) {
            long latest = latestResult.partitionResult(tp).get().offset();

            long committed = adminClientPool.get("localhost:9092")
                    .listConsumerGroupOffsets("thanh-group")
                    .partitionsToOffsetAndMetadata()
                    .get()
                    .get(tp)
                    .offset();

            long lag = latest - committed;

            System.out.println(tp + " offset = " + committed + " " + tp + "\\ lag = " + lag);
        }
    }

    @Override
    public void getMessage(){
        String topic = "avro";
        Consumer<String, GenericRecord> consumer = avroPool.get(topic);
        consumer.poll(Duration.ofMillis(100));

        Set<TopicPartition> partitions = consumer.assignment();

        for (TopicPartition partition : partitions) {
            long end = consumer.endOffsets(List.of(partition)).get(partition);
            long offset = Math.max(end - 1, 0);  // Lùi lại 1 record
            consumer.seek(partition, offset);
        }

        while (true) {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, GenericRecord> record : records) {
                System.out.println("Last message:");
                System.out.println(record.value());
                break;
            }
            break;
        }
    }
}
