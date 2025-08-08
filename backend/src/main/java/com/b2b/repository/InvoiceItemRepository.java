package com.b2b.repository;

import com.b2b.entity.Invoice;
import com.b2b.entity.InvoiceItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    
    List<InvoiceItem> findByInvoice(Invoice invoice);
    
    Page<InvoiceItem> findByInvoice(Invoice invoice, Pageable pageable);
    
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId")
    List<InvoiceItem> findByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    @Query("SELECT ii FROM InvoiceItem ii WHERE " +
           "LOWER(ii.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<InvoiceItem> findByDescriptionContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT SUM(ii.totalPrice) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId")
    BigDecimal sumTotalPriceByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    @Query("SELECT COUNT(ii) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId")
    long countByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    void deleteByInvoice(Invoice invoice);
}