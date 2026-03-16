package com.jaycodesx.mortgage.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaycodesx.mortgage.lead.dto.MortgageLeadResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSnapshotServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private NotificationSnapshotService notificationSnapshotService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        notificationSnapshotService = new NotificationSnapshotService(redisTemplate, new ObjectMapper());
    }

    @Test
    void storesAndLoadsQuoteSnapshot() throws Exception {
        QuoteNotificationMessage message = new QuoteNotificationMessage(
                QuoteNotificationMessage.SCHEMA_VERSION,
                "notification-msg-1",
                7L,
                "session-7",
                "COMPLETED",
                false,
                "PUBLIC",
                "ESTIMATED",
                false,
                false,
                new BigDecimal("525000.00"),
                new BigDecimal("105000.00"),
                new BigDecimal("420000.00"),
                "98101",
                "CONVENTIONAL",
                "PRIMARY_RESIDENCE",
                30,
                new BigDecimal("6.0500"),
                new BigDecimal("6.2300"),
                new BigDecimal("2531.63"),
                new BigDecimal("116025.00"),
                "Market Estimate",
                "Create an account or continue to provide borrower details for a refined quote.",
                new MortgageLeadResponseDto(4L, 7L, "NEW", "PUBLIC_QUOTE_FUNNEL"),
                "token"
        );
        notificationSnapshotService.store(message);
        verify(valueOperations).set(eq("notifications:quotes:7"), org.mockito.ArgumentMatchers.anyString());

        String stored = new ObjectMapper().writeValueAsString(message.toDto());
        when(valueOperations.get("notifications:quotes:7")).thenReturn(stored);

        Optional<com.jaycodesx.mortgage.notification.dto.LoanQuoteNotificationDto> loaded = notificationSnapshotService.find(7L);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().estimatedRate()).isEqualByComparingTo("6.0500");
        assertThat(loaded.get().lead()).isNotNull();
    }
}
