package com.thanhpham.Kafka.utils.decoder.impl;

import com.thanhpham.Kafka.utils.decoder.MessageDecoder;
import org.springframework.stereotype.Component;

@Component
public class JsonDecoder implements MessageDecoder {
    @Override
    public String decode() {
        return "";
    }
}
