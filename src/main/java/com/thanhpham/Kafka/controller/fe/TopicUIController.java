package com.thanhpham.Kafka.controller.fe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanhpham.Kafka.dto.form.PartitionIncreaseRequest;
import com.thanhpham.Kafka.dto.form.TopicCreateForm;
import com.thanhpham.Kafka.dto.request.ConfigItem;
import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.*;
import com.thanhpham.Kafka.mapper.TopicUIMapper;
import com.thanhpham.Kafka.service.message.IMessageService;
import com.thanhpham.Kafka.service.topic.ITopicService;
import com.thanhpham.Kafka.dto.uiformat.TopicDetailUI;
import com.thanhpham.Kafka.util.ListSlicer;
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
    private final IMessageService iMessageService;

    @GetMapping
    public String getTopicListUI(Model model) throws ExecutionException, InterruptedException {
        List<Pair> navLabels = new ArrayList<>();
        navLabels.add(new Pair("Topics", "/topic"));
        navLabels.add(new Pair("List of Topics", "/topic"));
        model.addAttribute("activeNavIndex", 1);
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
                .sorted(Comparator.comparing(TopicDetailResponse::getTopicName))
                .map(TopicUIMapper::format).toList();
        
        model.addAttribute("subList", topics);
        return "components/TopicListFilter/index :: topiclist";
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

    @GetMapping("/messages/avro/{topicName}")
    public Object getAvroMessage(@PathVariable("topicName") String topicName, Model model) {
        List<AvroMessage> messages = new ArrayList<>();
        try {
            messages = iMessageService.decodeAvro("pageviews");
        } catch (Exception e) {
            System.out.println("Parsing avro message has error");
        }

        model.addAttribute("messages", messages.reversed());
        return "components/MessageTemplate/index :: messageTemplate";
    }

    @GetMapping("/messages/json/{topicName}")
    public Object getJsonMessage(@PathVariable("topicName") String topicName, Model model) {
        List<JsonMessage> messages = new ArrayList<>();
        try {
            messages = iMessageService.decodeJson("pageviews");
        } catch (Exception e) {
            System.out.println("Parsing json message has error");
        }

        model.addAttribute("messages", messages.reversed());
        return "components/MessageTemplate/index :: messageTemplate";
    }

    @PostMapping("/change")
    public Object changeDecode(@RequestParam("mode") String mode, Model model){
        System.out.println("Change to " + mode);
        model.addAttribute("mode", mode);
        return "components/TemplateDecoder/index :: changeDecode";
    }

    @GetMapping("/paginate")
    public String t (@RequestParam(defaultValue = "0") int page, Model model) throws ExecutionException, InterruptedException {
        List<TopicDetailResponse> data = iTopicService.getAllTopicDetail();
        List<TopicDetailUI> topics = data.stream()
                .sorted(Comparator.comparing(TopicDetailResponse::getTopicName))
                .map(TopicUIMapper::format).toList();

        int pageSize = 8;

        List<TopicDetailUI> subList = ListSlicer.slice(topics, page, pageSize);

        model.addAttribute("subList", subList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) topics.size() / pageSize));
        return "components/TopicList/index :: topiclist";
    }
}
