package com.thanhpham.Kafka.controller.api;

import com.thanhpham.Kafka.dto.response.MessageSlice;
import com.thanhpham.Kafka.service.message.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final IMessageService iMessageService;

    @GetMapping("/avro/{topicName}")
    public List<MessageSlice> getAvroMessage(@PathVariable("topicName") String topicName) {
        return iMessageService.decodeAvro(topicName);
    }

    @GetMapping("/json/{topicName}")
    public List<MessageSlice> getJsonMessage(@PathVariable("topicName") String topicName) {
        return iMessageService.decodeJson(topicName);
    }

    @GetMapping("/avro")
    public List<MessageSlice> readAvroMessageByOffset(@RequestParam("topic") String topic, @RequestParam("partition") int partition, @RequestParam("startOffset") long startOffset, @RequestParam("endOffset") long endOffset) {
        return iMessageService.readAvroMessageByOffset(topic, partition, startOffset, endOffset);
    }

    @GetMapping("/json")
    public List<MessageSlice> readJsonMessageByOffset(@RequestParam("topic") String topic, @RequestParam("partition") int partition, @RequestParam("startOffset") long startOffset, @RequestParam("endOffset") long endOffset) {
        return iMessageService.readJsonMessageByOffset(topic, partition, startOffset, endOffset);
    }
}
