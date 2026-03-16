package com.jaycodesx.mortgage.lead.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MortgageLeadResponseDtoTest {

    @Test
    void exposesRecordValues() {
        MortgageLeadResponseDto dto = new MortgageLeadResponseDto(2L, 8L, "NEW", "PUBLIC_QUOTE_FUNNEL");

        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.loanQuoteId()).isEqualTo(8L);
        assertThat(dto.leadStatus()).isEqualTo("NEW");
        assertThat(dto.leadSource()).isEqualTo("PUBLIC_QUOTE_FUNNEL");
    }
}
