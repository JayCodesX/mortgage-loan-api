package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
@EnableConfigurationProperties(ServiceTokenProperties.class)
public class ServiceTokenValidator {

    private final ServiceTokenProperties properties;
    private final SecretKey signingKey;

    public ServiceTokenValidator(ServiceTokenProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public void validateBorrowerReadToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing service bearer token");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);

        Claims claims = claimsJws.getPayload();
        if (!properties.issuer().equals(claims.getIssuer())) {
            throw new IllegalArgumentException("Invalid service token issuer");
        }
        if (claims.getAudience() == null || !claims.getAudience().contains(properties.audience())) {
            throw new IllegalArgumentException("Invalid service token audience");
        }
        if (!properties.scope().equals(claims.get("scope", String.class))) {
            throw new IllegalArgumentException("Invalid service token scope");
        }
        if (!"service".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("Invalid service token type");
        }
    }
}
