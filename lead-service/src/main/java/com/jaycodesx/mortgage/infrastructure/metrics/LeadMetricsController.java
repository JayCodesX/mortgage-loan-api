package com.jaycodesx.mortgage.infrastructure.metrics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/metrics")
public class LeadMetricsController {

    private final LeadCatalogMetricsService leadCatalogMetricsService;

    public LeadMetricsController(LeadCatalogMetricsService leadCatalogMetricsService) {
        this.leadCatalogMetricsService = leadCatalogMetricsService;
    }

    @GetMapping
    public ResponseEntity<LeadMetricsResponseDto> getMetrics(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(leadCatalogMetricsService.getSnapshot());
    }
}
