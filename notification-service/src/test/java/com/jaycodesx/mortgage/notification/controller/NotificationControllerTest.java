package com.jaycodesx.mortgage.notification.controller;

import com.jaycodesx.mortgage.notification.dto.LoanQuoteNotificationDto;
import com.jaycodesx.mortgage.notification.service.NotificationSnapshotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationSnapshotService notificationSnapshotService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void returnsSnapshotWhenPresent() {
        LoanQuoteNotificationDto snapshot = new LoanQuoteNotificationDto(
                3L, "session-3", "COMPLETED", false, "PUBLIC", "ESTIMATED", false, false,
                new BigDecimal("450000.00"), new BigDecimal("90000.00"), new BigDecimal("360000.00"),
                "60614", "CONVENTIONAL", "PRIMARY_RESIDENCE", 30, new BigDecimal("6.1000"),
                new BigDecimal("6.2800"), new BigDecimal("2200.00"), new BigDecimal("99500.00"),
                "Prime", "Next step", null
        );
        when(notificationSnapshotService.find(3L)).thenReturn(Optional.of(snapshot));

        ResponseEntity<LoanQuoteNotificationDto> response = notificationController.getQuoteNotification(3L).block();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(snapshot);
    }
}
