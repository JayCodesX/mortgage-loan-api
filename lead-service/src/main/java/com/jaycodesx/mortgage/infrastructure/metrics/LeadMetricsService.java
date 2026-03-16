package com.jaycodesx.mortgage.infrastructure.metrics;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LeadMetricsService {

    private final StringRedisTemplate redisTemplate;

    public LeadMetricsService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void recordLeadCreated(Long quoteId) {
        redisTemplate.opsForValue().increment("metrics:leads:created");
        String startValue = redisTemplate.opsForValue().get("metrics:quote:lead:start:" + quoteId);
        if (startValue == null) {
            return;
        }

        long duration = Math.max(0L, System.currentTimeMillis() - Long.parseLong(startValue));
        redisTemplate.opsForValue().increment("metrics:timing:lead:sum-ms", duration);
        redisTemplate.opsForValue().increment("metrics:timing:lead:count");
        redisTemplate.delete("metrics:quote:lead:start:" + quoteId);
    }
}
