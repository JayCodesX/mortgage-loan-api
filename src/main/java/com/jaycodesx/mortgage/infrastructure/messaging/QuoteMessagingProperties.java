package com.jaycodesx.mortgage.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record QuoteMessagingProperties(
        boolean enabled,
        boolean consumerEnabled,
        String queueName,
        String pricingResultQueueName,
        String leadResultQueueName,
        String notificationQueueName,
        String pricingResultDlqName,
        String leadResultDlqName,
        String notificationDlqName,
        String endpoint,
        String region,
        long pollDelayMs
) {
}
