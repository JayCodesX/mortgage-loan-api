package com.jaycodesx.mortgage.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.lead-result-messaging")
public record LeadResultMessagingProperties(
        boolean enabled,
        String queueName,
        String dlqName,
        String endpoint,
        String region
) {
}
