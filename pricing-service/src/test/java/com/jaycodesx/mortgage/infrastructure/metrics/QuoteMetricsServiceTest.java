package com.jaycodesx.mortgage.infrastructure.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteMetricsServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private QuoteMetricsService quoteMetricsService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        quoteMetricsService = new QuoteMetricsService(redisTemplate);
    }

    @Test
    void recordsCompletedPricingMetrics() {
        when(valueOperations.get("metrics:quote:pricing:start:12")).thenReturn(String.valueOf(System.currentTimeMillis() - 100));

        quoteMetricsService.recordQuoteCompleted(12L);

        verify(valueOperations).increment("metrics:quotes:completed");
        verify(valueOperations).increment(org.mockito.ArgumentMatchers.eq("metrics:timing:pricing:sum-ms"), org.mockito.ArgumentMatchers.anyLong());
        verify(valueOperations).increment("metrics:timing:pricing:count");
        verify(redisTemplate).delete("metrics:quote:pricing:start:12");
    }

    @Test
    void marksLeadQueueStart() {
        quoteMetricsService.markLeadQueued(12L);

        verify(valueOperations).set(org.mockito.ArgumentMatchers.eq("metrics:quote:lead:start:12"), anyString());
    }
}
