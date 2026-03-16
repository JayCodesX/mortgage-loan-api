package com.jaycodesx.mortgage.quote.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class QuoteSessionService {

    private static final Duration STATUS_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;

    public QuoteSessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheQuoteStatus(Long quoteId, String processingStatus) {
        redisTemplate.opsForValue().set("quote:status:" + quoteId, processingStatus, STATUS_TTL);
    }
}
