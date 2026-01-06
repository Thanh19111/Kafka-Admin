package com.thanhpham.Kafka.config;

import jakarta.annotation.PreDestroy;
import org.jolokia.client.JolokiaClient;
import org.jolokia.client.JolokiaClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JolokiaMetricConfig {
    @Bean(destroyMethod = "close")
    JolokiaClient initJolokiaClient(){
        return new JolokiaClientBuilder()
                .url("http://localhost:8778/jolokia")
                .build();
    }
}
