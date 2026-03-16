package com.jaycodesx.mortgage.pricing.service;

import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import com.jaycodesx.mortgage.quote.model.LoanQuote;
import com.jaycodesx.mortgage.shared.service.MortgageMathService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class QuotePricingServiceTest {

    private final QuotePricingService quotePricingService = new QuotePricingService(new MortgageMathService());

    @Test
    void pricesPublicQuote() {
        LoanQuote quote = buildQuote();

        QuotePricingService.QuoteDecision decision = quotePricingService.pricePublicQuote(quote);

        assertThat(decision.financedAmount()).isEqualByComparingTo("360000.00");
        assertThat(decision.estimatedRate()).isGreaterThan(new BigDecimal("4.7500"));
        assertThat(decision.estimatedMonthlyPayment()).isGreaterThan(BigDecimal.ZERO);
        assertThat(decision.qualificationTier()).isEqualTo("Market Estimate");
    }

    @Test
    void pricesRefinedQuoteUsingBorrowerProfileInputs() {
        LoanQuote quote = buildQuote();
        QuoteRefinementRequestDto profile = new QuoteRefinementRequestDto(
                "Jay",
                "Lane",
                "jay@jaycodesx.dev",
                "555-111-0101",
                new BigDecimal("145000.00"),
                new BigDecimal("900.00"),
                775,
                new BigDecimal("50000.00"),
                true,
                false
        );

        QuotePricingService.QuoteDecision decision = quotePricingService.priceRefinedQuote(quote, profile);

        assertThat(decision.qualificationTier()).isEqualTo("Prime+");
        assertThat(decision.estimatedApr()).isGreaterThan(decision.estimatedRate());
        assertThat(decision.estimatedCashToClose()).isEqualByComparingTo("99450.00");
    }

    private LoanQuote buildQuote() {
        LoanQuote quote = new LoanQuote();
        quote.setHomePrice(new BigDecimal("450000.00"));
        quote.setDownPayment(new BigDecimal("90000.00"));
        quote.setZipCode("60614");
        quote.setLoanProgram("CONVENTIONAL");
        quote.setPropertyUse("PRIMARY_RESIDENCE");
        quote.setTermYears(30);
        return quote;
    }
}
