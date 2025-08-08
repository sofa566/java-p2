package com.b2b.repository;

import com.b2b.entity.Invoice;
import com.b2b.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByInvoice(Invoice invoice);
    
    Page<Payment> findByInvoice(Invoice invoice, Pageable pageable);
    
    List<Payment> findByStatus(Payment.Status status);
    
    Page<Payment> findByStatus(Payment.Status status, Pageable pageable);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId")
    List<Payment> findByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    @Query("SELECT p FROM Payment p WHERE p.invoice.billingAccount.store.id = :storeId")
    List<Payment> findByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT p FROM Payment p WHERE p.invoice.billingAccount.store.id = :storeId")
    Page<Payment> findByStoreId(@Param("storeId") Long storeId, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND " +
           "p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByStatusAndPaymentDateBetween(@Param("status") Payment.Status status,
                                                    @Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Payment p WHERE " +
           "(LOWER(p.paymentReference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.notes) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Payment> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.invoice.id = :invoiceId AND p.status = 'COMPLETED'")
    BigDecimal sumCompletedPaymentsByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.invoice.billingAccount.id = :billingAccountId " +
           "AND p.status = 'COMPLETED'")
    BigDecimal sumCompletedPaymentsByBillingAccountId(@Param("billingAccountId") Long billingAccountId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.invoice.id = :invoiceId")
    long countByInvoiceId(@Param("invoiceId") Long invoiceId);
}