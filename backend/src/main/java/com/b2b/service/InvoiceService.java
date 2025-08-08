package com.b2b.service;

import com.b2b.dto.request.InvoiceCreateRequest;
import com.b2b.dto.response.InvoiceResponse;
import com.b2b.entity.BillingAccount;
import com.b2b.entity.Invoice;
import com.b2b.entity.InvoiceItem;
import com.b2b.exception.ResourceNotFoundException;
import com.b2b.repository.BillingAccountRepository;
import com.b2b.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BillingAccountRepository billingAccountRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, 
                         BillingAccountRepository billingAccountRepository) {
        this.invoiceRepository = invoiceRepository;
        this.billingAccountRepository = billingAccountRepository;
    }

    public InvoiceResponse createInvoice(InvoiceCreateRequest request) {
        BillingAccount billingAccount = billingAccountRepository.findById(request.getBillingAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Billing account not found with id: " + request.getBillingAccountId()));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setBillingAccount(billingAccount);
        invoice.setAmount(request.getAmount());
        invoice.setTaxAmount(request.getTaxAmount());
        invoice.setTotalAmount(request.getAmount().add(request.getTaxAmount()));
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(request.getDueDate());
        invoice.setDescription(request.getDescription());

        // Add invoice items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<InvoiceItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    InvoiceItem item = new InvoiceItem();
                    item.setInvoice(invoice);
                    item.setDescription(itemRequest.getDescription());
                    item.setQuantity(itemRequest.getQuantity());
                    item.setUnitPrice(itemRequest.getUnitPrice());
                    item.setTotalPrice(itemRequest.getQuantity().multiply(itemRequest.getUnitPrice()));
                    return item;
                })
                .collect(Collectors.toList());
            invoice.setInvoiceItems(items);
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return new InvoiceResponse(savedInvoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return new InvoiceResponse(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with number: " + invoiceNumber));
        return new InvoiceResponse(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
            .map(InvoiceResponse::new);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByBillingAccount(Long billingAccountId) {
        BillingAccount billingAccount = billingAccountRepository.findById(billingAccountId)
            .orElseThrow(() -> new ResourceNotFoundException("Billing account not found with id: " + billingAccountId));
        
        return invoiceRepository.findByBillingAccount(billingAccount)
            .stream()
            .map(InvoiceResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesByBillingAccount(Long billingAccountId, Pageable pageable) {
        BillingAccount billingAccount = billingAccountRepository.findById(billingAccountId)
            .orElseThrow(() -> new ResourceNotFoundException("Billing account not found with id: " + billingAccountId));
        
        return invoiceRepository.findByBillingAccount(billingAccount, pageable)
            .map(InvoiceResponse::new);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByStore(Long storeId) {
        return invoiceRepository.findByStoreId(storeId)
            .stream()
            .map(InvoiceResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesByStore(Long storeId, Pageable pageable) {
        return invoiceRepository.findByStoreId(storeId, pageable)
            .map(InvoiceResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesByStatus(Invoice.Status status, Pageable pageable) {
        return invoiceRepository.findByStatus(status, pageable)
            .map(InvoiceResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> searchInvoices(String search, Pageable pageable) {
        return invoiceRepository.findBySearchTerm(search, pageable)
            .map(InvoiceResponse::new);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(Invoice.Status.SENT, LocalDate.now())
            .stream()
            .map(InvoiceResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesDueBetween(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findInvoicesDueBetween(startDate, endDate)
            .stream()
            .map(InvoiceResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesIssuedBetween(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findInvoicesIssuedBetween(startDate, endDate)
            .stream()
            .map(InvoiceResponse::new)
            .collect(Collectors.toList());
    }

    public InvoiceResponse updateInvoiceStatus(Long id, Invoice.Status status) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        invoice.setStatus(status);
        
        if (status == Invoice.Status.PAID) {
            invoice.setPaidDate(LocalDate.now());
        }
        
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return new InvoiceResponse(updatedInvoice);
    }

    public InvoiceResponse sendInvoice(Long id) {
        return updateInvoiceStatus(id, Invoice.Status.SENT);
    }

    public InvoiceResponse markInvoiceAsPaid(Long id) {
        return updateInvoiceStatus(id, Invoice.Status.PAID);
    }

    public InvoiceResponse cancelInvoice(Long id) {
        return updateInvoiceStatus(id, Invoice.Status.CANCELLED);
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        if (invoice.getStatus() != Invoice.Status.DRAFT) {
            throw new IllegalStateException("Only draft invoices can be deleted");
        }
        
        invoiceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByBillingAccountAndStatus(Long billingAccountId, Invoice.Status status) {
        BigDecimal total = invoiceRepository.sumTotalAmountByBillingAccountIdAndStatus(billingAccountId, status);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public long getInvoiceCountByBillingAccount(Long billingAccountId) {
        return invoiceRepository.countByBillingAccountId(billingAccountId);
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + "-" + timestamp + "-" + randomSuffix;
    }
}