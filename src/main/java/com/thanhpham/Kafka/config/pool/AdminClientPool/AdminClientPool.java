package com.thanhpham.Kafka.config.pool.AdminClientPool;

import com.thanhpham.Kafka.config.factory.AdminClientFactory;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AdminClientPool implements IAdminClientPool {
    private final AdminClientFactory adminClientFactory;
    private final Map<String, Admin> pool = new ConcurrentHashMap<>();

    public Admin get(String bootstrap) {
        return pool.computeIfAbsent(bootstrap, this::create);
    }

    private Admin create(String bootstrap) {
        return adminClientFactory.createAdmin(bootstrap);
    }

    @PreDestroy
    public void shutdown() {
        pool.values().forEach(Admin::close);
    }
}
