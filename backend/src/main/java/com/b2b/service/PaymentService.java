package com.b2b.service;

import com.b2b.dto.request.PaymentCreateRequest;
import com.b2b.dto.response.PaymentResponse;
import com.b2b.entity.Invoice;
import com.b2b.entity.Payment;
import com.b2b.exception.ResourceNotFoundException;
import com.b2b.repository.InvoiceRepository;
import com.b2b.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public PaymentResponse createPayment(PaymentCreateRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        // Validate payment amount doesn't exceed remaining balance
        BigDecimal totalPaid = getCompletedPaymentsByInvoice(request.getInvoiceId());
        BigDecimal remainingBalance = invoice.getTotalAmount().subtract(totalPaid);
        
        if (request.getAmount().compareTo(remainingBalance) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds remaining invoice balance");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentReference(request.getPaymentReference());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setNotes(request.getNotes());

        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status if fully paid
        updateInvoiceStatusIfFullyPaid(invoice.getId());
        
        return new PaymentResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return new PaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
            .map(PaymentResponse::new);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
        
        return paymentRepository.findByInvoice(invoice)
            .stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByInvoice(Long invoiceId, Pageable pageable) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
        
        return paymentRepository.findByInvoice(invoice, pageable)
            .map(PaymentResponse::new);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStore(Long storeId) {
        return paymentRepository.findByStoreId(storeId)
            .stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByStore(Long storeId, Pageable pageable) {
        return paymentRepository.findByStoreId(storeId, pageable)
            .map(PaymentResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByStatus(Payment.Status status, Pageable pageable) {
        return paymentRepository.findByStatus(status, pageable)
            .map(PaymentResponse::new);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByPaymentMethod(Payment.PaymentMethod paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod)
            .stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findPaymentsBetweenDates(startDate, endDate)
            .stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatusAndDateRange(Payment.Status status, 
                                                                LocalDate startDate, 
                                                                LocalDate endDate) {
        return paymentRepository.findByStatusAndPaymentDateBetween(status, startDate, endDate)
            .stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> searchPayments(String search, Pageable pageable) {
        return paymentRepository.findBySearchTerm(search, pageable)
            .map(PaymentResponse::new);
    }

    public PaymentResponse updatePaymentStatus(Long id, Payment.Status status) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Update invoice status based on payment completion
        if (status == Payment.Status.COMPLETED) {
            updateInvoiceStatusIfFullyPaid(payment.getInvoice().getId());
        }
        
        return new PaymentResponse(updatedPayment);
    }

    public PaymentResponse completePayment(Long id) {
        return updatePaymentStatus(id, Payment.Status.COMPLETED);
    }

    public PaymentResponse failPayment(Long id) {
        return updatePaymentStatus(id, Payment.Status.FAILED);
    }

    public PaymentResponse refundPayment(Long id) {
        return updatePaymentStatus(id, Payment.Status.REFUNDED);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        
        if (payment.getStatus() == Payment.Status.COMPLETED) {
            throw new IllegalStateException("Cannot delete completed payment");
        }
        
        paymentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCompletedPaymentsByInvoice(Long invoiceId) {
        BigDecimal total = paymentRepository.sumCompletedPaymentsByInvoiceId(invoiceId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getCompletedPaymentsByBillingAccount(Long billingAccountId) {
        BigDecimal total = paymentRepository.sumCompletedPaymentsByBillingAccountId(billingAccountId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public long getPaymentCountByInvoice(Long invoiceId) {
        return paymentRepository.countByInvoiceId(invoiceId);
    }

    private void updateInvoiceStatusIfFullyPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
        
        BigDecimal totalPaid = getCompletedPaymentsByInvoice(invoiceId);
        
        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0 && 
            invoice.getStatus() != Invoice.Status.PAID) {
            invoice.setStatus(Invoice.Status.PAID);
            invoice.setPaidDate(LocalDate.now());
            invoiceRepository.save(invoice);
        }
    }
}