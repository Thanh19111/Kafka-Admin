package com.thanhpham.Kafka.controller.fe;

import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.dto.response.TopicDetailResponse;
import com.thanhpham.Kafka.dto.response.TopicDetailResponseWithConfig;
import com.thanhpham.Kafka.mapper.TopicUIMapper;
import com.thanhpham.Kafka.service.ITopicService;
import com.thanhpham.Kafka.utils.uiformat.TopicDetailUI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String home(Model model) throws ExecutionException, InterruptedException {
        List<TopicDetailResponse> data = iTopicService.getAllTopicDetail();
        List<TopicDetailUI> topics = data.stream().map(TopicUIMapper::format).toList();
        model.addAttribute("topics", topics);
        return "TopicList";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Map<String, List<String>> configMap = new HashMap<>();
        configMap.put("cleanup.policy", List.of("delete", "compact"));
        configMap.put("compression.type", List.of("gzip", "lz4", "snappy"));
        configMap.put("acks", List.of("0", "1", "all"));

        model.addAttribute("configs", configMap);

        return "CreateNewTopic";
    }

    // do lười nên truyền nhiều giá trị
    @GetMapping("/{topicName}")
    public String topicDetail(@PathVariable String topicName, Model model) throws ExecutionException, InterruptedException {
        TopicDetailResponseWithConfig topic = iTopicService.getATopicDetailWithConfig(topicName);
        TopicDetailUI res = TopicUIMapper.format(topic.getTopic());
        List<Pair> configs = topic.getConfig();
        model.addAttribute("detail", res);
        model.addAttribute("id", topic.getTopic().getTopicId());
        model.addAttribute("partitions", topic.getTopic().getPartitions());
        model.addAttribute("configs", configs);
        return "TopicDetail";
    }

    @GetMapping("/test")
    public String testTheme(Model model) throws ExecutionException, InterruptedException {
        List<TopicDetailResponse> data = iTopicService.getAllTopicDetail();
        List<TopicDetailUI> topics = data.stream().map(TopicUIMapper::format).toList();

        List<Pair> navLabels = new ArrayList<>();
        navLabels.add(new Pair("Topics", "/topic"));
        navLabels.add(new Pair("List of Topics", "/topic"));
        model.addAttribute("topics", topics);
        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Topics");
        return "pages/Topic/TopicList/index";
    }

}
