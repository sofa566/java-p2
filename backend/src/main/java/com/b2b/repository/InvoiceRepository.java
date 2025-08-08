package com.b2b.repository;

import com.b2b.entity.BillingAccount;
import com.b2b.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByBillingAccount(BillingAccount billingAccount);
    
    Page<Invoice> findByBillingAccount(BillingAccount billingAccount, Pageable pageable);
    
    List<Invoice> findByStatus(Invoice.Status status);
    
    Page<Invoice> findByStatus(Invoice.Status status, Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE i.billingAccount.id = :billingAccountId")
    List<Invoice> findByBillingAccountId(@Param("billingAccountId") Long billingAccountId);
    
    @Query("SELECT i FROM Invoice i WHERE i.billingAccount.store.id = :storeId")
    List<Invoice> findByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT i FROM Invoice i WHERE i.billingAccount.store.id = :storeId")
    Page<Invoice> findByStoreId(@Param("storeId") Long storeId, Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < :date")
    List<Invoice> findOverdueInvoices(@Param("status") Invoice.Status status, 
                                      @Param("date") LocalDate date);
    
    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesDueBetween(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesIssuedBetween(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i FROM Invoice i WHERE " +
           "(LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.billingAccount.accountName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Invoice> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.billingAccount.id = :billingAccountId AND i.status = :status")
    BigDecimal sumTotalAmountByBillingAccountIdAndStatus(@Param("billingAccountId") Long billingAccountId, 
                                                         @Param("status") Invoice.Status status);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.billingAccount.id = :billingAccountId")
    long countByBillingAccountId(@Param("billingAccountId") Long billingAccountId);
}