package com.thanhpham.Kafka.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.thanhpham.Kafka.service.IMessageService;
import com.thanhpham.Kafka.service.impl.MessageService;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final IMessageService iMessageService;

    @GetMapping("/avro")
    public List<String> getAvroMessage(@RequestParam("topicName") String topicName, @RequestParam("limit") int limit) {
        return iMessageService.decodeAvro(topicName, limit);
    }

    @GetMapping("/json")
    public List<JsonNode> getJsonMessage(@RequestParam("topicName") String topicName, @RequestParam("limit") int limit) {
        return iMessageService.decodeJson(topicName, limit);
    }
}
