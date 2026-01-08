package com.thanhpham.Kafka.dto.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpuUsageUI {
    private double systemCpuLoad;
    private double processCpuLoad;
}
