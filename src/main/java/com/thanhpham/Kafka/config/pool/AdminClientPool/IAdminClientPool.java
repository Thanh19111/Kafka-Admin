package com.thanhpham.Kafka.config.pool.AdminClientPool;

import org.apache.kafka.clients.admin.Admin;

public interface IAdminClientPool {
    Admin get(String bootstrap);
}
