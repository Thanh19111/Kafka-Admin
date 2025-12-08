package com.thanhpham.Kafka.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicDetailResponseWithConfig {
    private TopicDetailResponse topic;
    private List<Pair> config;
}
