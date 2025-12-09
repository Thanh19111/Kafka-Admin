package com.thanhpham.Kafka.controller.fe;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupMemberResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/group")
@RequiredArgsConstructor
public class ConsumerGroupUIController {
    private final IGroupConsumerService iGroupConsumerService;
    @GetMapping
    public String home(Model model) throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> data = iGroupConsumerService.getAllConsumerGroups();
        List<GroupConsumerDetailUI> groups = data.stream().map(ConsumerGroupUIMapper::format).toList();
        model.addAttribute("groups", groups);
        return "ConsumerGroupList";
    }

    // endpoint này để sau
    @GetMapping("/demo")
    public String memberDetail(Model model) throws ExecutionException, InterruptedException {
//        List<GroupDetailResponse> data = iGroupConsumerService.getAllConsumerGroups();
//        List<GroupConsumerDetailUI> groups = data.stream().map(GroupConsumerUIMapper::format).toList();
//        model.addAttribute("groups", groups);
        return "ConsumerGroupMember";
    }

    @GetMapping("/member/{groupId}")
    public String memberList(@PathVariable("groupId") String groupId, Model model) throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> data = iGroupConsumerService.getAllConsumerGroups().stream().filter(
                groupDetailResponse -> Objects.equals(groupDetailResponse.getGroupId(), groupId)
        ).toList();

        List<ConsumerGroupMemberUI> members = data.getFirst().getMembers().stream().map(ConsumerGroupUIMapper::format).toList();
        model.addAttribute("members", members);
        model.addAttribute("groupId", groupId);
        return "ConsumerGroupMemberList";
    }

    @GetMapping("/{groupId}")
    public String groupDetail(@PathVariable("groupId") String groupId, Model model) throws ExecutionException, InterruptedException {
        List<GroupDetailResponse> data = iGroupConsumerService.getAllConsumerGroups().stream().filter(
                groupDetailResponse -> Objects.equals(groupDetailResponse.getGroupId(), groupId)
        ).toList();
        List<GroupPartitionResponse> groupLags = iGroupConsumerService.checkLagByGroupId(groupId);
        model.addAttribute("group", data.getFirst());
        model.addAttribute("groupLags", groupLags);
        return "ConsumerGroupDetail";
    }
}
