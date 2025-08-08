package com.b2b.dto.request;

import com.b2b.entity.BillingAccount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BillingAccountCreateRequest {
    
    @NotNull(message = "Store ID is required")
    private Long storeId;
    
    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    private String accountName;
    
    @NotNull(message = "Account type is required")
    private BillingAccount.AccountType accountType;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance must be non-negative")
    private BigDecimal initialBalance = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit must be non-negative")
    private BigDecimal creditLimit = BigDecimal.ZERO;
    
    @Size(min = 3, max = 3, message = "Currency must be a 3-character code")
    private String currency = "USD";

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
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
}