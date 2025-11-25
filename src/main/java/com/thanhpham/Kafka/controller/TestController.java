package com.thanhpham.Kafka.controller;

import com.thanhpham.Kafka.service.ITopicService;
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
}
