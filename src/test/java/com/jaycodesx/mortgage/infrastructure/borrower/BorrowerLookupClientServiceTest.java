package com.jaycodesx.mortgage.infrastructure.borrower;

import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BorrowerLookupClientServiceTest {

    @Test
    void borrowerExistsReturnsTrueWhenServiceReportsExistingBorrower() {
        AtomicReference<String> authHeader = new AtomicReference<>();
        ExchangeFunction exchangeFunction = request -> {
            authHeader.set(request.headers().getFirst(HttpHeaders.AUTHORIZATION));
            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"borrowerId\":42,\"exists\":true}")
                    .build());
        };
        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);
        ServiceTokenService serviceTokenService = mock(ServiceTokenService.class);
        when(serviceTokenService.generateToken(
                "borrower-service-test-secret-12345678901234567890",
                "borrower-service",
                "borrower:read"
        )).thenReturn("service-token");

        BorrowerLookupClientService service = new BorrowerLookupClientService(
                builder,
                new BorrowerServiceClientProperties(
                        "http://borrower-service:8087",
                        "borrower-service-test-secret-12345678901234567890",
                        "borrower-service",
                        "borrower:read"
                ),
                serviceTokenService
        );

        boolean exists = service.borrowerExists(42L);

        assertThat(exists).isTrue();
        assertThat(authHeader.get()).isEqualTo("Bearer service-token");
    }

    @Test
    void borrowerExistsReturnsFalseWhenServiceReportsMissingBorrower() {
        ExchangeFunction exchangeFunction = request -> Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND).build());
        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);
        ServiceTokenService serviceTokenService = mock(ServiceTokenService.class);
        when(serviceTokenService.generateToken(
                "borrower-service-test-secret-12345678901234567890",
                "borrower-service",
                "borrower:read"
        )).thenReturn("service-token");

        BorrowerLookupClientService service = new BorrowerLookupClientService(
                builder,
                new BorrowerServiceClientProperties(
                        "http://borrower-service:8087",
                        "borrower-service-test-secret-12345678901234567890",
                        "borrower-service",
                        "borrower:read"
                ),
                serviceTokenService
        );

        boolean exists = service.borrowerExists(999L);

        assertThat(exists).isFalse();
    }
}
