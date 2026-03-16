package com.jaycodesx.mortgage.lead.repository;

import com.jaycodesx.mortgage.lead.model.MortgageLead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MortgageLeadRepository extends JpaRepository<MortgageLead, Long> {

    Optional<MortgageLead> findByLoanQuoteId(Long loanQuoteId);
}
