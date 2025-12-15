package com.thanhpham.Kafka.controller.api;

import com.thanhpham.Kafka.dto.request.SchemaCreateRequest;
import com.thanhpham.Kafka.service.IConsumerService;
import com.thanhpham.Kafka.service.IMessageService;
import com.thanhpham.Kafka.service.impl.SchemaRegistry;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final IConsumerService iConsumerService;
    private final SchemaRegistry schemaRegistryUtil;
    private final IMessageService iMessageService;

    /// schema
    @GetMapping("/schema")
    public void getAllSubject() throws RestClientException, IOException {
        schemaRegistryUtil.getAllSubject();
    }

    @PostMapping("/schema")
    public void createSchema(@RequestBody SchemaCreateRequest request) throws RestClientException, IOException {
        schemaRegistryUtil.createSchema(request);
    }

    @GetMapping("/schema/{subject}")
    public void get(@PathVariable("subject") String subject) throws RestClientException, IOException {
        schemaRegistryUtil.getSchemaBySubject(subject);
    }

    @PostMapping("read")
    public void readMessage() throws RestClientException, IOException {
        iConsumerService.getMessage();
    }

    @GetMapping("/check")
    public void checkTopic(@RequestParam("topicName") String topicName) throws RestClientException, IOException {
        iMessageService.checkFormat(topicName);
    }

}
