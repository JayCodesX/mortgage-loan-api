package com.jaycodesx.mortgage.lead.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaycodesx.mortgage.infrastructure.messaging.LeadResultMessagingProperties;
import com.jaycodesx.mortgage.infrastructure.security.OutboundServiceTokenService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@EnableConfigurationProperties(LeadResultMessagingProperties.class)
public class LeadResultPublisher {

    private final ObjectProvider<SqsClient> sqsClientProvider;
    private final LeadResultMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final OutboundServiceTokenService outboundServiceTokenService;

    public LeadResultPublisher(
            ObjectProvider<SqsClient> sqsClientProvider,
            LeadResultMessagingProperties properties,
            ObjectMapper objectMapper,
            OutboundServiceTokenService outboundServiceTokenService
    ) {
        this.sqsClientProvider = sqsClientProvider;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.outboundServiceTokenService = outboundServiceTokenService;
    }

    public void publish(LeadResultMessage message) {
        if (!properties.enabled()) {
            throw new IllegalStateException("Lead result messaging is disabled");
        }

        SqsClient sqsClient = sqsClientProvider.getIfAvailable();
        if (sqsClient == null) {
            throw new IllegalStateException("SQS client is unavailable while lead result messaging is enabled");
        }

        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(properties.queueName()).build()).queueUrl();
        try {
            LeadResultMessage signed = message.withServiceToken(outboundServiceTokenService.generateLeadResultToken());
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(signed))
                    .build());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize lead result message", ex);
        }
    }
}
