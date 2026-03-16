package com.jaycodesx.mortgage.infrastructure.config;

import com.jaycodesx.mortgage.lead.model.MortgageLead;
import com.jaycodesx.mortgage.lead.repository.MortgageLeadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class DataSeederTest {

    @Test
    void seedsLeadsWhenRepositoryIsEmpty() throws Exception {
        DataSeeder dataSeeder = new DataSeeder();
        MortgageLeadRepository mortgageLeadRepository = mock(MortgageLeadRepository.class);
        when(mortgageLeadRepository.count()).thenReturn(0L);

        CommandLineRunner runner = dataSeeder.seedLeads(mortgageLeadRepository);
        runner.run();

        verify(mortgageLeadRepository).saveAll(argThat((List<MortgageLead> leads) -> {
            assertThat(leads).hasSize(3);
            assertThat(leads).extracting(MortgageLead::getLeadStatus)
                    .containsExactlyInAnyOrder("NEW", "CONTACTED", "QUALIFIED");
            return true;
        }));
    }

    @Test
    void skipsWhenLeadsAlreadyExist() throws Exception {
        DataSeeder dataSeeder = new DataSeeder();
        MortgageLeadRepository mortgageLeadRepository = mock(MortgageLeadRepository.class);
        when(mortgageLeadRepository.count()).thenReturn(1L);

        CommandLineRunner runner = dataSeeder.seedLeads(mortgageLeadRepository);
        runner.run();

        verify(mortgageLeadRepository, never()).saveAll(anyList());
    }
}
