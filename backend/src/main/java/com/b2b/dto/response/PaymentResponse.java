package com.b2b.dto.response;

import com.b2b.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentResponse {
    
    private Long id;
    private InvoiceResponse invoice;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
    private String paymentReference;
    private LocalDate paymentDate;
    private Payment.Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentResponse() {}

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.invoice = payment.getInvoice() != null ? new InvoiceResponse(payment.getInvoice()) : null;
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentReference = payment.getPaymentReference();
        this.paymentDate = payment.getPaymentDate();
        this.status = payment.getStatus();
        this.notes = payment.getNotes();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvoiceResponse getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceResponse invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Payment.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Payment.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Payment.Status getStatus() {
        return status;
    }

    public void setStatus(Payment.Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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