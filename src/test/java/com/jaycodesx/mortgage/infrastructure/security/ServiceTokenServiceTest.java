package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceTokenServiceTest {

    @Test
    void generatesPricingTokenWithExpectedClaims() {
        ServiceTokenProperties properties = new ServiceTokenProperties(
                "test-service-token-secret-12345678901234567890",
                "mortgage-loan-api",
                "pricing-service",
                "pricing:write",
                300
        );
        ServiceTokenService service = new ServiceTokenService(properties);

        String token = service.generatePricingToken();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getIssuer()).isEqualTo("mortgage-loan-api");
        assertThat(claims.getSubject()).isEqualTo("mortgage-loan-api");
        assertThat(claims.getAudience()).contains("pricing-service");
        assertThat(claims.get("scope", String.class)).isEqualTo("pricing:write");
        assertThat(claims.get("type", String.class)).isEqualTo("service");
    }
}
