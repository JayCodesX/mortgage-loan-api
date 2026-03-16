package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceTokenValidatorTest {

    private final ServiceTokenProperties properties = new ServiceTokenProperties(
            "test-service-token-secret-12345678901234567890",
            "mortgage-loan-api",
            "pricing-service",
            "pricing:write"
    );

    private final ServiceTokenValidator validator = new ServiceTokenValidator(properties);

    @Test
    void acceptsValidServiceToken() {
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

        assertThatCode(() -> validator.validatePricingToken(token)).doesNotThrowAnyException();
    }

    @Test
    void rejectsTokenWithWrongScope() {
        String token = Jwts.builder()
                .issuer(properties.issuer())
                .subject(properties.issuer())
                .audience().add(properties.audience()).and()
                .claim("scope", "pricing:read")
                .claim("type", "service")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(300)))
                .signWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThatThrownBy(() -> validator.validatePricingToken(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid service token scope");
    }
}
