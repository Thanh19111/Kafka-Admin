package com.thanhpham.Kafka.controller.fe;

import com.thanhpham.Kafka.dto.response.GroupDetailResponse;
import com.thanhpham.Kafka.dto.response.GroupPartitionResponse;
import com.thanhpham.Kafka.dto.response.MessageSlice;
import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.mapper.ConsumerGroupUIMapper;
import com.thanhpham.Kafka.service.consumergroup.IGroupConsumerService;
import com.thanhpham.Kafka.dto.ui.ConsumerGroupMemberUI;
import com.thanhpham.Kafka.dto.ui.ConsumerGroupDetailUI;
import com.thanhpham.Kafka.service.message.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/group")
@RequiredArgsConstructor
public class ConsumerGroupUIController {
    private final IGroupConsumerService iGroupConsumerService;
    private final IMessageService iMessageService;

    @GetMapping
    public String getConsumerGroupListUI(Model model, @RequestParam(defaultValue = "none") String searchByName, @RequestParam(defaultValue = "none") String filter) throws ExecutionException, InterruptedException {
        // lay thong tin chung cua cac consumer group
        List<GroupDetailResponse> data = iGroupConsumerService.getAllConsumerGroups();

        Map<String, List<GroupPartitionResponse>> offsetAndLag = new HashMap<>();

        for (GroupDetailResponse group : data) {
            offsetAndLag.put(group.getGroupId(), iGroupConsumerService.checkLagByGroupId(group.getGroupId()));
        }

        List<ConsumerGroupDetailUI> groups = data.stream().map(ConsumerGroupUIMapper::format).toList();

        for (ConsumerGroupDetailUI groupDetailUI: groups) {
            List<GroupPartitionResponse> partitionDetail = offsetAndLag.get(groupDetailUI.getConsumerGroupId());
            // tim max offset cua partition

            try {
                GroupPartitionResponse maxOffset = partitionDetail.stream().reduce(partitionDetail.getFirst(), (a, b) -> a.getLatestOffset() > b.getLatestOffset() ? a : b);
                groupDetailUI.setLatestOffset(maxOffset.getLatestOffset());

                // tim so luong topic
                int count = (int) partitionDetail.stream().map(GroupPartitionResponse::getTopic).distinct().count();
                groupDetailUI.setTopicNum(count);

                // tim tong lag
                long lagSum = partitionDetail.stream().map(GroupPartitionResponse::getLag).reduce(partitionDetail.getFirst().getLag(), Long::sum);
                groupDetailUI.setMessageBehind(lagSum);
                // end

            } catch (Exception e) {
                System.out.println(groupDetailUI.getConsumerGroupId()+ ": Error for calculating offset and lag");
            }
        }

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

    @GetMapping("/{groupId}/latest")
    public String test(@PathVariable("groupId") String groupId, Model model) throws ExecutionException, InterruptedException {
        List<GroupPartitionResponse> groupLags = iGroupConsumerService.checkLagByGroupId(groupId);
        model.addAttribute("groupLags", groupLags);
        return "components/GroupPartitionDetail/index :: partitionDetail";
    }

    @PostMapping("/message")
    public String test1(@RequestParam("topic") String topic, @RequestParam("partition") int partition, @RequestParam("format") String format, @RequestParam("startOffset") long startOffset, @RequestParam("endOffset") Long endOffset, Model model) {
        List<MessageSlice> messages = new ArrayList<>();

        if(format.equals("avro")) {
            messages = iMessageService.readAvroMessageByOffset(topic, partition, startOffset, endOffset);
        } else if(format.equals("json")) {
            messages = iMessageService.readJsonMessageByOffset(topic, partition, startOffset, endOffset);
        }

        model.addAttribute("messages", messages);
        model.addAttribute("topic", topic);
        model.addAttribute("partition", partition);
        return "components/MessageByOffset/index :: messageByOffset";
    }

    @PostMapping("/changeOffset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void test2(@RequestParam("groupId") String groupId, @RequestParam("topic") String topic, @RequestParam("partition") int partition, @RequestParam("offset") long offset) throws ExecutionException, InterruptedException {
        iGroupConsumerService.changeOffset(groupId, topic, partition, offset);
    }

    @GetMapping("/partition/{groupId}")
    public String t(@PathVariable("groupId") String groupId, @RequestParam("topic") String topic, Model model) throws ExecutionException, InterruptedException {
        List<Integer> partitions = iGroupConsumerService.checkLagByGroupId(groupId)
                .stream()
                .filter(group -> group.getTopic().equals(topic))
                .map(GroupPartitionResponse::getPartition)
                .toList();

        model.addAttribute("partitions", partitions);
        return "components/PartitionSlice/index :: partitionSlice";
    }
}
