package com.thanhpham.Kafka.util;

// tất cả đều đon vị là miliseconds
public class Constants {
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    public static final int REQUEST_TIMEOUT_MS_CONFIG = 30000;
    public static final int DEFAULT_API_TIMEOUT_MS_CONFIG = 300000;
    public static final int ADJUST_TOPIC_MAX_TIMEOUT_CONFIG = 120000;
    public static final int LAST_N = 100;
    public static String SCHEMA_REGISTRY_URL = "http://localhost:8081";
}
