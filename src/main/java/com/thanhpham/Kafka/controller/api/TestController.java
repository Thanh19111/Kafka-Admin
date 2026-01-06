package com.thanhpham.Kafka.controller.api;

import com.thanhpham.Kafka.dto.request.SchemaCreateRequest;
import com.thanhpham.Kafka.service.message.IMessageService;
import com.thanhpham.Kafka.service.metric.MetricService;
import com.thanhpham.Kafka.service.schemaregistry.SchemaRegistry;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.RequiredArgsConstructor;
import org.jolokia.client.exception.JolokiaException;
import org.springframework.web.bind.annotation.*;

import javax.management.MalformedObjectNameException;
import java.io.IOException;

@RestController
@RequestMapping("api/test")
@RequiredArgsConstructor
public class TestController {
    private final SchemaRegistry schemaRegistryUtil;
    private final IMessageService iMessageService;
    private final MetricService metricService;

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

    @PostMapping("/message")
    public void t() {
        System.out.println("OK");
        iMessageService.pushJsonMessage();
    }

    @GetMapping("/metric")
    public void g() throws MalformedObjectNameException, JolokiaException {
        metricService.t();
    }
}
