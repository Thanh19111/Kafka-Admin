package com.thanhpham.Kafka.controller.fe;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupMemberResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.mapper.ConsumerGroupUIMapper;
import com.thanhpham.Kafka.service.IGroupConsumerService;
import com.thanhpham.Kafka.utils.uiformat.ConsumerGroupMemberUI;
import com.thanhpham.Kafka.utils.uiformat.GroupConsumerDetailUI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/group")
@RequiredArgsConstructor
public class ConsumerGroupUIController {
    private final IGroupConsumerService iGroupConsumerService;

    @GetMapping
    public String getConsumerGroupListUI(Model model) throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> data = iGroupConsumerService.getAllConsumerGroups();
        List<GroupConsumerDetailUI> groups = data.stream().map(ConsumerGroupUIMapper::format).toList();

        List<Pair> navLabels = new ArrayList<>();
        navLabels.add(new Pair("Consumers", "/group"));
        navLabels.add(new Pair("List of Consumers", "/group"));
        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Consumer");
        model.addAttribute("activeNavIndex", 1);

        model.addAttribute("groups", groups);
        return "pages/ConsumerGroup/ConsumerGroupList/index";
    }

    @GetMapping("/{groupId}")
    public String getGroupDetailUI(@PathVariable("groupId") String groupId, Model model) throws ExecutionException, InterruptedException {
        GroupDetailResponse group = iGroupConsumerService.getAllConsumerGroups().stream().filter(
                groupDetailResponse -> Objects.equals(groupDetailResponse.getGroupId(), groupId)
        ).toList().getFirst();
        List<GroupPartitionResponse> groupLags = iGroupConsumerService.checkLagByGroupId(groupId);

        List<Pair> navLabels = new ArrayList<>();
        navLabels.add(new Pair("Consumers", "/group"));
        navLabels.add(new Pair(group.getGroupId(), "/group/" + group.getGroupId()));
        navLabels.add(new Pair("member", "/group/member/" + group.getGroupId()));

        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Consumer");
        model.addAttribute("activeNavIndex", 1);

        model.addAttribute("group", group);
        model.addAttribute("groupLags", groupLags);
        return "pages/ConsumerGroup/ConsumerGroupDetail/index";
    }

    @GetMapping("/member/{groupId}")
    public String getMemberListUI(@PathVariable("groupId") String groupId, Model model) throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> data = iGroupConsumerService.getAllConsumerGroups().stream().filter(
                groupDetailResponse -> Objects.equals(groupDetailResponse.getGroupId(), groupId)
        ).toList();
        List<ConsumerGroupMemberUI> members = data.getFirst().getMembers().stream().map(ConsumerGroupUIMapper::format).toList();

        List<Pair> navLabels = new ArrayList<>();
        navLabels.add(new Pair("Consumers", "/group"));
        navLabels.add(new Pair(groupId, "/group/" + groupId));
        navLabels.add(new Pair("member", "/group/member/" + groupId));

        model.addAttribute("navLabels", navLabels);
        model.addAttribute("mainLabel", "Consumer");
        model.addAttribute("activeNavIndex", 2);

        model.addAttribute("members", members);
        model.addAttribute("groupId", groupId);
        return "pages/ConsumerGroup/ConsumerGroupMemberList/index";
    }
}
