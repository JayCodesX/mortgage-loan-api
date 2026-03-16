package com.jaycodesx.mortgage.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.pricing-result-messaging")
public record PricingResultMessagingProperties(
        boolean enabled,
        String queueName,
        String dlqName,
        String endpoint,
        String region
) {
}
