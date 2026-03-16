package com.jaycodesx.mortgage.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.outbound-service-token")
public record OutboundServiceTokenProperties(
        String secret,
        String issuer,
        String audience,
        String scope,
        long ttlSeconds
) {
}
