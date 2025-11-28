package com.thanhpham.Kafka.controller;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.service.IConsumerService;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Set<String> test() throws ExecutionException, InterruptedException {
        return iTopicService.getList();
    }

    @GetMapping("/all")
    public void getAll() throws ExecutionException, InterruptedException {
        iTopicService.getAllTopicDetail();
    }

    @PostMapping
    public String createNewTopic(@RequestParam("topicName") String topicName, @RequestParam("partitionNum") int partitionNum, @RequestParam("replicaFNum") int  replicaFNum) throws ExecutionException, InterruptedException {
        iTopicService.createNewTopic(topicName, partitionNum, replicaFNum);
        return "New topic was created!";
    }

    @DeleteMapping
    public String deleteTopic(@RequestParam("topicName") String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        iTopicService.deleteTopic(topicName);
        return "Topic was deleted!";
    }

    @PostMapping("/adjust")
    public String increatePartition(@RequestParam("topicName") String topicName, @RequestParam("partitionNum") int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        iTopicService.increasePartition(topicName, partitionNum);
        return "Partition of topic was adjusted!";
    }

    @GetMapping("/message")
    public void getMessage(@RequestParam("topicName") String topicName){
        iConsumerService.readMessage(topicName);
    }

    @GetMapping("/group")
    public void getMessage() throws ExecutionException, InterruptedException {
        iConsumerService.getAllConsumerGroups();
    }

    @GetMapping("/check")
    public void checkLag() throws ExecutionException, InterruptedException {
        iConsumerService.checkLag(Constants.BOOTSTRAP_SERVERS, "thanh", "thanh-group");
    }

    @PostMapping("/config")
    public void checkLag(@RequestBody TopicCreateRequest request){
        System.out.println(request.toString());
    }
}
