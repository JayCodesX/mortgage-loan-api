package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@EnableConfigurationProperties(ServiceTokenProperties.class)
public class ServiceTokenValidator {

    private final ServiceTokenProperties properties;

    public ServiceTokenValidator(ServiceTokenProperties properties) {
        this.properties = properties;
    }

    public void validateNotificationToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!properties.issuer().equals(claims.getIssuer())) {
            throw new IllegalArgumentException("Invalid token issuer");
        }
        if (claims.getAudience() == null || !claims.getAudience().contains(properties.audience())) {
            throw new IllegalArgumentException("Invalid token audience");
        }
        if (!properties.scope().equals(claims.get("scope", String.class))) {
            throw new IllegalArgumentException("Invalid token scope");
        }
        if (!"service".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("Invalid token type");
        }
    }
}
