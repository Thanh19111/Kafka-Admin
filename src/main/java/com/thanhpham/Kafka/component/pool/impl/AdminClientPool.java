package com.thanhpham.Kafka.component.pool.impl;

import com.thanhpham.Kafka.component.factory.IAdminClientFactory;
import com.thanhpham.Kafka.component.pool.IAdminClientPool;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AdminClientPool implements IAdminClientPool {
    private final IAdminClientFactory adminClientFactory;
    private final Map<String, Admin> pool = new ConcurrentHashMap<>();

    @Override
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
