package com.jaycodesx.mortgage.infrastructure.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteMetricsServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private SetOperations<String, String> setOperations;

    private QuoteMetricsService quoteMetricsService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        quoteMetricsService = new QuoteMetricsService(redisTemplate);
    }

    @Test
    void recordsQuoteStartAndPricingStartTime() {
        quoteMetricsService.recordQuoteStarted(22L, "session-22");

        verify(valueOperations).increment("metrics:quotes:started");
        verify(valueOperations).set(eq("metrics:quote:pricing:start:22"), anyString());
        verify(setOperations).add("metrics:sessions:quotes", "session-22");
    }

    @Test
    void buildsSnapshotFromRedisCountersAndSessionSets() {
        when(valueOperations.get("metrics:quotes:started")).thenReturn("10");
        when(valueOperations.get("metrics:quotes:refinements-requested")).thenReturn("4");
        when(valueOperations.get("metrics:quotes:deduped")).thenReturn("2");
        when(valueOperations.get("metrics:quotes:completed")).thenReturn("8");
        when(valueOperations.get("metrics:quotes:failed")).thenReturn("1");
        when(valueOperations.get("metrics:leads:created")).thenReturn("3");
        when(valueOperations.get("metrics:timing:pricing:sum-ms")).thenReturn("4000");
        when(valueOperations.get("metrics:timing:pricing:count")).thenReturn("8");
        when(valueOperations.get("metrics:timing:lead:sum-ms")).thenReturn("900");
        when(valueOperations.get("metrics:timing:lead:count")).thenReturn("3");
        when(valueOperations.get("metrics:auth:registrations")).thenReturn("2");
        when(valueOperations.get("metrics:auth:logins")).thenReturn("5");
        when(valueOperations.get("metrics:messaging:pricing-result:deduped")).thenReturn("6");
        when(valueOperations.get("metrics:messaging:lead-result:deduped")).thenReturn("4");
        when(valueOperations.get("metrics:messaging:pricing-result:dlq-publishes")).thenReturn("1");
        when(valueOperations.get("metrics:messaging:lead-result:dlq-publishes")).thenReturn("2");
        when(setOperations.size("metrics:sessions:quotes")).thenReturn(10L);
        when(setOperations.size("metrics:sessions:authenticated")).thenReturn(7L);
        when(setOperations.size("metrics:sessions:refined")).thenReturn(4L);
        when(setOperations.size("metrics:sessions:lead-converted")).thenReturn(3L);

        QuoteMetricsResponseDto snapshot = quoteMetricsService.getSnapshot();

        assertThat(snapshot.quotesStarted()).isEqualTo(10);
        assertThat(snapshot.quoteRefinementsRequested()).isEqualTo(4);
        assertThat(snapshot.quotesDeduped()).isEqualTo(2);
        assertThat(snapshot.quotesCompleted()).isEqualTo(8);
        assertThat(snapshot.quotesFailed()).isEqualTo(1);
        assertThat(snapshot.leadsCreated()).isEqualTo(3);
        assertThat(snapshot.averagePricingDurationMs()).isEqualTo(500);
        assertThat(snapshot.averageLeadDurationMs()).isEqualTo(300);
        assertThat(snapshot.sessionsWithQuotes()).isEqualTo(10);
        assertThat(snapshot.authenticatedSessions()).isEqualTo(7);
        assertThat(snapshot.sessionsWithRefinements()).isEqualTo(4);
        assertThat(snapshot.leadConvertedSessions()).isEqualTo(3);
        assertThat(snapshot.authRegistrations()).isEqualTo(2);
        assertThat(snapshot.authLogins()).isEqualTo(5);
        assertThat(snapshot.quoteToAuthConversionRate()).isEqualTo(70.0);
        assertThat(snapshot.authToRefinementConversionRate()).isEqualTo(57.1);
        assertThat(snapshot.quoteToLeadConversionRate()).isEqualTo(30.0);
        assertThat(snapshot.pricingResultMessagesDeduped()).isEqualTo(6);
        assertThat(snapshot.leadResultMessagesDeduped()).isEqualTo(4);
        assertThat(snapshot.pricingResultDlqPublishes()).isEqualTo(1);
        assertThat(snapshot.leadResultDlqPublishes()).isEqualTo(2);
    }
}
