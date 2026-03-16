package com.jaycodesx.mortgage.infrastructure.admin;

import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

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
        return get(properties.borrowerBaseUrl(), properties.borrowerSecret(), properties.borrowerAudience(), properties.borrowerScope(), BorrowerMetricsResponseDto.class);
    }

    public PricingMetricsResponseDto fetchPricingMetrics() {
        return get(properties.pricingBaseUrl(), properties.pricingSecret(), properties.pricingAudience(), properties.pricingScope(), PricingMetricsResponseDto.class);
    }

    public LeadMetricsResponseDto fetchLeadMetrics() {
        return get(properties.leadBaseUrl(), properties.leadSecret(), properties.leadAudience(), properties.leadScope(), LeadMetricsResponseDto.class);
    }

    public AuthMetricsResponseDto fetchAuthMetrics() {
        return get(properties.authBaseUrl(), properties.authSecret(), properties.authAudience(), properties.authScope(), AuthMetricsResponseDto.class);
    }

    public NotificationMetricsResponseDto fetchNotificationMetrics() {
        return get(properties.notificationBaseUrl(), properties.notificationSecret(), properties.notificationAudience(), properties.notificationScope(), NotificationMetricsResponseDto.class);
    }

    public List<BorrowerAdminResponseDto> fetchBorrowers() {
        return getList(properties.borrowerBaseUrl(), properties.borrowerSecret(), properties.borrowerAudience(), properties.borrowerScope(), "/internal/admin/borrowers", new ParameterizedTypeReference<>() {});
    }

    public List<AdminPricingProductResponseDto> fetchProducts() {
        return getList(properties.pricingBaseUrl(), properties.pricingSecret(), properties.pricingAudience(), properties.pricingScope(), "/internal/admin/products", new ParameterizedTypeReference<>() {});
    }

    public AdminPricingProductResponseDto createProduct(AdminPricingProductRequestDto request) {
        return exchangeWithBody(properties.pricingBaseUrl(), properties.pricingSecret(), properties.pricingAudience(), properties.pricingScope(), "/internal/admin/products", request, AdminPricingProductResponseDto.class, "POST");
    }

    public AdminPricingProductResponseDto updateProduct(Long id, AdminPricingProductRequestDto request) {
        return exchangeWithBody(properties.pricingBaseUrl(), properties.pricingSecret(), properties.pricingAudience(), properties.pricingScope(), "/internal/admin/products/" + id, request, AdminPricingProductResponseDto.class, "PUT");
    }

    public void deleteProduct(Long id) {
        webClientBuilder.baseUrl(properties.pricingBaseUrl()).build()
                .delete()
                .uri("/internal/admin/products/" + id)
                .header(HttpHeaders.AUTHORIZATION, bearer(properties.pricingSecret(), properties.pricingAudience(), properties.pricingScope()))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private <T> T get(String baseUrl, String secret, String audience, String scope, Class<T> responseType) {
        return webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri("/internal/admin/metrics")
                .header(HttpHeaders.AUTHORIZATION, bearer(secret, audience, scope))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    private <T> List<T> getList(String baseUrl, String secret, String audience, String scope, String uri, ParameterizedTypeReference<List<T>> responseType) {
        return webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, bearer(secret, audience, scope))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    private <T> T exchangeWithBody(String baseUrl, String secret, String audience, String scope, String uri, Object body, Class<T> responseType, String method) {
        WebClient.RequestBodySpec request = switch (method) {
            case "POST" -> webClientBuilder.baseUrl(baseUrl).build().post().uri(uri);
            case "PUT" -> webClientBuilder.baseUrl(baseUrl).build().put().uri(uri);
            default -> throw new IllegalArgumentException("Unsupported method");
        };

        return request.header(HttpHeaders.AUTHORIZATION, bearer(secret, audience, scope))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    private String bearer(String secret, String audience, String scope) {
        return "Bearer " + serviceTokenService.generateToken(secret, audience, scope);
    }
}
