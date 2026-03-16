package com.jaycodesx.mortgage.infrastructure.borrower;

import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@EnableConfigurationProperties(BorrowerServiceClientProperties.class)
public class BorrowerLookupClientService {

    private final WebClient webClient;
    private final BorrowerServiceClientProperties properties;
    private final ServiceTokenService serviceTokenService;

    public BorrowerLookupClientService(
            WebClient.Builder webClientBuilder,
            BorrowerServiceClientProperties properties,
            ServiceTokenService serviceTokenService
    ) {
        this.webClient = webClientBuilder.baseUrl(properties.baseUrl()).build();
        this.properties = properties;
        this.serviceTokenService = serviceTokenService;
    }

    public boolean borrowerExists(Long borrowerId) {
        try {
            BorrowerExistsResponseDto response = webClient.get()
                    .uri("/borrowers/internal/{id}/exists", borrowerId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceTokenService.generateToken(
                            properties.secret(),
                            properties.audience(),
                            properties.scope()
                    ))
                    .retrieve()
                    .bodyToMono(BorrowerExistsResponseDto.class)
                    .block();
            return response != null && response.exists();
        } catch (WebClientResponseException.NotFound ex) {
            return false;
        }
    }
}
