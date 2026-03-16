package com.jaycodesx.mortgage.lead.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "mortgage_leads")
public class MortgageLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long loanQuoteId;

    @Column(nullable = false)
    private Long borrowerQuoteProfileId;

    @Column(nullable = false)
    private String leadStatus;

    @Column(nullable = false)
    private String leadSource;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getLoanQuoteId() {
        return loanQuoteId;
    }

    public void setLoanQuoteId(Long loanQuoteId) {
        this.loanQuoteId = loanQuoteId;
    }

    public Long getBorrowerQuoteProfileId() {
        return borrowerQuoteProfileId;
    }

    public void setBorrowerQuoteProfileId(Long borrowerQuoteProfileId) {
        this.borrowerQuoteProfileId = borrowerQuoteProfileId;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
