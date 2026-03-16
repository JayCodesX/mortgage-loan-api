package com.jaycodesx.mortgage.infrastructure.metrics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/metrics")
public class PricingMetricsController {

    private final PricingCatalogMetricsService pricingCatalogMetricsService;

    public PricingMetricsController(PricingCatalogMetricsService pricingCatalogMetricsService) {
        this.pricingCatalogMetricsService = pricingCatalogMetricsService;
    }

    @GetMapping
    public ResponseEntity<PricingMetricsResponseDto> getMetrics(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(pricingCatalogMetricsService.getSnapshot());
    }
}
