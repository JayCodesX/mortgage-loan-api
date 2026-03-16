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
public class BorrowerMetricsController {

    private final BorrowerMetricsService borrowerMetricsService;
    private final ServiceTokenValidator serviceTokenValidator;

    public BorrowerMetricsController(
            BorrowerMetricsService borrowerMetricsService,
            ServiceTokenValidator serviceTokenValidator
    ) {
        this.borrowerMetricsService = borrowerMetricsService;
        this.serviceTokenValidator = serviceTokenValidator;
    }

    @GetMapping
    public ResponseEntity<BorrowerMetricsResponseDto> getMetrics(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        try {
            serviceTokenValidator.validateBorrowerReadToken(authorizationHeader);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(borrowerMetricsService.getSnapshot());
    }
}
