package com.jaycodesx.mortgage.quote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaycodesx.mortgage.infrastructure.messaging.QuoteMessagingProperties;
import com.jaycodesx.mortgage.infrastructure.messaging.DeadLetterQueuePublisher;
import com.jaycodesx.mortgage.infrastructure.messaging.MessageDeduplicationService;
import com.jaycodesx.mortgage.infrastructure.metrics.QuoteMetricsService;
import com.jaycodesx.mortgage.infrastructure.security.InboundServiceTokenValidator;
import com.jaycodesx.mortgage.infrastructure.security.PricingResultTokenProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
@EnableConfigurationProperties(PricingResultTokenProperties.class)
public class PricingResultConsumer {

    private final SqsClient sqsClient;
    private final QuoteMessagingProperties properties;
    private final ObjectMapper objectMapper;
    private final LoanQuoteService loanQuoteService;
    private final InboundServiceTokenValidator tokenValidator;
    private final MessageDeduplicationService messageDeduplicationService;
    private final DeadLetterQueuePublisher deadLetterQueuePublisher;
    private final PricingResultTokenProperties tokenProperties;
    private final QuoteMetricsService quoteMetricsService;

    public PricingResultConsumer(
            SqsClient sqsClient,
            QuoteMessagingProperties properties,
            ObjectMapper objectMapper,
            LoanQuoteService loanQuoteService,
            InboundServiceTokenValidator tokenValidator,
            MessageDeduplicationService messageDeduplicationService,
            DeadLetterQueuePublisher deadLetterQueuePublisher,
            PricingResultTokenProperties tokenProperties,
            QuoteMetricsService quoteMetricsService
    ) {
        this.sqsClient = sqsClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.loanQuoteService = loanQuoteService;
        this.tokenValidator = tokenValidator;
        this.messageDeduplicationService = messageDeduplicationService;
        this.deadLetterQueuePublisher = deadLetterQueuePublisher;
        this.tokenProperties = tokenProperties;
        this.quoteMetricsService = quoteMetricsService;
    }

    @Scheduled(fixedDelayString = "${app.messaging.poll-delay-ms:3000}")
    public void poll() {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(properties.pricingResultQueueName())
                .build()).queueUrl();
        sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(1)
                        .build())
                .messages()
                .forEach(message -> {
                    try {
                        PricingResultMessage payload = objectMapper.readValue(message.body(), PricingResultMessage.class);
                        if (!payload.hasSupportedSchemaVersion()) {
                            throw new IllegalArgumentException("Unsupported pricing result schema version: " + payload.schemaVersion());
                        }
                        if (!messageDeduplicationService.firstReceipt("pricing-result", payload.messageId())) {
                            quoteMetricsService.recordPricingResultMessageDeduped();
                            return;
                        }
                        tokenValidator.validate(
                                payload.serviceToken(),
                                tokenProperties.secret(),
                                tokenProperties.issuer(),
                                tokenProperties.audience(),
                                tokenProperties.scope()
                        );
                        loanQuoteService.applyPricingResult(payload);
                    } catch (Exception ex) {
                        try {
                            deadLetterQueuePublisher.publish(properties.pricingResultDlqName(), message.body(), ex.getMessage());
                            quoteMetricsService.recordPricingResultDlqPublish();
                        } catch (Exception ignored) {
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
