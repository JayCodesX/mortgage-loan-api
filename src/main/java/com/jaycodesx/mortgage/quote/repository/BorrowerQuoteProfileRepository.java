package com.jaycodesx.mortgage.quote.repository;

import com.jaycodesx.mortgage.quote.model.BorrowerQuoteProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowerQuoteProfileRepository extends JpaRepository<BorrowerQuoteProfile, Long> {

    Optional<BorrowerQuoteProfile> findByLoanQuoteId(Long loanQuoteId);
}
