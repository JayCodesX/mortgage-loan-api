package com.jaycodesx.mortgage.infrastructure.config;

import com.jaycodesx.mortgage.borrower.model.Borrower;
import com.jaycodesx.mortgage.borrower.repository.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DataSeederTest {

    @Test
    void seedsBorrowersWhenRepositoryIsEmpty() throws Exception {
        DataSeeder dataSeeder = new DataSeeder();
        BorrowerRepository borrowerRepository = mock(BorrowerRepository.class);
        when(borrowerRepository.count()).thenReturn(0L);

        CommandLineRunner runner = dataSeeder.seedBorrowers(borrowerRepository);
        runner.run();

        verify(borrowerRepository).saveAll(argThat((List<Borrower> borrowers) -> {
            assertThat(borrowers).hasSize(4);
            assertThat(borrowers).allMatch(borrower -> "Jay".equals(borrower.getFirstName()));
            return true;
        }));
    }

    @Test
    void skipsWhenBorrowersAlreadyExist() throws Exception {
        DataSeeder dataSeeder = new DataSeeder();
        BorrowerRepository borrowerRepository = mock(BorrowerRepository.class);
        when(borrowerRepository.count()).thenReturn(1L);

        CommandLineRunner runner = dataSeeder.seedBorrowers(borrowerRepository);
        runner.run();

        verify(borrowerRepository, never()).saveAll(anyList());
    }
}
