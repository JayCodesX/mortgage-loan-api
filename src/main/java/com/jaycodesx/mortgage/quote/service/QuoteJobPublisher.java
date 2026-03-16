package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.infrastructure.messaging.QuoteMessagingProperties;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class QuoteJobPublisher {

    private final ObjectProvider<SqsClient> sqsClientProvider;
    private final QuoteMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final ServiceTokenService serviceTokenService;

    public QuoteJobPublisher(
            ObjectProvider<SqsClient> sqsClientProvider,
            QuoteMessagingProperties properties,
            ObjectMapper objectMapper,
            ServiceTokenService serviceTokenService
    ) {
        this.sqsClientProvider = sqsClientProvider;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.serviceTokenService = serviceTokenService;
    }

    public void publish(QuoteJobMessage message) {
        if (!properties.enabled()) {
            throw new IllegalStateException("Quote messaging is disabled");
        }

        SqsClient sqsClient = sqsClientProvider.getIfAvailable();
        if (sqsClient == null) {
            throw new IllegalStateException("SQS client is unavailable while messaging is enabled");
        }

        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(properties.queueName()).build()).queueUrl();
        try {
            QuoteJobMessage signedMessage = message.withServiceToken(serviceTokenService.generatePricingToken());
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(signedMessage))
                    .build());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize quote job message", ex);
        }
    }
}
