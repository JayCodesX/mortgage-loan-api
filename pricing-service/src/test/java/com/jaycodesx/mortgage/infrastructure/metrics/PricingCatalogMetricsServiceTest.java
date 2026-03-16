package com.jaycodesx.mortgage.infrastructure.metrics;

import com.jaycodesx.mortgage.pricing.model.PricingAdjustmentRule;
import com.jaycodesx.mortgage.pricing.model.PricingProduct;
import com.jaycodesx.mortgage.pricing.model.RateSheet;
import com.jaycodesx.mortgage.pricing.repository.PricingAdjustmentRuleRepository;
import com.jaycodesx.mortgage.pricing.repository.PricingProductRepository;
import com.jaycodesx.mortgage.pricing.repository.RateSheetRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PricingCatalogMetricsServiceTest {

    @Test
    void buildsPricingCatalogSnapshot() {
        PricingProductRepository products = mock(PricingProductRepository.class);
        RateSheetRepository rateSheets = mock(RateSheetRepository.class);
        PricingAdjustmentRuleRepository rules = mock(PricingAdjustmentRuleRepository.class);

        PricingProduct conventional = new PricingProduct();
        conventional.setProgramCode("CONVENTIONAL");
        conventional.setProductName("Conv 30");
        conventional.setBaseRate(new BigDecimal("6.1250"));
        conventional.setActive(true);
        PricingProduct fha = new PricingProduct();
        fha.setProgramCode("FHA");
        fha.setProductName("FHA 30");
        fha.setBaseRate(new BigDecimal("6.0000"));
        fha.setActive(false);

        RateSheet rateSheet = new RateSheet();
        rateSheet.setActive(true);
        PricingAdjustmentRule rule = new PricingAdjustmentRule();
        rule.setActive(true);

        when(products.findAll()).thenReturn(List.of(conventional, fha));
        when(rateSheets.findAll()).thenReturn(List.of(rateSheet));
        when(rules.findAll()).thenReturn(List.of(rule));

        PricingMetricsResponseDto snapshot = new PricingCatalogMetricsService(products, rateSheets, rules).getSnapshot();

        assertThat(snapshot.totalProducts()).isEqualTo(2);
        assertThat(snapshot.activeProducts()).isEqualTo(1);
        assertThat(snapshot.activeRateSheets()).isEqualTo(1);
        assertThat(snapshot.activeAdjustmentRules()).isEqualTo(1);
        assertThat(snapshot.programDistribution()).hasSize(2);
    }
}
