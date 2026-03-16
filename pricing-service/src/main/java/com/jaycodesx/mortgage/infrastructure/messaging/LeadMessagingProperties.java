package com.jaycodesx.mortgage.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.lead-messaging")
public record LeadMessagingProperties(
        boolean enabled,
        String queueName,
        String dlqName,
        String endpoint,
        String region
) {
}
