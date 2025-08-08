package com.b2b.service;

import com.b2b.dto.request.BillingAccountCreateRequest;
import com.b2b.dto.response.BillingAccountResponse;
import com.b2b.entity.BillingAccount;
import com.b2b.entity.Store;
import com.b2b.exception.ResourceNotFoundException;
import com.b2b.repository.BillingAccountRepository;
import com.b2b.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillingAccountService {

    private final BillingAccountRepository billingAccountRepository;
    private final StoreRepository storeRepository;

    @Autowired
    public BillingAccountService(BillingAccountRepository billingAccountRepository, 
                                StoreRepository storeRepository) {
        this.billingAccountRepository = billingAccountRepository;
        this.storeRepository = storeRepository;
    }

    public BillingAccountResponse createBillingAccount(BillingAccountCreateRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + request.getStoreId()));

        BillingAccount account = new BillingAccount();
        account.setStore(store);
        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setCreditLimit(request.getCreditLimit());
        account.setCurrency(request.getCurrency());

        BillingAccount savedAccount = billingAccountRepository.save(account);
        return new BillingAccountResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public BillingAccountResponse getBillingAccountById(Long id) {
        BillingAccount account = billingAccountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Billing account not found with id: " + id));
        return new BillingAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public Page<BillingAccountResponse> getAllBillingAccounts(Pageable pageable) {
        return billingAccountRepository.findAll(pageable)
            .map(BillingAccountResponse::new);
    }

    @Transactional(readOnly = true)
    public List<BillingAccountResponse> getBillingAccountsByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        return billingAccountRepository.findByStore(store)
            .stream()
            .map(BillingAccountResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<BillingAccountResponse> getBillingAccountsByStore(Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        return billingAccountRepository.findByStore(store, pageable)
            .map(BillingAccountResponse::new);
    }

    @Transactional(readOnly = true)
    public List<BillingAccountResponse> getBillingAccountsByStoreAndStatus(Long storeId, BillingAccount.Status status) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        
        return billingAccountRepository.findByStoreAndStatus(store, status)
            .stream()
            .map(BillingAccountResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<BillingAccountResponse> getBillingAccountsByStatus(BillingAccount.Status status, Pageable pageable) {
        return billingAccountRepository.findByStatus(status, pageable)
            .map(BillingAccountResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<BillingAccountResponse> searchBillingAccounts(String search, Pageable pageable) {
        return billingAccountRepository.findBySearchTerm(search, pageable)
            .map(BillingAccountResponse::new);
    }

    public BillingAccountResponse updateBillingAccountStatus(Long id, BillingAccount.Status status) {
        BillingAccount account = billingAccountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Billing account not found with id: " + id));
        
        account.setStatus(status);
        BillingAccount updatedAccount = billingAccountRepository.save(account);
        return new BillingAccountResponse(updatedAccount);
    }

    public BillingAccountResponse updateCreditLimit(Long id, BigDecimal creditLimit) {
        BillingAccount account = billingAccountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Billing account not found with id: " + id));
        
        account.setCreditLimit(creditLimit);
        BillingAccount updatedAccount = billingAccountRepository.save(account);
        return new BillingAccountResponse(updatedAccount);
    }

    public BillingAccountResponse adjustBalance(Long id, BigDecimal amount) {
        BillingAccount account = billingAccountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Billing account not found with id: " + id));
        
        account.setBalance(account.getBalance().add(amount));
        BillingAccount updatedAccount = billingAccountRepository.save(account);
        return new BillingAccountResponse(updatedAccount);
    }

    @Transactional(readOnly = true)
    public List<BillingAccountResponse> getAccountsWithLowBalance(BigDecimal threshold) {
        return billingAccountRepository.findAccountsWithLowBalance(threshold)
            .stream()
            .map(BillingAccountResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BillingAccountResponse> getOverCreditLimitAccounts() {
        return billingAccountRepository.findOverCreditLimitAccounts()
            .stream()
            .map(BillingAccountResponse::new)
            .collect(Collectors.toList());
    }

    public void deleteBillingAccount(Long id) {
        if (!billingAccountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Billing account not found with id: " + id);
        }
        billingAccountRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long getBillingAccountCountByStore(Long storeId) {
        return billingAccountRepository.countByStoreId(storeId);
    }
}