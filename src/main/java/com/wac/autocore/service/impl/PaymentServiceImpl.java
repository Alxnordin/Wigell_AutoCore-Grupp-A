package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.service.interf.InvoiceService;
import com.wac.autocore.service.interf.PaymentService;
import com.wac.autocore.repository.PaymentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    //private final PaymentDAO paymentDAO = new PaymentDAO();
    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;


    public PaymentServiceImpl(PaymentRepository paymentRepository, InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.invoiceService = invoiceService;
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }


    //Alexander
    //kontrollerar betalningstyp och faktura först och kastar exception vid fel
    //paymentType är en kod: CARD, SWISH eller CASH inte den översatta texten i rullistan
    @PreAuthorize("hasRole('ADMIN')")
    public Payment processPayment(int invoiceId, String paymentType) {
        if (paymentType == null || paymentType.trim().isEmpty()) {
            throw new ValidationException("missingPaymentTypeError");
        }
        if (!isKnownPaymentType(paymentType)) {
            throw new ValidationException("unknownPaymentTypeError", paymentType);
        }
        Invoice invoice = invoiceService.findInvoice(invoiceId);

        if (invoice == null) {
            throw new NotFoundException("invoiceNotFoundError", String.valueOf(invoiceId));
        }

        if (invoice.isPaid()) {
            throw new BusinessRuleException("invoiceAlreadyPaidError", String.valueOf(invoiceId));
        }

        Payment payment = new Payment(
                0,
                invoiceId,
                invoice.getTotalAmount(),
                paymentType);

        boolean successful = false;

        if (paymentType.equalsIgnoreCase("CARD")) {

            System.out.println("Connecting directly to SuperCardPayment...");
            System.out.println("Card payment approved.");
            successful = true;

        } else if (paymentType.equalsIgnoreCase("SWISH")) {

            System.out.println("Calling Swish payment service...");
            System.out.println("Swish payment approved.");
            successful = true;

        } else if (paymentType.equalsIgnoreCase("CASH")) {

            System.out.println("Registering cash payment...");
            successful = true;

        }

        payment.setSuccessful(successful);
        paymentRepository.save(payment);

        if (successful) {
            invoice.setPaid(true);
            invoiceService.updatePaidInvoice(invoice);

            System.out.println("Payment completed successfully.");
            System.out.println("Sending payment confirmation to customer...");
            System.out.println("Confirmation sent.");
        }
        return payment;
    }
    //Alexander
    //kunden hämtas via fakturan, ger null om fakturan eller kunde inte hittas
    @PreAuthorize("hasRole('ADMIN')")
    public Customer findCustomerForPayment(Payment payment) {
        Invoice invoice = invoiceService.findInvoice(payment.getInvoiceId());
        if (invoice == null) {
            return null;
        }
        return invoiceService.findCustomerForInvoice(invoice);
    }

    //Alexander
    //dom betalningstyper som systemet kan hantera
    private boolean isKnownPaymentType(String paymentType) {
        return paymentType.equalsIgnoreCase("CARD") || paymentType.equalsIgnoreCase("SWISH")
                || paymentType.equalsIgnoreCase("CASH");
    }

}
