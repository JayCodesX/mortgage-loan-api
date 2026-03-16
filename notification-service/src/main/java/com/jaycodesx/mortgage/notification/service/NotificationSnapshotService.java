package com.jaycodesx.mortgage.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaycodesx.mortgage.notification.dto.LoanQuoteNotificationDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class NotificationSnapshotService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public NotificationSnapshotService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void store(QuoteNotificationMessage message) {
        try {
            redisTemplate.opsForValue().set(snapshotKey(message.id()), objectMapper.writeValueAsString(message.toDto()));
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to serialize notification snapshot", ex);
        }
    }

    public Optional<LoanQuoteNotificationDto> find(Long quoteId) {
        try {
            String payload = redisTemplate.opsForValue().get(snapshotKey(quoteId));
            if (payload == null || payload.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(payload, LoanQuoteNotificationDto.class));
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to deserialize notification snapshot", ex);
        }
    }

    public long countSnapshots() {
        Set<String> keys = redisTemplate.keys("notifications:quotes:*");
        return keys == null ? 0 : keys.size();
    }

    private String snapshotKey(Long quoteId) {
        return "notifications:quotes:" + quoteId;
    }
}
