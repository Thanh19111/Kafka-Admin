package com.thanhpham.Kafka.controller.api;

import com.thanhpham.Kafka.dto.request.SchemaCreateRequest;
import com.thanhpham.Kafka.service.message.IMessageService;
import com.thanhpham.Kafka.service.schemaregistry.SchemaRegistry;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
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
}
