package com.thanhpham.Kafka.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageSlice {
    private long offset;
    private String message;
}
