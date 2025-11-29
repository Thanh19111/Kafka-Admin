package com.thanhpham.Kafka.dto.response;

import lombok.Data;

@Data
public class NodeResponse {
    private int id;
    private String idString;
    private String host;
    private int port;
    private String rack;
    private boolean isFenced;
    private boolean isEmpty;
}
