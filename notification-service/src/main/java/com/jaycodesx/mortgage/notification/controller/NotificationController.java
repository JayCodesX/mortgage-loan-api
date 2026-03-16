package com.jaycodesx.mortgage.notification.controller;

import com.jaycodesx.mortgage.notification.dto.LoanQuoteNotificationDto;
import com.jaycodesx.mortgage.notification.service.NotificationSnapshotService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@RestController
@RequestMapping("/notifications/quotes")
public class NotificationController {

    private final NotificationSnapshotService notificationSnapshotService;

    public NotificationController(NotificationSnapshotService notificationSnapshotService) {
        this.notificationSnapshotService = notificationSnapshotService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<LoanQuoteNotificationDto>> getQuoteNotification(@PathVariable Long id) {
        return Mono.just(notificationSnapshotService.find(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @GetMapping(path = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<LoanQuoteNotificationDto>> streamQuote(@PathVariable Long id) {
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(2))
                .map(tick -> notificationSnapshotService.find(id))
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .distinctUntilChanged()
                .takeUntil(snapshot -> !isInFlight(snapshot.processingStatus()))
                .map(snapshot -> ServerSentEvent.<LoanQuoteNotificationDto>builder()
                        .event("quote-notification")
                        .id(String.valueOf(snapshot.id()))
                        .data(snapshot)
                        .build());
    }

    private boolean isInFlight(String processingStatus) {
        return "QUEUED".equalsIgnoreCase(processingStatus)
                || "PROCESSING".equalsIgnoreCase(processingStatus)
                || "PENDING".equalsIgnoreCase(processingStatus);
    }
}
