package com.jaycodesx.mortgage.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@Component
public class DeadLetterQueuePublisher {

    private final ObjectProvider<SqsClient> sqsClientProvider;
    private final ObjectMapper objectMapper;

    public DeadLetterQueuePublisher(ObjectProvider<SqsClient> sqsClientProvider, ObjectMapper objectMapper) {
        this.sqsClientProvider = sqsClientProvider;
        this.objectMapper = objectMapper;
    }

    public void publish(String queueName, Object payload, String errorMessage) {
        SqsClient sqsClient = sqsClientProvider.getIfAvailable();
        if (sqsClient == null || queueName == null || queueName.isBlank()) {
            return;
        }
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "error", errorMessage,
                    "payload", payload
            ));
            sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(body).build());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize dead-letter message", ex);
        }
    }
}
