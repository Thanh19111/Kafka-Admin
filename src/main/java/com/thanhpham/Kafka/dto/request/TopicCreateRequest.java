package com.thanhpham.Kafka.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TopicCreateRequest {
    @NotBlank(message = "Topic name cannot be blank")
    private String topicName;

    @Min(value = 1, message = "Partition number must be greater than zero")
    @NotNull(message = "Partition number cannot be null")
    private int partitionNum;

    @Min(value = 1, message = "Replica number must be greater than zero")
    @NotNull(message = "Replica number cannot be null")
    private short replicaFNum;

    private List<ConfigItem> config = new ArrayList<>();
}
