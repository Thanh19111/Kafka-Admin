package com.thanhpham.Kafka.controller.fe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanhpham.Kafka.dto.form.PartitionIncreaseRequest;
import com.thanhpham.Kafka.dto.form.TopicCreateForm;
import com.thanhpham.Kafka.dto.request.ConfigItem;
import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponseWithConfig;
import com.thanhpham.Kafka.mapper.TopicUIMapper;
import com.thanhpham.Kafka.service.TopicService.ITopicService;
import com.thanhpham.Kafka.dto.uiformat.TopicDetailUI;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Controller
@RequestMapping("/topic")
@RequiredArgsConstructor
public class TopicUIController {
    private final ITopicService iTopicService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String getTopicListUI(Model model) throws ExecutionException, InterruptedException {
        List<TopicDetailResponse> data = iTopicService.getAllTopicDetail();
        List<TopicDetailUI> topics = data.stream().map(TopicUIMapper::format).toList();

        List<Pair> navLabels = new ArrayList<>();
        navLabels.add(new Pair("Topics", "/topic"));
        navLabels.add(new Pair("List of Topics", "/topic"));

        model.addAttribute("activeNavIndex", 1);
        model.addAttribute("topics", topics);
        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Topics");
        return "pages/Topic/TopicList/index";
    }

    @GetMapping("/create")
    public String getCreateNewTopicUI(Model model) {
        List<Pair> navLabels = new ArrayList<>();
        navLabels.add(new Pair("Topics", "/topic"));
        navLabels.add(new Pair("List of Topics", "/topic"));
        navLabels.add(new Pair("Create New Topic", "/topic/create"));

        Map<String, List<String>> configMap = new HashMap<>();
        configMap.put("cleanup.policy", List.of("delete", "compact"));
        configMap.put("compression.type", List.of("gzip", "lz4", "snappy"));

        model.addAttribute("activeNavIndex", 2);
        model.addAttribute("configMap", configMap);
        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Topics");

        TopicCreateForm form = new TopicCreateForm();
        form.setPartitionNum(1);
        form.setReplicaFNum((short) 1);
        model.addAttribute("topicForm", form);

        return "pages/Topic/CreateNewTopic/index";
    }

    @GetMapping("/{topicName}")
    public String getTopicDetailUI(@PathVariable String topicName, Model model) throws ExecutionException, InterruptedException {
        TopicDetailResponseWithConfig topic = iTopicService.getATopicDetailWithConfig(topicName);
        TopicDetailUI res = TopicUIMapper.format(topic.getTopic());
        List<Pair> configs = topic.getConfig();
        List<Pair> navLabels = new ArrayList<>();

        navLabels.add(new Pair("Topics", "/topic"));
        navLabels.add(new Pair("List of Topics", "/topic"));
        navLabels.add(new Pair(topic.getTopic().getTopicName(), "/topic/" + topic.getTopic().getTopicName()));

        model.addAttribute("activeNavIndex", 2);
        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Topics");

        model.addAttribute("detail", res);
        model.addAttribute("id", topic.getTopic().getTopicId());
        model.addAttribute("partitions", topic.getTopic().getPartitions());
        model.addAttribute("configs", configs);
        return "pages/Topic/TopicDetail/index";
    }

    // filter by topic Name

    @GetMapping("/filter")
    public String filter(@RequestParam String keyword, Model model) throws ExecutionException, InterruptedException {

        List<TopicDetailResponse> data = iTopicService.getAllTopicDetail();
        List<TopicDetailUI> topics = data.stream()
                .filter(topic -> topic.getTopicName().contains(keyword))
                .map(TopicUIMapper::format).toList();

        model.addAttribute("topiclist", topics);
        return "components/TopicList/index :: topiclist";
    }

    @PostMapping("/create")
    public Object checkTopicCreationRequest(@Valid @ModelAttribute("topicForm") TopicCreateForm topicForm,
                                            BindingResult bindingResult,
                                            Model model) throws JsonProcessingException, ExecutionException, InterruptedException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("topicForm", topicForm);
            return "components/TopicForm/index :: form";
        }

        List<ConfigItem> configs = objectMapper.readValue(topicForm.getConfig(), new TypeReference<>() {});

        TopicCreateRequest request = new TopicCreateRequest();
        request.setTopicName(topicForm.getTopicName());
        request.setPartitionNum(topicForm.getPartitionNum());
        request.setReplicaFNum(topicForm.getReplicaFNum());
        request.setConfig(configs);

        iTopicService.createNewTopic(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("HX-Redirect", "/topic")
                .build();
    }

    @DeleteMapping("/{topicName}")
    public Object deleteTopicByName(@PathVariable("topicName") String topicName) throws ExecutionException, InterruptedException, TimeoutException {
        iTopicService.deleteTopic(topicName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("HX-Redirect", "/topic")
                .build();
    }

    @PutMapping("/adjust")
    public Object increasePartitionByTopicName(@ModelAttribute PartitionIncreaseRequest request){
        try {
            iTopicService.increasePartition(request.getTopicName(), request.getPartitionNumber());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("HX-Redirect", "/topic")
                .header("HX-Trigger", "toast-success")
                .build();
    }
}
