package com.jaycodesx.mortgage.infrastructure.metrics;

import com.jaycodesx.mortgage.notification.service.NotificationSnapshotService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationMetricsServiceTest {

    @Test
    void returnsCachedSnapshotCount() {
        NotificationSnapshotService snapshotService = mock(NotificationSnapshotService.class);
        when(snapshotService.countSnapshots()).thenReturn(4L);

        NotificationMetricsResponseDto response = new NotificationMetricsService(snapshotService).getSnapshot();

        assertThat(response.cachedQuoteSnapshots()).isEqualTo(4);
    }
}
