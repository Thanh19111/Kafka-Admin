package com.thanhpham.Kafka.dto.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCountUI {
    private long messageInCount;
    private long byteOutCount;
}
