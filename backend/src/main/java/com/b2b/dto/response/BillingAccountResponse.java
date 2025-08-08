package com.b2b.dto.response;

import com.b2b.entity.BillingAccount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillingAccountResponse {
    
    private Long id;
    private StoreResponse store;
    private String accountName;
    private BillingAccount.AccountType accountType;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private String currency;
    private BillingAccount.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillingAccountResponse() {}

    public BillingAccountResponse(BillingAccount account) {
        this.id = account.getId();
        this.store = account.getStore() != null ? new StoreResponse(account.getStore()) : null;
        this.accountName = account.getAccountName();
        this.accountType = account.getAccountType();
        this.balance = account.getBalance();
        this.creditLimit = account.getCreditLimit();
        this.currency = account.getCurrency();
        this.status = account.getStatus();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoreResponse getStore() {
        return store;
    }

    public void setStore(StoreResponse store) {
        this.store = store;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BillingAccount.AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(BillingAccount.AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BillingAccount.Status getStatus() {
        return status;
    }

    public void setStatus(BillingAccount.Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}