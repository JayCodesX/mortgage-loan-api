package com.jaycodesx.mortgage.quote.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaycodesx.mortgage.infrastructure.messaging.PricingResultMessagingProperties;
import com.jaycodesx.mortgage.infrastructure.security.OutboundServiceTokenService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@EnableConfigurationProperties(PricingResultMessagingProperties.class)
public class PricingResultPublisher {

    private final ObjectProvider<SqsClient> sqsClientProvider;
    private final PricingResultMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final OutboundServiceTokenService outboundServiceTokenService;

    public PricingResultPublisher(
            ObjectProvider<SqsClient> sqsClientProvider,
            PricingResultMessagingProperties properties,
            ObjectMapper objectMapper,
            OutboundServiceTokenService outboundServiceTokenService
    ) {
        this.sqsClientProvider = sqsClientProvider;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.outboundServiceTokenService = outboundServiceTokenService;
    }

    public void publish(PricingResultMessage message) {
        if (!properties.enabled()) {
            throw new IllegalStateException("Pricing result messaging is disabled");
        }

        SqsClient sqsClient = sqsClientProvider.getIfAvailable();
        if (sqsClient == null) {
            throw new IllegalStateException("SQS client is unavailable while pricing result messaging is enabled");
        }

        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(properties.queueName()).build()).queueUrl();
        try {
            PricingResultMessage signed = message.withServiceToken(outboundServiceTokenService.generatePricingResultToken());
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(signed))
                    .build());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize pricing result message", ex);
        }
    }
}
