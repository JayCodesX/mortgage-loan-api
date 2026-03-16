package com.jaycodesx.mortgage.lead.service;

import com.jaycodesx.mortgage.infrastructure.messaging.LeadMessagingProperties;
import com.jaycodesx.mortgage.infrastructure.security.OutboundServiceTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@EnableConfigurationProperties(LeadMessagingProperties.class)
public class LeadJobPublisher {

    private final ObjectProvider<SqsClient> sqsClientProvider;
    private final LeadMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final OutboundServiceTokenService outboundServiceTokenService;

    public LeadJobPublisher(
            ObjectProvider<SqsClient> sqsClientProvider,
            LeadMessagingProperties properties,
            ObjectMapper objectMapper,
            OutboundServiceTokenService outboundServiceTokenService
    ) {
        this.sqsClientProvider = sqsClientProvider;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.outboundServiceTokenService = outboundServiceTokenService;
    }

    public void publish(LeadJobMessage message) {
        if (!properties.enabled()) {
            throw new IllegalStateException("Lead messaging is disabled");
        }

        SqsClient sqsClient = sqsClientProvider.getIfAvailable();
        if (sqsClient == null) {
            throw new IllegalStateException("SQS client is unavailable while lead messaging is enabled");
        }

        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(properties.queueName()).build()).queueUrl();
        try {
            LeadJobMessage signed = message.withServiceToken(outboundServiceTokenService.generateLeadToken());
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(signed))
                    .build());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize lead job message", ex);
        }
    }
}
