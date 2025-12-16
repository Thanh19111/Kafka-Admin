package com.thanhpham.Kafka.service.TopicService;

import com.thanhpham.Kafka.config.pool.AdminClientPool.IAdminClientPool;
import com.thanhpham.Kafka.dto.request.ConfigItem;
import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponseWithConfig;
import com.thanhpham.Kafka.mapper.TopicDetailMapper;
import com.thanhpham.Kafka.utils.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class TopicService implements ITopicService {
    private final IAdminClientPool adminClientPool;

    public Set<String> getAllListTopic() throws ExecutionException, InterruptedException {
        return adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .listTopics()
                .names()
                .get();
    }

    @Override
    public String createNewTopic(@Valid TopicCreateRequest request) throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic(request.getTopicName(), request.getPartitionNum(), request.getReplicaFNum());

        newTopic.configs(convertConfigListToConfigMap(request.getConfig()));

        adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .createTopics(Collections.singleton(newTopic))
                .all()
                .get();
        return "Topic " + request.getTopicName() + " has been created!";
    }

    @Override
    public List<TopicDetailResponse> getAllTopicDetail() throws ExecutionException, InterruptedException {
        List<TopicDetailResponse> res = new ArrayList<>();
        List<String> topicNames = new ArrayList<>(getAllListTopic());
        DescribeTopicsResult describeResult = adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .describeTopics(topicNames);

        Map<String, KafkaFuture<TopicDescription>> desc = describeResult.topicNameValues();
        for (String topic : desc.keySet()) {
            TopicDescription detail = desc.get(topic).get();
            res.add(TopicDetailMapper.toResponse(detail));
        }
        return res;
    }

    @Override
    public TopicDetailResponse getATopicDetail(String topicName) throws ExecutionException, InterruptedException {
        List<String> topicNames = new ArrayList<>(List.of(topicName));
        DescribeTopicsResult describeResult = adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .describeTopics(topicNames);
        TopicDescription t = describeResult.topicNameValues()
                .get(topicName)
                .get();
        return TopicDetailMapper.toResponse(t);
    }

    @Override
    public TopicDetailResponseWithConfig getATopicDetailWithConfig(String topicName) throws ExecutionException, InterruptedException {
        List<Pair> configs = getTopicConfigByTopicName(topicName);
        TopicDetailResponse topic = getATopicDetail(topicName);
        return new TopicDetailResponseWithConfig(topic, configs);
    }

    @Override
    public String deleteTopic(String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        DeleteTopicsResult result = adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .deleteTopics(Collections.singleton(topicName));
        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Topic " + topicName + " has been deleted!";
    }

    @Override
    public String increasePartition(String topicName, int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        NewPartitions newPartitions = NewPartitions.increaseTo(partitionNum);
        CreatePartitionsResult result = adminClientPool.get(Constants.BOOTSTRAP_SERVERS)
                .createPartitions(Collections.singletonMap(topicName, newPartitions));

        result.all().get(Constants.ADJUST_TOPIC_MAX_TIMEOUT_CONFIG, TimeUnit.MILLISECONDS);
        return "Đã tăng partition của topic " + topicName + " lên " + partitionNum;
    }

    // internal
    private List<Pair> getTopicConfigByTopicName(String topicName) throws ExecutionException, InterruptedException {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
        DescribeConfigsResult result = adminClientPool.get(Constants.BOOTSTRAP_SERVERS).describeConfigs(List.of(resource));
        Config config = result.all().get().get(resource);
        List<Pair> configs = new ArrayList<>();
        for (ConfigEntry entry: config.entries()){
            configs.add(new Pair(entry.name(), entry.value()));
        }
        return configs;
    }

    private Map<String, String> convertConfigListToConfigMap(List<ConfigItem> list) {
        Map<String, String> map = new HashMap<>();
        for(ConfigItem item : list) {
            map.put(item.getAttribute(), item.getValue());
        }
        return map;
    }

}
