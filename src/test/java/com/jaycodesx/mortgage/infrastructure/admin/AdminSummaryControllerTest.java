package com.jaycodesx.mortgage.infrastructure.admin;

import com.jaycodesx.mortgage.infrastructure.metrics.QuoteMetricsResponseDto;
import com.jaycodesx.mortgage.infrastructure.metrics.QuoteMetricsService;
import com.jaycodesx.mortgage.infrastructure.security.UserTokenAuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminSummaryControllerTest {

    @Test
    void returnsAggregatedSummaryForAdminUser() {
        QuoteMetricsService quoteMetricsService = mock(QuoteMetricsService.class);
        AdminMetricsClientService adminMetricsClientService = mock(AdminMetricsClientService.class);
        UserTokenAuthorizationService authService = mock(UserTokenAuthorizationService.class);
        when(quoteMetricsService.getSnapshot()).thenReturn(new QuoteMetricsResponseDto(1, 1, 0, 1, 0, 1, 10, 20, 1, 1, 1, 1, 1, 1, 100.0, 100.0, 100.0, 0, 0, 0, 0));
        when(adminMetricsClientService.fetchAuthMetrics()).thenReturn(new AuthMetricsResponseDto(2, 1, 1));
        when(adminMetricsClientService.fetchBorrowerMetrics()).thenReturn(new BorrowerMetricsResponseDto(4, 720.0, List.of()));
        when(adminMetricsClientService.fetchPricingMetrics()).thenReturn(new PricingMetricsResponseDto(4, 4, 5, 16, List.of()));
        when(adminMetricsClientService.fetchLeadMetrics()).thenReturn(new LeadMetricsResponseDto(2, List.of(), List.of()));
        when(adminMetricsClientService.fetchNotificationMetrics()).thenReturn(new NotificationMetricsResponseDto(3));
        doNothing().when(authService).requireAdminUser("Bearer token");

        ResponseEntity<?> response = new AdminSummaryController(quoteMetricsService, adminMetricsClientService, authService)
                .getAdminSummary("Bearer token")
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(AdminSummaryResponseDto.class);
    }
}
