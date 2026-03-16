package com.jaycodesx.mortgage.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaycodesx.mortgage.infrastructure.messaging.MessageDeduplicationService;
import com.jaycodesx.mortgage.infrastructure.messaging.NotificationMessagingProperties;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
@EnableConfigurationProperties(NotificationMessagingProperties.class)
public class QuoteNotificationConsumer {

    private final SqsClient sqsClient;
    private final NotificationMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final ServiceTokenValidator serviceTokenValidator;
    private final NotificationSnapshotService notificationSnapshotService;
    private final MessageDeduplicationService messageDeduplicationService;

    public QuoteNotificationConsumer(
            SqsClient sqsClient,
            NotificationMessagingProperties properties,
            ObjectMapper objectMapper,
            ServiceTokenValidator serviceTokenValidator,
            NotificationSnapshotService notificationSnapshotService,
            MessageDeduplicationService messageDeduplicationService
    ) {
        this.sqsClient = sqsClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.serviceTokenValidator = serviceTokenValidator;
        this.notificationSnapshotService = notificationSnapshotService;
        this.messageDeduplicationService = messageDeduplicationService;
    }

    @Scheduled(fixedDelayString = "${app.messaging.poll-delay-ms:3000}")
    public void poll() {
        String queueUrl;
        try {
            queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(properties.queueName())
                    .build()).queueUrl();
        } catch (QueueDoesNotExistException ignored) {
            return;
        }
        sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(1)
                        .build())
                .messages()
                .forEach(message -> {
                    try {
                        QuoteNotificationMessage payload = objectMapper.readValue(message.body(), QuoteNotificationMessage.class);
                        if (!payload.hasSupportedSchemaVersion()) {
                            throw new IllegalArgumentException("Unsupported notification schema version: " + payload.schemaVersion());
                        }
                        if (!messageDeduplicationService.firstReceipt("quote-notification", payload.messageId())) {
                            return;
                        }
                        serviceTokenValidator.validateNotificationToken(payload.serviceToken());
                        notificationSnapshotService.store(payload);
                    } catch (Exception ex) {
                        // Local dev consumer deletes poison messages after one failed attempt.
                    } finally {
                        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build());
                    }
                });
    }
}
