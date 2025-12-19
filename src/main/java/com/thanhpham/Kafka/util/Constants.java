package com.thanhpham.Kafka.util;

import org.springframework.beans.factory.annotation.Value;

// tất cả đều đon vị là miliseconds
public class Constants {
//    @Value("${kafka.bootstrap-server}")
//    public static String BOOTSTRAP_SERVERS;
    public static final int REQUEST_TIMEOUT_MS_CONFIG = 30000;
    public static final int DEFAULT_API_TIMEOUT_MS_CONFIG = 300000;
    public static final int ADJUST_TOPIC_MAX_TIMEOUT_CONFIG = 120000;
//    @Value("${schema-registry.url}")
//    public static String SCHEMA_REGISTRY_URL;
}
