package com.jaycodesx.mortgage.quote.repository;

import com.jaycodesx.mortgage.quote.model.LoanQuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanQuoteRepository extends JpaRepository<LoanQuote, Long> {
}
