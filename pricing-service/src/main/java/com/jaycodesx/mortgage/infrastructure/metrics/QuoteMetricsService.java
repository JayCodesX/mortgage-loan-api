package com.jaycodesx.mortgage.infrastructure.metrics;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuoteMetricsService {

    private final StringRedisTemplate redisTemplate;

    public QuoteMetricsService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void recordQuoteCompleted(Long quoteId) {
        increment("metrics:quotes:completed");
        recordDurationFromStart("metrics:quote:pricing:start:" + quoteId, "metrics:timing:pricing:sum-ms", "metrics:timing:pricing:count");
    }

    public void recordQuoteFailed(Long quoteId) {
        increment("metrics:quotes:failed");
        recordDurationFromStart("metrics:quote:pricing:start:" + quoteId, "metrics:timing:pricing:sum-ms", "metrics:timing:pricing:count");
    }

    public void markLeadQueued(Long quoteId) {
        redisTemplate.opsForValue().set("metrics:quote:lead:start:" + quoteId, String.valueOf(System.currentTimeMillis()));
    }

    private void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    private void recordDurationFromStart(String startKey, String sumKey, String countKey) {
        String startValue = redisTemplate.opsForValue().get(startKey);
        if (startValue == null) {
            return;
        }

        long duration = Math.max(0L, System.currentTimeMillis() - Long.parseLong(startValue));
        redisTemplate.opsForValue().increment(sumKey, duration);
        redisTemplate.opsForValue().increment(countKey);
        redisTemplate.delete(startKey);
    }
}
