package com.thanhpham.Kafka.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    @Bean
    public CacheManager caffeineCacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("avros");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterAccess(10, TimeUnit.SECONDS)
                        .removalListener((key, value, cause) -> {
                            if (value instanceof Consumer ac) {
                                try {
                                    System.out.println(">>> " + cause);
                                    ac.wakeup();
                                    ac.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .recordStats()
        );
        return cacheManager;
    }
}
