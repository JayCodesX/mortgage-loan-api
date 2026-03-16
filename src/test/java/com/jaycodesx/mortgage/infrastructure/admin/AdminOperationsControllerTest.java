package com.jaycodesx.mortgage.infrastructure.admin;

import com.jaycodesx.mortgage.infrastructure.security.UserTokenAuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminOperationsControllerTest {

    @Test
    void returnsProductListForAdmin() {
        UserTokenAuthorizationService authService = mock(UserTokenAuthorizationService.class);
        AdminMetricsClientService clientService = mock(AdminMetricsClientService.class);
        AdminReportService reportService = mock(AdminReportService.class);
        doNothing().when(authService).requireAdminUser("Bearer token");
        when(clientService.fetchProducts()).thenReturn(List.of(new AdminPricingProductResponseDto(1L, "CONVENTIONAL", "Demo 30-Year Fixed", new BigDecimal("6.1250"), true)));

        ResponseEntity<List<AdminPricingProductResponseDto>> response = new AdminOperationsController(authService, clientService, reportService)
                .getProducts("Bearer token")
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void returnsReportForAdmin() {
        UserTokenAuthorizationService authService = mock(UserTokenAuthorizationService.class);
        AdminMetricsClientService clientService = mock(AdminMetricsClientService.class);
        AdminReportService reportService = mock(AdminReportService.class);
        doNothing().when(authService).requireAdminUser("Bearer token");
        AdminReportQueryDto query = new AdminReportQueryDto("PRODUCTS", "", false, "", null, null, "");
        when(reportService.runReport(query)).thenReturn(new AdminReportResponseDto("Pricing products", List.of("id"), List.of(Map.of("id", 1)), 1));

        ResponseEntity<AdminReportResponseDto> response = new AdminOperationsController(authService, clientService, reportService)
                .queryReport("Bearer token", query)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBody().totalRows()).isEqualTo(1);
    }
}
