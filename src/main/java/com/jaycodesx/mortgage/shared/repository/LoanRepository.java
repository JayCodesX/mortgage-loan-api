package com.jaycodesx.mortgage.shared.repository;

import com.jaycodesx.mortgage.shared.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
