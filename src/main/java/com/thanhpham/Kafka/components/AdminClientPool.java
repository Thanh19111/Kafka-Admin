package com.thanhpham.Kafka.components;

import com.thanhpham.Kafka.utils.Constants;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AdminClientPool {

    private final Map<String, Admin> pool = new ConcurrentHashMap<>();

    public Admin get(String bootstrap) {
        return pool.computeIfAbsent(bootstrap, this::create);
    }

    private Admin create(String bootstrap) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, Constants.REQUEST_TIMEOUT_MS_CONFIG);
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, Constants.DEFAULT_API_TIMEOUT_MS_CONFIG);
        return AdminClient.create(props);
    }

    @PreDestroy
    public void shutdown() {
        pool.values().forEach(Admin::close);
    }
}
