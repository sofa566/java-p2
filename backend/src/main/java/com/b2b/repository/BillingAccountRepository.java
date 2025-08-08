package com.b2b.repository;

import com.b2b.entity.BillingAccount;
import com.b2b.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BillingAccountRepository extends JpaRepository<BillingAccount, Long> {
    
    List<BillingAccount> findByStore(Store store);
    
    Page<BillingAccount> findByStore(Store store, Pageable pageable);
    
    List<BillingAccount> findByStoreAndStatus(Store store, BillingAccount.Status status);
    
    Page<BillingAccount> findByStatus(BillingAccount.Status status, Pageable pageable);
    
    List<BillingAccount> findByAccountType(BillingAccount.AccountType accountType);
    
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.store.id = :storeId")
    List<BillingAccount> findByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.store.id = :storeId AND ba.status = :status")
    List<BillingAccount> findByStoreIdAndStatus(@Param("storeId") Long storeId, 
                                                @Param("status") BillingAccount.Status status);
    
    @Query("SELECT ba FROM BillingAccount ba WHERE " +
           "(LOWER(ba.accountName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ba.store.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BillingAccount> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.balance < :threshold")
    List<BillingAccount> findAccountsWithLowBalance(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.accountType = 'CREDIT' AND " +
           "ba.balance > ba.creditLimit")
    List<BillingAccount> findOverCreditLimitAccounts();
    
    @Query("SELECT COUNT(ba) FROM BillingAccount ba WHERE ba.store.id = :storeId")
    long countByStoreId(@Param("storeId") Long storeId);
}