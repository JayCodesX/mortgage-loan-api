package com.jaycodesx.mortgage.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record LeadMessagingProperties(
        boolean enabled,
        boolean consumerEnabled,
        String queueName,
        String dlqName,
        String endpoint,
        String region,
        long pollDelayMs
) {
}
