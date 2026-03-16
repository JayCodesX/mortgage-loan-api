package com.jaycodesx.mortgage.pricing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_products")
public class PricingProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String programCode;

    @Column(nullable = false, length = 80)
    private String productName;

    @Column(nullable = false, precision = 7, scale = 4)
    private BigDecimal baseRate;

    @Column(nullable = false)
    private boolean active;

    public Long getId() { return id; }
    public String getProgramCode() { return programCode; }
    public void setProgramCode(String programCode) { this.programCode = programCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
