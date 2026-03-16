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
@EnableConfigurationProperties({OutboundServiceTokenProperties.class, PricingResultTokenProperties.class})
public class OutboundServiceTokenService {

    private final OutboundServiceTokenProperties properties;
    private final PricingResultTokenProperties pricingResultTokenProperties;
    private final SecretKey signingKey;

    public OutboundServiceTokenService(
            OutboundServiceTokenProperties properties,
            PricingResultTokenProperties pricingResultTokenProperties
    ) {
        this.properties = properties;
        this.pricingResultTokenProperties = pricingResultTokenProperties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateLeadToken() {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(properties.issuer())
                .audience().add(properties.audience()).and()
                .claim("scope", properties.scope())
                .claim("type", "service")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(properties.ttlSeconds())))
                .signWith(signingKey)
                .compact();
    }

    public String generatePricingResultToken() {
        Instant now = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(pricingResultTokenProperties.secret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .issuer(pricingResultTokenProperties.issuer())
                .subject(pricingResultTokenProperties.issuer())
                .audience().add(pricingResultTokenProperties.audience()).and()
                .claim("scope", pricingResultTokenProperties.scope())
                .claim("type", "service")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(pricingResultTokenProperties.ttlSeconds())))
                .signWith(key)
                .compact();
    }
}
