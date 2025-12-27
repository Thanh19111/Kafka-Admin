package com.thanhpham.Kafka.service.message;

import com.thanhpham.Kafka.dto.response.MessageSlice;

import java.util.List;

public interface IMessageService {
    List<MessageSlice> decodeJson(String topic);
    List<MessageSlice> decodeAvro(String topic);
    List<MessageSlice> readAvroMessageByOffset(String topic, int partition, long startOffset, long endOffset);
    List<MessageSlice> readJsonMessageByOffset(String topic, int partition, long startOffset, long endOffset);
    void pushJsonMessage();
}
