package com.thanhpham.Kafka.controller;

import com.thanhpham.Kafka.dto.request.SchemaCreateRequest;
import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.service.IConsumerService;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.service.impl.SchemaRegistry;
import com.thanhpham.Kafka.utils.Constants;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final ITopicService iTopicService;
    private final IConsumerService iConsumerService;
    private final SchemaRegistry schemaRegistryUtil;

    @PostMapping
    public String createNewTopic(@RequestBody TopicCreateRequest request) throws ExecutionException, InterruptedException {
        return iTopicService.createNewTopic(request);
    }

    @DeleteMapping
    public String deleteTopic(@RequestParam("topicName") String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        return iTopicService.deleteTopic(topicName);
    }

    @PostMapping("/adjust")
    public String increasePartition(@RequestParam("topicName") String topicName, @RequestParam("partitionNum") int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        return iTopicService.increasePartition(topicName, partitionNum);
    }

    @GetMapping("/message")
    public void getMessage(@RequestParam("topicName") String topicName){
        iConsumerService.readMessage(topicName);
    }


    @GetMapping("/check")
    public void checkLag() throws ExecutionException, InterruptedException {
        iConsumerService.checkLag(Constants.BOOTSTRAP_SERVERS, "thanh", "thanh-group");
    }
    /// schema

    @GetMapping("/schema")
    public void getAllSubject() throws RestClientException, IOException {
        schemaRegistryUtil.getAllSubject();
    }

    @PostMapping("/schema")
    public void createSchema(@RequestBody SchemaCreateRequest request) throws RestClientException, IOException {
        schemaRegistryUtil.createSchema(request);
    }

    @GetMapping("/schema/{subject}")
    public void get(@PathVariable("subject") String subject) throws RestClientException, IOException {
        schemaRegistryUtil.getSchemaBySubject(subject);
    }

    @PostMapping("read")
    public void readMessage() throws RestClientException, IOException {
        iConsumerService.getMessage();
    }

}
