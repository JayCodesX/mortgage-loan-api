package com.jaycodesx.mortgage.infrastructure.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.internal-admin")
public record InternalServiceClientProperties(
        String borrowerBaseUrl,
        String borrowerSecret,
        String borrowerAudience,
        String borrowerScope,
        String pricingBaseUrl,
        String pricingSecret,
        String pricingAudience,
        String pricingScope,
        String leadBaseUrl,
        String leadSecret,
        String leadAudience,
        String leadScope,
        String authBaseUrl,
        String authSecret,
        String authAudience,
        String authScope,
        String notificationBaseUrl,
        String notificationSecret,
        String notificationAudience,
        String notificationScope
) {
}
