package com.jaycodesx.mortgage.lead.service;

import com.jaycodesx.mortgage.infrastructure.messaging.LeadMessagingProperties;
import com.jaycodesx.mortgage.infrastructure.messaging.MessageDeduplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@ConditionalOnProperty(name = "app.messaging.consumer-enabled", havingValue = "true")
public class LeadJobConsumer {

    private final SqsClient sqsClient;
    private final LeadMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final LeadJobProcessor leadJobProcessor;
    private final MessageDeduplicationService messageDeduplicationService;

    public LeadJobConsumer(
            SqsClient sqsClient,
            LeadMessagingProperties properties,
            ObjectMapper objectMapper,
            LeadJobProcessor leadJobProcessor,
            MessageDeduplicationService messageDeduplicationService
    ) {
        this.sqsClient = sqsClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.leadJobProcessor = leadJobProcessor;
        this.messageDeduplicationService = messageDeduplicationService;
    }

    @Scheduled(fixedDelayString = "${app.messaging.poll-delay-ms:3000}")
    public void poll() {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(properties.queueName()).build()).queueUrl();
        sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(1)
                        .build())
                .messages()
                .forEach(message -> {
                    try {
                        LeadJobMessage payload = objectMapper.readValue(message.body(), LeadJobMessage.class);
                        if (!payload.hasSupportedSchemaVersion()) {
                            throw new IllegalArgumentException("Unsupported lead job schema version: " + payload.schemaVersion());
                        }
                        if (!messageDeduplicationService.firstReceipt("lead-job", payload.messageId())) {
                            return;
                        }
                        leadJobProcessor.process(payload);
                    } catch (Exception ex) {
                        // Delete the message after a failed attempt to avoid an infinite poison-message loop in local dev.
                    } finally {
                        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build());
                    }
                });
    }
}
