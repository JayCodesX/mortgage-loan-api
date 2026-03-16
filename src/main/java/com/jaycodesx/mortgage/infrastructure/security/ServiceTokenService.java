package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@EnableConfigurationProperties(ServiceTokenProperties.class)
public class ServiceTokenService {

    private final ServiceTokenProperties properties;
    private final SecretKey signingKey;

    public ServiceTokenService(ServiceTokenProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generatePricingToken() {
        return generateToken(properties.secret(), properties.audience(), properties.scope());
    }

    public String generateToken(String audience, String scope) {
        return generateToken(properties.secret(), audience, scope);
    }

    public String generateToken(String secret, String audience, String scope) {
        Instant now = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(properties.issuer())
                .audience().add(audience).and()
                .claim("scope", scope)
                .claim("type", "service")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(properties.ttlSeconds())))
                .signWith(key)
                .compact();
    }
}
