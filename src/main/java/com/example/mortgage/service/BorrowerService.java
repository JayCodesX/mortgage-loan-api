package com.example.mortgage.service;

import com.example.mortgage.dto.BorrowerCreateRequestDto;
import com.example.mortgage.model.Borrower;
import com.example.mortgage.repository.BorrowerRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public Borrower createBorrower(BorrowerCreateRequestDto request) {
        Borrower borrower = new Borrower();
        borrower.setFirstName(request.firstName());
        borrower.setLastName(request.lastName());
        borrower.setEmail(request.email());
        borrower.setCreditScore(request.creditScore());
        return borrowerRepository.save(borrower);
    }
}
