package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceTokenValidatorTest {

    private static final String SECRET = "local-dev-notification-service-secret-1234567890";

    @Test
    void validatesExpectedNotificationToken() {
        ServiceTokenValidator validator = new ServiceTokenValidator(new ServiceTokenProperties(
                SECRET,
                "mortgage-loan-api",
                "notification-service",
                "notification:write"
        ));

        String token = Jwts.builder()
                .issuer("mortgage-loan-api")
                .subject("mortgage-loan-api")
                .audience().add("notification-service").and()
                .claim("scope", "notification:write")
                .claim("type", "service")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(300)))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThatNoException().isThrownBy(() -> validator.validateNotificationToken(token));
    }

    @Test
    void rejectsWrongAudience() {
        ServiceTokenValidator validator = new ServiceTokenValidator(new ServiceTokenProperties(
                SECRET,
                "mortgage-loan-api",
                "notification-service",
                "notification:write"
        ));

        String token = Jwts.builder()
                .issuer("mortgage-loan-api")
                .subject("mortgage-loan-api")
                .audience().add("pricing-service").and()
                .claim("scope", "notification:write")
                .claim("type", "service")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(300)))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThatThrownBy(() -> validator.validateNotificationToken(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("audience");
    }
}
