package com.jaycodesx.mortgage.infrastructure.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadMetricsServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private LeadMetricsService leadMetricsService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        leadMetricsService = new LeadMetricsService(redisTemplate);
    }

    @Test
    void recordsLeadCreationMetrics() {
        when(valueOperations.get("metrics:quote:lead:start:31")).thenReturn(String.valueOf(System.currentTimeMillis() - 50));

        leadMetricsService.recordLeadCreated(31L);

        verify(valueOperations).increment("metrics:leads:created");
        verify(valueOperations).increment(org.mockito.ArgumentMatchers.eq("metrics:timing:lead:sum-ms"), org.mockito.ArgumentMatchers.anyLong());
        verify(valueOperations).increment("metrics:timing:lead:count");
        verify(redisTemplate).delete("metrics:quote:lead:start:31");
    }
}
