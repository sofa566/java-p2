package com.b2b.dto.response;

import com.b2b.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceResponse {
    
    private Long id;
    private String invoiceNumber;
    private BillingAccountResponse billingAccount;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private Invoice.Status status;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private String description;
    private List<InvoiceItemResponse> invoiceItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InvoiceResponse() {}

    public InvoiceResponse(Invoice invoice) {
        this.id = invoice.getId();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.billingAccount = invoice.getBillingAccount() != null ? 
            new BillingAccountResponse(invoice.getBillingAccount()) : null;
        this.amount = invoice.getAmount();
        this.taxAmount = invoice.getTaxAmount();
        this.totalAmount = invoice.getTotalAmount();
        this.status = invoice.getStatus();
        this.issueDate = invoice.getIssueDate();
        this.dueDate = invoice.getDueDate();
        this.paidDate = invoice.getPaidDate();
        this.description = invoice.getDescription();
        this.invoiceItems = invoice.getInvoiceItems() != null ? 
            invoice.getInvoiceItems().stream()
                .map(InvoiceItemResponse::new)
                .collect(Collectors.toList()) : null;
        this.createdAt = invoice.getCreatedAt();
        this.updatedAt = invoice.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BillingAccountResponse getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccountResponse billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Invoice.Status getStatus() {
        return status;
    }

    public void setStatus(Invoice.Status status) {
        this.status = status;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InvoiceItemResponse> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItemResponse> invoiceItems) {
        this.invoiceItems = invoiceItems;
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