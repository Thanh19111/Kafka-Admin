package com.thanhpham.Kafka.config;

import com.thanhpham.Kafka.config.property.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.jolokia.client.JolokiaClient;
import org.jolokia.client.JolokiaClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JolokiaMetricConfig {
    private final KafkaProperties properties;

    @Bean(destroyMethod = "close")
    JolokiaClient initJolokiaClient(){
        return new JolokiaClientBuilder()
                .url(properties.getJolokiaServer())
                .build();
    }
}
