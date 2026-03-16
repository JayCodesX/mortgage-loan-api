package com.jaycodesx.mortgage.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.user-token")
public record UserTokenProperties(
        String secret,
        String issuer,
        String audience,
        long ttlSeconds
) {
}
