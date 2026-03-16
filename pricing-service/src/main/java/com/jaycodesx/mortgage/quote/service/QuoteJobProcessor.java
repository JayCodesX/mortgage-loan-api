package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.infrastructure.metrics.QuoteMetricsService;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import com.jaycodesx.mortgage.lead.service.LeadJobMessage;
import com.jaycodesx.mortgage.lead.service.LeadJobPublisher;
import com.jaycodesx.mortgage.pricing.service.QuotePricingService;
import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import org.springframework.stereotype.Service;

@Service
public class QuoteJobProcessor {

    private final QuotePricingService quotePricingService;
    private final QuoteSessionService quoteSessionService;
    private final ServiceTokenValidator serviceTokenValidator;
    private final LeadJobPublisher leadJobPublisher;
    private final PricingResultPublisher pricingResultPublisher;
    private final QuoteMetricsService quoteMetricsService;

    public QuoteJobProcessor(
            QuotePricingService quotePricingService,
            QuoteSessionService quoteSessionService,
            ServiceTokenValidator serviceTokenValidator,
            LeadJobPublisher leadJobPublisher,
            PricingResultPublisher pricingResultPublisher,
            QuoteMetricsService quoteMetricsService
    ) {
        this.quotePricingService = quotePricingService;
        this.quoteSessionService = quoteSessionService;
        this.serviceTokenValidator = serviceTokenValidator;
        this.leadJobPublisher = leadJobPublisher;
        this.pricingResultPublisher = pricingResultPublisher;
        this.quoteMetricsService = quoteMetricsService;
    }

    public void process(QuoteJobMessage message) {
        if (!message.hasSupportedSchemaVersion()) {
            throw new IllegalArgumentException("Unsupported quote job schema version: " + message.schemaVersion());
        }
        serviceTokenValidator.validatePricingToken(message.serviceToken());
        PricingScenario scenario = message.toPricingScenario();

        publishResult(new PricingResultMessage(
                PricingResultMessage.SCHEMA_VERSION,
                null,
                message.quoteId(),
                "PROCESSING",
                null,
                "PROCESSING",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ));
        quoteSessionService.cacheQuoteStatus(message.quoteId(), "PROCESSING");

        if ("PUBLIC_QUOTE".equals(message.jobType())) {
            QuotePricingService.QuoteDecision decision = quotePricingService.pricePublicQuote(scenario);
            publishDecision(scenario, decision, "PUBLIC", "ESTIMATED");
            quoteSessionService.cacheQuoteStatus(message.quoteId(), "COMPLETED");
            quoteMetricsService.recordQuoteCompleted(message.quoteId());
            return;
        }

        QuoteRefinementRequestDto request = message.toRefinementRequest();
        QuotePricingService.QuoteDecision decision = quotePricingService.priceRefinedQuote(scenario, request);
        publishDecision(scenario, decision, "REFINED", "LEAD_READY");

        leadJobPublisher.publish(new LeadJobMessage(
                LeadJobMessage.SCHEMA_VERSION,
                null,
                message.quoteId(),
                message.borrowerQuoteProfileId(),
                "NEW",
                "PUBLIC_QUOTE_FUNNEL",
                null
        ));
        quoteMetricsService.markLeadQueued(message.quoteId());

        quoteSessionService.cacheQuoteStatus(message.quoteId(), "COMPLETED");
        quoteMetricsService.recordQuoteCompleted(message.quoteId());
    }

    public void fail(Long quoteId, String message) {
        publishResult(new PricingResultMessage(
                PricingResultMessage.SCHEMA_VERSION,
                null,
                quoteId,
                "FAILED",
                null,
                "FAILED",
                null,
                null,
                null,
                null,
                null,
                null,
                message,
                null
        ));
        quoteSessionService.cacheQuoteStatus(quoteId, "FAILED");
        quoteMetricsService.recordQuoteFailed(quoteId);
    }

    private void publishDecision(PricingScenario scenario, QuotePricingService.QuoteDecision decision, String quoteStage, String quoteStatus) {
        publishResult(new PricingResultMessage(
                PricingResultMessage.SCHEMA_VERSION,
                null,
                scenario.quoteId(),
                "COMPLETED",
                quoteStage,
                quoteStatus,
                decision.estimatedRate(),
                decision.estimatedApr(),
                decision.financedAmount(),
                decision.estimatedMonthlyPayment(),
                decision.estimatedCashToClose(),
                decision.qualificationTier(),
                null,
                null
        ));
    }

    private void publishResult(PricingResultMessage message) {
        pricingResultPublisher.publish(message);
    }
}
