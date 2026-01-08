package com.thanhpham.Kafka.controller.fe;

import com.thanhpham.Kafka.dto.response.Pair;
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
import java.math.BigDecimal;

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
        return "pages/Home/index";
    }
}
