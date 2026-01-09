package com.thanhpham.Kafka.dto.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BufferUI {
    private Long totalCapacity;
    private Long memoryUsed;
}
