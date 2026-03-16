package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.infrastructure.messaging.QuoteMessagingProperties;
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
public class QuoteJobConsumer {

    private final SqsClient sqsClient;
    private final QuoteMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final QuoteJobProcessor quoteJobProcessor;

    public QuoteJobConsumer(SqsClient sqsClient, QuoteMessagingProperties properties, ObjectMapper objectMapper, QuoteJobProcessor quoteJobProcessor) {
        this.sqsClient = sqsClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.quoteJobProcessor = quoteJobProcessor;
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
                    QuoteJobMessage payload = null;
                    try {
                        payload = objectMapper.readValue(message.body(), QuoteJobMessage.class);
                        quoteJobProcessor.process(payload);
                    } catch (Exception ex) {
                        if (payload != null) {
                            quoteJobProcessor.fail(payload.quoteId(), ex.getMessage());
                        }
                    } finally {
                        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build());
                    }
                });
    }
}
