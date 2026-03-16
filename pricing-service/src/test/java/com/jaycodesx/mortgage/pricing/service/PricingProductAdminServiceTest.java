package com.jaycodesx.mortgage.pricing.service;

import com.jaycodesx.mortgage.pricing.dto.PricingProductAdminRequestDto;
import com.jaycodesx.mortgage.pricing.model.PricingProduct;
import com.jaycodesx.mortgage.pricing.repository.PricingProductRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PricingProductAdminServiceTest {

    @Test
    void createsPricingProduct() {
        PricingProductRepository repository = mock(PricingProductRepository.class);
        when(repository.save(any(PricingProduct.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PricingProductAdminService service = new PricingProductAdminService(repository);
        var response = service.create(new PricingProductAdminRequestDto("VA", "VA Fixed", new BigDecimal("5.8750"), true));

        assertThat(response.programCode()).isEqualTo("VA");
        assertThat(response.productName()).isEqualTo("VA Fixed");
    }

    @Test
    void listsProducts() {
        PricingProductRepository repository = mock(PricingProductRepository.class);
        PricingProduct product = new PricingProduct();
        product.setProgramCode("FHA");
        product.setProductName("FHA Streamline");
        product.setBaseRate(new BigDecimal("6.2500"));
        product.setActive(true);
        when(repository.findAll()).thenReturn(List.of(product));

        PricingProductAdminService service = new PricingProductAdminService(repository);
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void throwsWhenUpdatingMissingProduct() {
        PricingProductRepository repository = mock(PricingProductRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        PricingProductAdminService service = new PricingProductAdminService(repository);
        assertThatThrownBy(() -> service.update(99L, new PricingProductAdminRequestDto("FHA", "Test", new BigDecimal("6.0"), true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
