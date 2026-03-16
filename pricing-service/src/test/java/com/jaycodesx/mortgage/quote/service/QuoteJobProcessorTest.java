package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.infrastructure.metrics.QuoteMetricsService;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import com.jaycodesx.mortgage.lead.service.LeadJobPublisher;
import com.jaycodesx.mortgage.pricing.service.QuotePricingService;
import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteJobProcessorTest {

    @Mock
    private QuotePricingService quotePricingService;
    @Mock
    private QuoteSessionService quoteSessionService;
    @Mock
    private ServiceTokenValidator serviceTokenValidator;
    @Mock
    private LeadJobPublisher leadJobPublisher;
    @Mock
    private PricingResultPublisher pricingResultPublisher;
    @Mock
    private QuoteMetricsService quoteMetricsService;

    @InjectMocks
    private QuoteJobProcessor quoteJobProcessor;

    @Test
    void processesPublicQuoteJob() throws Exception {
        PricingScenario quote = buildQuote();
        QuotePricingService.QuoteDecision decision = new QuotePricingService.QuoteDecision(
                new BigDecimal("6.0500"), new BigDecimal("6.2300"), new BigDecimal("360000.00"),
                new BigDecimal("2178.44"), new BigDecimal("99450.00"), "Market Estimate"
        );
        when(quotePricingService.pricePublicQuote(quote)).thenReturn(decision);

        quoteJobProcessor.process(new QuoteJobMessage(QuoteJobMessage.SCHEMA_VERSION, "public-msg-1", "PUBLIC_QUOTE", 5L, "session-5", null, new BigDecimal("450000.00"), new BigDecimal("90000.00"), "60614", "CONVENTIONAL", "PRIMARY_RESIDENCE", 30, "signed-token", null, null, null, null, null, null, null, null, null, null));

        verify(serviceTokenValidator).validatePricingToken("signed-token");
        verify(pricingResultPublisher, org.mockito.Mockito.times(2)).publish(any());
        verify(quoteSessionService).cacheQuoteStatus(5L, "PROCESSING");
        verify(quoteSessionService).cacheQuoteStatus(5L, "COMPLETED");
        verify(quoteMetricsService).recordQuoteCompleted(5L);
    }

    @Test
    void processesRefinedQuoteJob() throws Exception {
        PricingScenario quote = buildQuote();
        QuoteRefinementRequestDto request = new QuoteRefinementRequestDto(
                "Jay", "Lane", "jay@jaycodesx.dev", "555-111-0101",
                new BigDecimal("120000.00"), new BigDecimal("900.00"), 740,
                new BigDecimal("30000.00"), true, false
        );
        QuotePricingService.QuoteDecision decision = new QuotePricingService.QuoteDecision(
                new BigDecimal("5.9500"), new BigDecimal("6.1300"), new BigDecimal("360000.00"),
                new BigDecimal("2145.00"), new BigDecimal("99500.00"), "Prime"
        );
        when(quotePricingService.priceRefinedQuote(quote, request)).thenReturn(decision);
        quoteJobProcessor.process(new QuoteJobMessage(
                QuoteJobMessage.SCHEMA_VERSION,
                "refined-msg-1",
                "REFINED_QUOTE",
                5L,
                "session-5",
                8L,
                new BigDecimal("450000.00"),
                new BigDecimal("90000.00"),
                "60614",
                "CONVENTIONAL",
                "PRIMARY_RESIDENCE",
                30,
                "signed-token",
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.annualIncome(),
                request.monthlyDebts(),
                request.creditScore(),
                request.cashReserves(),
                request.firstTimeBuyer(),
                request.vaEligible()
        ));

        verify(serviceTokenValidator).validatePricingToken("signed-token");
        verify(pricingResultPublisher, org.mockito.Mockito.times(2)).publish(any());
        verify(leadJobPublisher).publish(any());
        verify(quoteMetricsService).markLeadQueued(5L);
        verify(quoteMetricsService).recordQuoteCompleted(5L);
        verify(quoteSessionService).cacheQuoteStatus(5L, "PROCESSING");
        verify(quoteSessionService).cacheQuoteStatus(5L, "COMPLETED");
    }

    private PricingScenario buildQuote() {
        return new PricingScenario(
                5L,
                new BigDecimal("450000.00"),
                new BigDecimal("90000.00"),
                "60614",
                "CONVENTIONAL",
                "PRIMARY_RESIDENCE",
                30
        );
    }
}
