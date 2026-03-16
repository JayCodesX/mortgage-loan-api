package com.jaycodesx.mortgage.infrastructure.metrics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/metrics/quotes")
public class QuoteMetricsController {

    private final QuoteMetricsService quoteMetricsService;

    public QuoteMetricsController(QuoteMetricsService quoteMetricsService) {
        this.quoteMetricsService = quoteMetricsService;
    }

    @GetMapping
    public Mono<ResponseEntity<QuoteMetricsResponseDto>> getQuoteMetrics() {
        return Mono.just(ResponseEntity.ok(quoteMetricsService.getSnapshot()));
    }

    @PostMapping("/sessions/authenticated")
    public Mono<ResponseEntity<Void>> recordAuthenticatedSession(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestParam(name = "authEventType", defaultValue = "LOGIN") String authEventType
    ) {
        quoteMetricsService.recordSessionAuthenticated(sessionId, authEventType);
        return Mono.just(ResponseEntity.noContent().build());
    }
}
