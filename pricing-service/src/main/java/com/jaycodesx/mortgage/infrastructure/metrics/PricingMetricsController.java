package com.jaycodesx.mortgage.infrastructure.metrics;

import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
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
    private final ServiceTokenValidator serviceTokenValidator;

    public PricingMetricsController(
            PricingCatalogMetricsService pricingCatalogMetricsService,
            ServiceTokenValidator serviceTokenValidator
    ) {
        this.pricingCatalogMetricsService = pricingCatalogMetricsService;
        this.serviceTokenValidator = serviceTokenValidator;
    }

    @GetMapping
    public ResponseEntity<PricingMetricsResponseDto> getMetrics(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        try {
            serviceTokenValidator.validatePricingTokenHeader(authorizationHeader);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(pricingCatalogMetricsService.getSnapshot());
    }
}
