package com.jaycodesx.mortgage.infrastructure.admin;

import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@EnableConfigurationProperties(InternalServiceClientProperties.class)
public class AdminMetricsClientService {

    private final WebClient.Builder webClientBuilder;
    private final InternalServiceClientProperties properties;
    private final ServiceTokenService serviceTokenService;

    public AdminMetricsClientService(
            WebClient.Builder webClientBuilder,
            InternalServiceClientProperties properties,
            ServiceTokenService serviceTokenService
    ) {
        this.webClientBuilder = webClientBuilder;
        this.properties = properties;
        this.serviceTokenService = serviceTokenService;
    }

    public BorrowerMetricsResponseDto fetchBorrowerMetrics() {
        return get(
                properties.borrowerBaseUrl(),
                properties.borrowerSecret(),
                properties.borrowerAudience(),
                properties.borrowerScope(),
                BorrowerMetricsResponseDto.class
        );
    }

    public PricingMetricsResponseDto fetchPricingMetrics() {
        return get(
                properties.pricingBaseUrl(),
                properties.pricingSecret(),
                properties.pricingAudience(),
                properties.pricingScope(),
                PricingMetricsResponseDto.class
        );
    }

    public LeadMetricsResponseDto fetchLeadMetrics() {
        return get(
                properties.leadBaseUrl(),
                properties.leadSecret(),
                properties.leadAudience(),
                properties.leadScope(),
                LeadMetricsResponseDto.class
        );
    }

    public AuthMetricsResponseDto fetchAuthMetrics() {
        return get(
                properties.authBaseUrl(),
                properties.authSecret(),
                properties.authAudience(),
                properties.authScope(),
                AuthMetricsResponseDto.class
        );
    }

    public NotificationMetricsResponseDto fetchNotificationMetrics() {
        return get(
                properties.notificationBaseUrl(),
                properties.notificationSecret(),
                properties.notificationAudience(),
                properties.notificationScope(),
                NotificationMetricsResponseDto.class
        );
    }

    private <T> T get(String baseUrl, String secret, String audience, String scope, Class<T> responseType) {
        return webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri("/internal/admin/metrics")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceTokenService.generateToken(secret, audience, scope))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
