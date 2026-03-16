package com.jaycodesx.mortgage.infrastructure.metrics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteMetricsControllerTest {

    @Mock
    private QuoteMetricsService quoteMetricsService;

    @InjectMocks
    private QuoteMetricsController quoteMetricsController;

    @Test
    void returnsMetricsSnapshot() {
        QuoteMetricsResponseDto snapshot = new QuoteMetricsResponseDto(10, 4, 2, 8, 1, 3, 500, 300, 10, 7, 4, 3, 2, 5, 70.0, 57.1, 30.0, 6, 4, 1, 2);
        when(quoteMetricsService.getSnapshot()).thenReturn(snapshot);

        ResponseEntity<QuoteMetricsResponseDto> response = quoteMetricsController.getQuoteMetrics().block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(snapshot);
    }

    @Test
    void recordsAuthenticatedSession() {
        ResponseEntity<Void> response = quoteMetricsController.recordAuthenticatedSession("session-7", "REGISTER").block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(quoteMetricsService).recordSessionAuthenticated("session-7", "REGISTER");
    }
}
