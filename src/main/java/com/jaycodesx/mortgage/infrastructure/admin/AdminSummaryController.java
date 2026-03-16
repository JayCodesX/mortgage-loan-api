package com.jaycodesx.mortgage.infrastructure.admin;

import com.jaycodesx.mortgage.infrastructure.metrics.QuoteMetricsService;
import com.jaycodesx.mortgage.infrastructure.security.UserTokenAuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/metrics/admin")
public class AdminSummaryController {

    private final QuoteMetricsService quoteMetricsService;
    private final AdminMetricsClientService adminMetricsClientService;
    private final UserTokenAuthorizationService userTokenAuthorizationService;

    public AdminSummaryController(
            QuoteMetricsService quoteMetricsService,
            AdminMetricsClientService adminMetricsClientService,
            UserTokenAuthorizationService userTokenAuthorizationService
    ) {
        this.quoteMetricsService = quoteMetricsService;
        this.adminMetricsClientService = adminMetricsClientService;
        this.userTokenAuthorizationService = userTokenAuthorizationService;
    }

    @GetMapping("/summary")
    public Mono<ResponseEntity<AdminSummaryResponseDto>> getAdminSummary(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        try {
            userTokenAuthorizationService.requireAdminUser(authorizationHeader);
            return Mono.fromCallable(() -> ResponseEntity.ok(new AdminSummaryResponseDto(
                            quoteMetricsService.getSnapshot(),
                            adminMetricsClientService.fetchAuthMetrics(),
                            adminMetricsClientService.fetchBorrowerMetrics(),
                            adminMetricsClientService.fetchPricingMetrics(),
                            adminMetricsClientService.fetchLeadMetrics(),
                            adminMetricsClientService.fetchNotificationMetrics()
                    )))
                    .subscribeOn(Schedulers.boundedElastic());
        } catch (IllegalArgumentException ex) {
            HttpStatus status = "Admin role required".equals(ex.getMessage()) ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;
            return Mono.error(new ResponseStatusException(status, ex.getMessage(), ex));
        }
    }

}
