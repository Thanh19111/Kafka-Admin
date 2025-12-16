package com.thanhpham.Kafka.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.dto.response.AvroMessage;
import com.thanhpham.Kafka.dto.response.JsonMessage;
import com.thanhpham.Kafka.service.MessageService.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final IMessageService iMessageService;

    @GetMapping("/avro")
    public List<AvroMessage> getAvroMessage(@RequestParam("topicName") String topicName) {
        return iMessageService.decodeAvro(topicName);
    }

    @GetMapping("/json")
    public List<JsonMessage> getJsonMessage(@RequestParam("topicName") String topicName) {
        return iMessageService.decodeJson(topicName);
    }
}
