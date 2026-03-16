package com.jaycodesx.mortgage.pricing.repository;

import com.jaycodesx.mortgage.pricing.model.PricingAdjustmentRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingAdjustmentRuleRepository extends JpaRepository<PricingAdjustmentRule, Long> {

    List<PricingAdjustmentRule> findByRuleTypeAndActiveTrue(String ruleType);
}
