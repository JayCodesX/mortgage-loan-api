package com.jaycodesx.mortgage.pricing.service;

import com.jaycodesx.mortgage.pricing.dto.PricingProductAdminRequestDto;
import com.jaycodesx.mortgage.pricing.dto.PricingProductAdminResponseDto;
import com.jaycodesx.mortgage.pricing.model.PricingProduct;
import com.jaycodesx.mortgage.pricing.repository.PricingProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingProductAdminService {

    private final PricingProductRepository pricingProductRepository;

    public PricingProductAdminService(PricingProductRepository pricingProductRepository) {
        this.pricingProductRepository = pricingProductRepository;
    }

    public List<PricingProductAdminResponseDto> findAll() {
        return pricingProductRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PricingProductAdminResponseDto create(PricingProductAdminRequestDto request) {
        PricingProduct product = new PricingProduct();
        apply(product, request);
        return toResponse(pricingProductRepository.save(product));
    }

    public PricingProductAdminResponseDto update(Long id, PricingProductAdminRequestDto request) {
        PricingProduct product = pricingProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pricing product not found"));
        apply(product, request);
        return toResponse(pricingProductRepository.save(product));
    }

    public void delete(Long id) {
        if (!pricingProductRepository.existsById(id)) {
            throw new IllegalArgumentException("Pricing product not found");
        }
        pricingProductRepository.deleteById(id);
    }

    private void apply(PricingProduct product, PricingProductAdminRequestDto request) {
        product.setProgramCode(request.programCode());
        product.setProductName(request.productName());
        product.setBaseRate(request.baseRate());
        product.setActive(request.active());
    }

    private PricingProductAdminResponseDto toResponse(PricingProduct product) {
        return new PricingProductAdminResponseDto(
                product.getId(),
                product.getProgramCode(),
                product.getProductName(),
                product.getBaseRate(),
                product.isActive()
        );
    }
}
