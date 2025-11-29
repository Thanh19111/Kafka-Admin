package com.thanhpham.Kafka.dto.response;

import lombok.Data;

import java.util.List;

/*
  * class nay chua thong tin khi lay thong tin cua 1 topic
*/

@Data
public class PartitionSpecsResponse {
    private int partition;
    private int leader;
    private List<Integer> replicas;
    private List<Integer> isr;
}


