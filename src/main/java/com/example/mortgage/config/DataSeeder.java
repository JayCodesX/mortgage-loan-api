package com.example.mortgage.config;

import com.example.mortgage.model.Borrower;
import com.example.mortgage.model.Loan;
import com.example.mortgage.repository.BorrowerRepository;
import com.example.mortgage.repository.LoanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class DataSeeder {

    private static final List<String> STATUSES = List.of("PENDING", "APPROVED", "UNDER_REVIEW");

    @Bean
    CommandLineRunner seedData(BorrowerRepository borrowerRepository, LoanRepository loanRepository) {
        return args -> {
            if (borrowerRepository.count() > 0) {
                return;
            }

            List<Borrower> borrowers = List.of(
                    createBorrower("Jay", "Carter", "jay.carter@example.com", randomInt(620, 810)),
                    createBorrower("Jay", "Miller", "jay.miller@example.com", randomInt(600, 790)),
                    createBorrower("Jay", "Nguyen", "jay.nguyen@example.com", randomInt(640, 830)),
                    createBorrower("Jay", "Patel", "jay.patel@example.com", randomInt(610, 800))
            ).stream().map(borrowerRepository::save).toList();

            borrowers.forEach(borrower -> loanRepository.save(createLoanForBorrower(borrower.getId())));
        };
    }

    private Borrower createBorrower(String firstName, String lastName, String email, int creditScore) {
        Borrower borrower = new Borrower();
        borrower.setFirstName(firstName);
        borrower.setLastName(lastName);
        borrower.setEmail(email);
        borrower.setCreditScore(creditScore);
        return borrower;
    }

    private Loan createLoanForBorrower(Long borrowerId) {
        Loan loan = new Loan();
        loan.setBorrowerId(borrowerId);
        loan.setLoanAmount(randomCurrency(180000, 620000));
        loan.setInterestRate(randomRate(4.0, 8.2));
        loan.setTermYears(randomInt(15, 30));
        loan.setStatus(STATUSES.get(randomInt(0, STATUSES.size() - 1)));
        return loan;
    }

    private int randomInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    private BigDecimal randomCurrency(int minInclusive, int maxInclusive) {
        int amount = randomInt(minInclusive, maxInclusive);
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal randomRate(double minInclusive, double maxInclusive) {
        double raw = ThreadLocalRandom.current().nextDouble(minInclusive, maxInclusive);
        return BigDecimal.valueOf(raw).setScale(4, RoundingMode.HALF_UP);
    }
}
