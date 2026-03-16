package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatCode;

class ServiceTokenValidatorTest {

    @Test
    void acceptsValidLeadServiceToken() {
        ServiceTokenProperties properties = new ServiceTokenProperties(
                "test-lead-service-token-secret-12345678901234567890",
                "pricing-service",
                "lead-service",
                "lead:write"
        );
        ServiceTokenValidator validator = new ServiceTokenValidator(properties);

        String token = Jwts.builder()
                .issuer(properties.issuer())
                .subject(properties.issuer())
                .audience().add(properties.audience()).and()
                .claim("scope", properties.scope())
                .claim("type", "service")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(300)))
                .signWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThatCode(() -> validator.validateLeadToken(token)).doesNotThrowAnyException();
    }
}
