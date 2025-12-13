package com.thanhpham.Kafka.controller.fe;

import com.thanhpham.Kafka.dto.request.TopicCreateRequest;
import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponseWithConfig;
import com.thanhpham.Kafka.dto.test.User;
import com.thanhpham.Kafka.mapper.TopicUIMapper;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.dto.uiformat.TopicDetailUI;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/topic")
@RequiredArgsConstructor
public class TopicUIController {
    private final ITopicService iTopicService;

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
        configMap.put("acks", List.of("0", "1", "all"));

        model.addAttribute("activeNavIndex", 2);
        model.addAttribute("configMap", configMap);
        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Topics");

        User user = new User("Alice", "alice@example.com");
        model.addAttribute("user", user);


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

        System.out.println(topics.size());

        model.addAttribute("topiclist", topics);
        return "components/TopicList/index :: topiclist";
    }

    @PostMapping("/create")
    public String checkTopicCreationRequest(@Valid TopicCreateRequest topicForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "pages/Topic/CreateNewTopic/index";
        }

        return "redirect:/index";
    }
}
