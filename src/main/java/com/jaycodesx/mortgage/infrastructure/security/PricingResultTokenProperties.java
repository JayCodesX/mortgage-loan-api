package com.jaycodesx.mortgage.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.pricing-result-token")
public record PricingResultTokenProperties(
        String secret,
        String issuer,
        String audience,
        String scope
) {
}
