package com.thanhpham.Kafka.service.message;

import com.thanhpham.Kafka.dto.response.AvroMessage;
import com.thanhpham.Kafka.dto.response.JsonMessage;

import java.util.List;

public interface IMessageService {
    List<JsonMessage> decodeJson(String topic);
    List<AvroMessage> decodeAvro(String topic);
}
