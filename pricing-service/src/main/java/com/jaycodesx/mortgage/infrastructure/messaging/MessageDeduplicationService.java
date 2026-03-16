package com.jaycodesx.mortgage.infrastructure.messaging;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MessageDeduplicationService {

    private final StringRedisTemplate redisTemplate;

    public MessageDeduplicationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean firstReceipt(String domain, String messageKey) {
        Boolean inserted = redisTemplate.opsForValue().setIfAbsent(key(domain, messageKey), "processed", Duration.ofHours(6));
        return Boolean.TRUE.equals(inserted);
    }

    private String key(String domain, String messageKey) {
        return "messaging:dedupe:" + domain + ":" + messageKey;
    }
}
