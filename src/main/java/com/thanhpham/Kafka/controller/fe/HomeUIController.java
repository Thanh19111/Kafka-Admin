package com.thanhpham.Kafka.controller.fe;

import com.thanhpham.Kafka.dto.response.Pair;
import com.thanhpham.Kafka.dto.ui.BufferUI;
import com.thanhpham.Kafka.dto.ui.CpuUsageUI;
import com.thanhpham.Kafka.dto.ui.MessageCountUI;
import com.thanhpham.Kafka.dto.ui.UnderReplicaUI;
import com.thanhpham.Kafka.service.metric.MetricService;
import lombok.RequiredArgsConstructor;
import org.jolokia.client.exception.JolokiaException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.management.MalformedObjectNameException;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeUIController {
    private final MetricService metricService;
    @GetMapping
    public String home(Model model) throws MalformedObjectNameException, JolokiaException {
        Pair heap = metricService.heap();
        CpuUsageUI cpu = metricService.cpu();
        UnderReplicaUI underReplica = metricService.underReplica();
        MessageCountUI message = metricService.messageCount();

        model.addAttribute("heap", heap);
        model.addAttribute("cpu", cpu);
        model.addAttribute("underR", underReplica);
        model.addAttribute("message", message);

        Long totalGroupMember = metricService.getLongValueMetric("kafka.coordinator.group:type=GroupMetadataManager,name=NumTotalGroupMembers");
        Long totalEmptyGroup = metricService.getLongValueMetric("kafka.coordinator.group:type=GroupMetadataManager,name=NumGroupsEmpty");
        Long globalTopic = metricService.getLongValueMetric("kafka.controller:type=KafkaController,name=GlobalTopicCount");

        model.addAttribute("totalGroupMember", totalGroupMember);
        model.addAttribute("totalEmptyGroup", totalEmptyGroup);
        model.addAttribute("globalTopic", globalTopic);


        Long activeController = metricService.getLongValueMetric("kafka.controller:type=KafkaController,name=ActiveControllerCount");
        Long activeBroker = metricService.getLongValueMetric("kafka.controller:type=KafkaController,name=ActiveBrokerCount");
        Long degradedStorageBroker = metricService.getLongValueMetric("kafka.controller:type=KafkaController,name=BrokersWithDegradedStorageCount");

        model.addAttribute("activeController", activeController);
        model.addAttribute("activeBroker", activeBroker);
        model.addAttribute("degradedStorageBroker", degradedStorageBroker);


        Long offlinePartition = metricService.getLongValueMetric("kafka.controller:type=KafkaController,name=OfflinePartitionsCount");

        double d = metricService.getBigDecimalValueMetric("kafka.controller:type=KafkaController,name=PartitionAvailability").doubleValue();
        double availablePartitionRate = d * 100.0;

        Long globalPartition = metricService.getLongValueMetric("kafka.controller:type=KafkaController,name=GlobalPartitionCount");
        Long underMinISRPartition = metricService.getLongValueMetric("kafka.controller:type=KafkaController,name=GlobalUnderMinIsrPartitionCount");

        model.addAttribute("offlinePartition", offlinePartition);
        model.addAttribute("availablePartitionRate", availablePartitionRate);
        model.addAttribute("globalPartition", globalPartition);
        model.addAttribute("underMinISRPartition", underMinISRPartition);

        BufferUI buffer = metricService.buffer();
        Long bufferUsed = buffer.getMemoryUsed();
        Long totalBuffer = buffer.getTotalCapacity();

        float bufferUsedRate = (float) (Math.round((float) bufferUsed / totalBuffer * 100) * 100.0 / 100.0);

        model.addAttribute("bufferUsedRate", bufferUsedRate);
        model.addAttribute("bufferUsed", bufferUsed);

        return "pages/Home/index";
    }
}
