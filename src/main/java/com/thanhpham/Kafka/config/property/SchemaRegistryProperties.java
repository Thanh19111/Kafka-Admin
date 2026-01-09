package com.thanhpham.Kafka.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "schema-registry")
@Getter
@Setter
public class SchemaRegistryProperties {
    private String url = "";
}
