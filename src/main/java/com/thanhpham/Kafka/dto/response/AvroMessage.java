package com.thanhpham.Kafka.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvroMessage {
    private long offset;
    private String message;
}
