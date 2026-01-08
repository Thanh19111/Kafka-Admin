package com.thanhpham.Kafka.dto.ui;

import lombok.Data;

@Data
public class MessageCountUI {
    private long messageInCount;
    private long byteOutCount;
}
