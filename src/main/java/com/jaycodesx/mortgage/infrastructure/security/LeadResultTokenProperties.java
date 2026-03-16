package com.jaycodesx.mortgage.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.lead-result-token")
public record LeadResultTokenProperties(
        String secret,
        String issuer,
        String audience,
        String scope
) {
}
