package com.thanhpham.Kafka.dto.ui;

import lombok.Data;

@Data
public class UnderReplicaUI {
    private int underReplicaCount;
    private int offlineReplicaCount;
}
