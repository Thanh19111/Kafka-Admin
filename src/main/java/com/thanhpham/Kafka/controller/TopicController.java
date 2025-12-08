package com.thanhpham.Kafka.controller;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.service.ITopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    private final ITopicService iTopicService;

    @GetMapping("/{topicName}")
    public TopicDetailResponse getTopicDetail(@PathVariable("topicName") String topicName) throws ExecutionException, InterruptedException {
        return iTopicService.getATopicDetail(topicName);
    }

    @GetMapping
    public List<TopicDetailResponse> getAllTopicDetail() throws ExecutionException, InterruptedException {
        return iTopicService.getAllTopicDetail();
    }

    @PostMapping
    public String createNewTopic(@RequestBody TopicCreateRequest request) throws ExecutionException, InterruptedException {
        return iTopicService.createNewTopic(request);
    }

    @PutMapping("/adjust")
    public String increasePartition(@RequestParam("topicName") String topicName, @RequestParam("partitionNum") int partitionNum) throws ExecutionException, InterruptedException, TimeoutException {
        return iTopicService.increasePartition(topicName, partitionNum);
    }

    @DeleteMapping("/{topicName}")
    public String deleteTopic(@PathVariable("topicName") String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        return iTopicService.deleteTopic(topicName);
    }
}
