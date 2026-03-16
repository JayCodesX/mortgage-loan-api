package com.jaycodesx.mortgage.pricing.repository;

import com.jaycodesx.mortgage.pricing.model.RateSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RateSheetRepository extends JpaRepository<RateSheet, Long> {

    List<RateSheet> findByProgramCodeAndPropertyUseAndActiveTrue(String programCode, String propertyUse);
}
