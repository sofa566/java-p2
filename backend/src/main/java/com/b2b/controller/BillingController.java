package com.b2b.controller;

import com.b2b.dto.request.BillingAccountCreateRequest;
import com.b2b.dto.request.InvoiceCreateRequest;
import com.b2b.dto.request.PaymentCreateRequest;
import com.b2b.dto.response.*;
import com.b2b.entity.BillingAccount;
import com.b2b.entity.Invoice;
import com.b2b.entity.Payment;
import com.b2b.service.BillingAccountService;
import com.b2b.service.InvoiceService;
import com.b2b.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BillingController {

    private final BillingAccountService billingAccountService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    @Autowired
    public BillingController(BillingAccountService billingAccountService,
                           InvoiceService invoiceService,
                           PaymentService paymentService) {
        this.billingAccountService = billingAccountService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
    }

    // Billing Account Endpoints
    @PostMapping("/accounts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<BillingAccountResponse>> createBillingAccount(
            @Valid @RequestBody BillingAccountCreateRequest createRequest) {
        BillingAccountResponse account = billingAccountService.createBillingAccount(createRequest);
        return ResponseEntity.ok(ApiResponse.success("Billing account created successfully", account));
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<Page<BillingAccountResponse>>> getAllBillingAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BillingAccount.Status status) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BillingAccountResponse> accounts;
        if (search != null && !search.trim().isEmpty()) {
            accounts = billingAccountService.searchBillingAccounts(search, pageable);
        } else if (status != null) {
            accounts = billingAccountService.getBillingAccountsByStatus(status, pageable);
        } else {
            accounts = billingAccountService.getAllBillingAccounts(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @GetMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<BillingAccountResponse>> getBillingAccountById(@PathVariable Long id) {
        BillingAccountResponse account = billingAccountService.getBillingAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @GetMapping("/accounts/store/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<BillingAccountResponse>>> getBillingAccountsByStore(@PathVariable Long storeId) {
        List<BillingAccountResponse> accounts = billingAccountService.getBillingAccountsByStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @PatchMapping("/accounts/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillingAccountResponse>> updateBillingAccountStatus(
            @PathVariable Long id,
            @RequestParam BillingAccount.Status status) {
        BillingAccountResponse account = billingAccountService.updateBillingAccountStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Account status updated successfully", account));
    }

    @PatchMapping("/accounts/{id}/credit-limit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillingAccountResponse>> updateCreditLimit(
            @PathVariable Long id,
            @RequestParam BigDecimal creditLimit) {
        BillingAccountResponse account = billingAccountService.updateCreditLimit(id, creditLimit);
        return ResponseEntity.ok(ApiResponse.success("Credit limit updated successfully", account));
    }

    @PatchMapping("/accounts/{id}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillingAccountResponse>> adjustBalance(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        BillingAccountResponse account = billingAccountService.adjustBalance(id, amount);
        return ResponseEntity.ok(ApiResponse.success("Balance adjusted successfully", account));
    }

    // Invoice Endpoints
    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(@Valid @RequestBody InvoiceCreateRequest createRequest) {
        InvoiceResponse invoice = invoiceService.createInvoice(createRequest);
        return ResponseEntity.ok(ApiResponse.success("Invoice created successfully", invoice));
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Invoice.Status status) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InvoiceResponse> invoices;
        if (search != null && !search.trim().isEmpty()) {
            invoices = invoiceService.searchInvoices(search, pageable);
        } else if (status != null) {
            invoices = invoiceService.getInvoicesByStatus(status, pageable);
        } else {
            invoices = invoiceService.getAllInvoices(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @GetMapping("/invoices/number/{invoiceNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceResponse invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @GetMapping("/invoices/store/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getInvoicesByStore(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceResponse> invoices = invoiceService.getInvoicesByStore(storeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/invoices/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getOverdueInvoices() {
        List<InvoiceResponse> invoices = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/invoices/due-between")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getInvoicesDueBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<InvoiceResponse> invoices = invoiceService.getInvoicesDueBetween(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @PatchMapping("/invoices/{id}/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> sendInvoice(@PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.sendInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice sent successfully", invoice));
    }

    @PatchMapping("/invoices/{id}/paid")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markInvoiceAsPaid(@PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice marked as paid", invoice));
    }

    @PatchMapping("/invoices/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> cancelInvoice(@PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice cancelled successfully", invoice));
    }

    @DeleteMapping("/invoices/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice deleted successfully"));
    }

    // Payment Endpoints
    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentCreateRequest createRequest) {
        PaymentResponse payment = paymentService.createPayment(createRequest);
        return ResponseEntity.ok(ApiResponse.success("Payment created successfully", payment));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Payment.Status status) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentResponse> payments;
        if (search != null && !search.trim().isEmpty()) {
            payments = paymentService.searchPayments(search, pageable);
        } else if (status != null) {
            payments = paymentService.getPaymentsByStatus(status, pageable);
        } else {
            payments = paymentService.getAllPayments(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/payments/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByInvoice(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/payments/store/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentsByStore(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentResponse> payments = paymentService.getPaymentsByStore(storeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/payments/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Payment.Status status) {
        
        List<PaymentResponse> payments;
        if (status != null) {
            payments = paymentService.getPaymentsByStatusAndDateRange(status, startDate, endDate);
        } else {
            payments = paymentService.getPaymentsBetweenDates(startDate, endDate);
        }
        
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PatchMapping("/payments/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> completePayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.completePayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment completed successfully", payment));
    }

    @PatchMapping("/payments/{id}/fail")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> failPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.failPayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment marked as failed", payment));
    }

    @DeleteMapping("/payments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully"));
    }
}