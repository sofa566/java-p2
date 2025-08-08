package com.b2b.dto.response;

import com.b2b.entity.InvoiceItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceItemResponse {
    
    private Long id;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    public InvoiceItemResponse() {}

    public InvoiceItemResponse(InvoiceItem item) {
        this.id = item.getId();
        this.description = item.getDescription();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.totalPrice = item.getTotalPrice();
        this.createdAt = item.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}