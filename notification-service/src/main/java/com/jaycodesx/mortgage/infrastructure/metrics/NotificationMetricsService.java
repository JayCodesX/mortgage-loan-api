package com.jaycodesx.mortgage.infrastructure.metrics;

import com.jaycodesx.mortgage.notification.service.NotificationSnapshotService;
import org.springframework.stereotype.Service;

@Service
public class NotificationMetricsService {

    private final NotificationSnapshotService notificationSnapshotService;

    public NotificationMetricsService(NotificationSnapshotService notificationSnapshotService) {
        this.notificationSnapshotService = notificationSnapshotService;
    }

    public NotificationMetricsResponseDto getSnapshot() {
        return new NotificationMetricsResponseDto(notificationSnapshotService.countSnapshots());
    }
}
