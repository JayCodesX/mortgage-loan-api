package com.jaycodesx.mortgage.pricing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_sheets")
public class RateSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String programCode;

    @Column(nullable = false, length = 40)
    private String propertyUse;

    @Column(nullable = false, length = 5)
    private String zipPrefix;

    @Column(nullable = false, precision = 7, scale = 4)
    private BigDecimal zipAdjustment;

    @Column(nullable = false)
    private boolean active;

    public Long getId() { return id; }
    public String getProgramCode() { return programCode; }
    public void setProgramCode(String programCode) { this.programCode = programCode; }
    public String getPropertyUse() { return propertyUse; }
    public void setPropertyUse(String propertyUse) { this.propertyUse = propertyUse; }
    public String getZipPrefix() { return zipPrefix; }
    public void setZipPrefix(String zipPrefix) { this.zipPrefix = zipPrefix; }
    public BigDecimal getZipAdjustment() { return zipAdjustment; }
    public void setZipAdjustment(BigDecimal zipAdjustment) { this.zipAdjustment = zipAdjustment; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
