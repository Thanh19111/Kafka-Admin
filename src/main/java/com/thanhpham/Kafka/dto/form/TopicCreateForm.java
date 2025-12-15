package com.thanhpham.Kafka.dto.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TopicCreateForm {
    @NotBlank(message = "Topic name cannot be blank")
    private String topicName;

    @NotNull(message = "Partition number cannot be null")
    @Min(value = 1, message = "Partition number must be greater than zero")
    private Integer partitionNum;

    @NotNull(message = "Replica number cannot be null")
    @Min(value = 1, message = "Replica number must be greater than zero")
    private Short replicaFNum;

    private String config;
}
