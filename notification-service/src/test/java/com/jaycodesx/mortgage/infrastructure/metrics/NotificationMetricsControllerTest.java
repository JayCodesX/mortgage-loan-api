package com.jaycodesx.mortgage.infrastructure.metrics;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationMetricsControllerTest {

    @Test
    void returnsUnauthorizedWithoutHeader() {
        NotificationMetricsController controller = new NotificationMetricsController(mock(NotificationMetricsService.class));

        ResponseEntity<NotificationMetricsResponseDto> response = controller.getMetrics(null);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void returnsSnapshotWithAuthorizationHeader() {
        NotificationMetricsService service = mock(NotificationMetricsService.class);
        when(service.getSnapshot()).thenReturn(new NotificationMetricsResponseDto(3));

        ResponseEntity<NotificationMetricsResponseDto> response = new NotificationMetricsController(service).getMetrics("Bearer token");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(new NotificationMetricsResponseDto(3));
    }
}
