package com.jaycodesx.mortgage.quote.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrower_quote_profiles")
public class BorrowerQuoteProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long loanQuoteId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal annualIncome;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyDebts;

    @Column(nullable = false)
    private Integer creditScore;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal cashReserves;

    @Column(nullable = false)
    private Boolean firstTimeBuyer;

    @Column(nullable = false)
    private Boolean vaEligible;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public BigDecimal getMonthlyDebts() {
        return monthlyDebts;
    }

    public void setMonthlyDebts(BigDecimal monthlyDebts) {
        this.monthlyDebts = monthlyDebts;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public BigDecimal getCashReserves() {
        return cashReserves;
    }

    public void setCashReserves(BigDecimal cashReserves) {
        this.cashReserves = cashReserves;
    }

    public Boolean getFirstTimeBuyer() {
        return firstTimeBuyer;
    }

    public void setFirstTimeBuyer(Boolean firstTimeBuyer) {
        this.firstTimeBuyer = firstTimeBuyer;
    }

    public Boolean getVaEligible() {
        return vaEligible;
    }

    public void setVaEligible(Boolean vaEligible) {
        this.vaEligible = vaEligible;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
