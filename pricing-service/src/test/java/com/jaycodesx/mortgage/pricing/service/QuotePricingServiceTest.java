package com.jaycodesx.mortgage.pricing.service;

import com.jaycodesx.mortgage.pricing.model.PricingAdjustmentRule;
import com.jaycodesx.mortgage.pricing.model.PricingProduct;
import com.jaycodesx.mortgage.pricing.model.RateSheet;
import com.jaycodesx.mortgage.pricing.repository.PricingAdjustmentRuleRepository;
import com.jaycodesx.mortgage.pricing.repository.PricingProductRepository;
import com.jaycodesx.mortgage.pricing.repository.RateSheetRepository;
import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import com.jaycodesx.mortgage.quote.service.PricingScenario;
import com.jaycodesx.mortgage.shared.service.MortgageMathService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuotePricingServiceTest {

    @Mock
    private PricingCacheService pricingCacheService;
    @Mock
    private PricingProductRepository pricingProductRepository;
    @Mock
    private RateSheetRepository rateSheetRepository;
    @Mock
    private PricingAdjustmentRuleRepository pricingAdjustmentRuleRepository;

    @Test
    void returnsCachedPublicDecisionWhenAvailable() {
        PricingScenario quote = buildQuote();
        QuotePricingService.QuoteDecision cachedDecision = new QuotePricingService.QuoteDecision(
                new BigDecimal("6.0100"),
                new BigDecimal("6.1900"),
                new BigDecimal("420000.00"),
                new BigDecimal("2520.00"),
                new BigDecimal("116025.00"),
                "Market Estimate"
        );
        when(pricingCacheService.getPublicDecision(quote)).thenReturn(Optional.of(cachedDecision));

        QuotePricingService service = new QuotePricingService(
                new MortgageMathService(),
                pricingCacheService,
                pricingProductRepository,
                rateSheetRepository,
                pricingAdjustmentRuleRepository
        );
        QuotePricingService.QuoteDecision result = service.pricePublicQuote(quote);

        assertThat(result).isEqualTo(cachedDecision);
        verify(pricingCacheService).getPublicDecision(quote);
        verifyNoMoreInteractions(pricingCacheService);
    }

    @Test
    void cachesComputedRefinedDecisionOnMiss() {
        PricingScenario quote = buildQuote();
        QuoteRefinementRequestDto request = new QuoteRefinementRequestDto(
                "Jay", "Banks", "jay@jaycodesx.dev", "555-111-0101",
                new BigDecimal("145000.00"), new BigDecimal("1100.00"), 760,
                new BigDecimal("50000.00"), true, false
        );
        when(pricingCacheService.getRefinedDecision(quote, request)).thenReturn(Optional.empty());
        when(pricingProductRepository.findByProgramCodeAndActiveTrue("CONVENTIONAL")).thenReturn(Optional.of(product("6.2500")));
        when(rateSheetRepository.findByProgramCodeAndPropertyUseAndActiveTrue("CONVENTIONAL", "PRIMARY_RESIDENCE"))
                .thenReturn(java.util.List.of(rateSheet("981", "0.0000")));
        when(pricingAdjustmentRuleRepository.findByRuleTypeAndActiveTrue("DOWN_PAYMENT"))
                .thenReturn(java.util.List.of(rule("GE_20", "-0.2000"), rule("GE_10", "0.0000"), rule("GE_5", "0.1800"), rule("LT_5", "0.3200")));
        when(pricingAdjustmentRuleRepository.findByRuleTypeAndActiveTrue("CREDIT_SCORE"))
                .thenReturn(java.util.List.of(rule("GE_760", "-0.3500"), rule("GE_720", "-0.1500"), rule("GE_680", "0.0500"), rule("GE_640", "0.2200"), rule("LT_640", "0.4500")));
        when(pricingAdjustmentRuleRepository.findByRuleTypeAndActiveTrue("DTI"))
                .thenReturn(java.util.List.of(rule("GT_43", "0.2500"), rule("GT_36", "0.1200"), rule("LT_28", "-0.1000")));
        when(pricingAdjustmentRuleRepository.findByRuleTypeAndActiveTrue("FLAG"))
                .thenReturn(java.util.List.of(rule("FIRST_TIME_BUYER", "-0.0500"), rule("VA_ELIGIBLE", "-0.1000")));

        QuotePricingService service = new QuotePricingService(
                new MortgageMathService(),
                pricingCacheService,
                pricingProductRepository,
                rateSheetRepository,
                pricingAdjustmentRuleRepository
        );
        QuotePricingService.QuoteDecision result = service.priceRefinedQuote(quote, request);

        assertThat(result.estimatedRate()).isEqualByComparingTo("5.5500");
        assertThat(result.qualificationTier()).isEqualTo("Prime+");
        verify(pricingCacheService).getRefinedDecision(quote, request);
        verify(pricingCacheService).cacheRefinedDecision(quote, request, result);
    }

    private PricingScenario buildQuote() {
        return new PricingScenario(
                5L,
                new BigDecimal("525000.00"),
                new BigDecimal("105000.00"),
                "98101",
                "CONVENTIONAL",
                "PRIMARY_RESIDENCE",
                30
        );
    }

    private PricingProduct product(String rate) {
        PricingProduct product = new PricingProduct();
        product.setProgramCode("CONVENTIONAL");
        product.setProductName("Conventional 30 Fixed");
        product.setBaseRate(new BigDecimal(rate));
        product.setActive(true);
        return product;
    }

    private RateSheet rateSheet(String zipPrefix, String adjustment) {
        RateSheet sheet = new RateSheet();
        sheet.setProgramCode("CONVENTIONAL");
        sheet.setPropertyUse("PRIMARY_RESIDENCE");
        sheet.setZipPrefix(zipPrefix);
        sheet.setZipAdjustment(new BigDecimal(adjustment));
        sheet.setActive(true);
        return sheet;
    }

    private PricingAdjustmentRule rule(String key, String adjustment) {
        PricingAdjustmentRule rule = new PricingAdjustmentRule();
        rule.setRuleType("IGNORED");
        rule.setRuleKey(key);
        rule.setAdjustment(new BigDecimal(adjustment));
        rule.setActive(true);
        return rule;
    }
}
