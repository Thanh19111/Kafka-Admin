package com.thanhpham.Kafka.dto.request;

import lombok.Data;

@Data
public class SchemaCreateRequest {
    private String subject;
    private String schema;
}
