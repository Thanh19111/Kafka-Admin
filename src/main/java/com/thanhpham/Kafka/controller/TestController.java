package com.thanhpham.Kafka.controller;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.TopicDescribeResponse;
import com.thanhpham.Kafka.service.IConsumerService;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final ITopicService iTopicService;
    private final IConsumerService iConsumerService;

    @GetMapping
    public Set<String> getAllListTopic() throws ExecutionException, InterruptedException {
        return iTopicService.getAllListTopic();
    }

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

    @PostMapping("/config")
    public void checkLag(@RequestBody TopicCreateRequest request){
        System.out.println(request.toString());
    }

    @GetMapping("/group")
    public void getAllGroup() throws ExecutionException, InterruptedException {
        iTopicService.getAllConsumerGroups();
    }

    @GetMapping("/group-csm")
    public void getAllConsumerGroups() throws ExecutionException, InterruptedException {
        iConsumerService.getAllConsumerGroups();
    }
}
