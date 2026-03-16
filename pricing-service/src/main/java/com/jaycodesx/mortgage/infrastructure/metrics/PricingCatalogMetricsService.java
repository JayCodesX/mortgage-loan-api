package com.jaycodesx.mortgage.infrastructure.metrics;

import com.jaycodesx.mortgage.pricing.model.PricingAdjustmentRule;
import com.jaycodesx.mortgage.pricing.model.PricingProduct;
import com.jaycodesx.mortgage.pricing.model.RateSheet;
import com.jaycodesx.mortgage.pricing.repository.PricingAdjustmentRuleRepository;
import com.jaycodesx.mortgage.pricing.repository.PricingProductRepository;
import com.jaycodesx.mortgage.pricing.repository.RateSheetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PricingCatalogMetricsService {

    private final PricingProductRepository pricingProductRepository;
    private final RateSheetRepository rateSheetRepository;
    private final PricingAdjustmentRuleRepository pricingAdjustmentRuleRepository;

    public PricingCatalogMetricsService(
            PricingProductRepository pricingProductRepository,
            RateSheetRepository rateSheetRepository,
            PricingAdjustmentRuleRepository pricingAdjustmentRuleRepository
    ) {
        this.pricingProductRepository = pricingProductRepository;
        this.rateSheetRepository = rateSheetRepository;
        this.pricingAdjustmentRuleRepository = pricingAdjustmentRuleRepository;
    }

    public PricingMetricsResponseDto getSnapshot() {
        List<PricingProduct> products = pricingProductRepository.findAll();
        List<RateSheet> rateSheets = rateSheetRepository.findAll();
        List<PricingAdjustmentRule> rules = pricingAdjustmentRuleRepository.findAll();

        Map<String, Long> distribution = products.stream()
                .collect(Collectors.groupingBy(PricingProduct::getProgramCode, Collectors.counting()));

        return new PricingMetricsResponseDto(
                products.size(),
                products.stream().filter(PricingProduct::isActive).count(),
                rateSheets.stream().filter(RateSheet::isActive).count(),
                rules.stream().filter(PricingAdjustmentRule::isActive).count(),
                distribution.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> new MetricSliceDto(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }
}
