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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);

    //private final PaymentDAO paymentDAO = new PaymentDAO();
    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;


    public PaymentServiceImpl(PaymentRepository paymentRepository, InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.invoiceService = invoiceService;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }


    //Alexander
    //kontrollerar betalningstyp och faktura först och kastar exception vid fel
    //paymentType är en kod: CARD, SWISH eller CASH inte den översatta texten i rullistan
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

            //simulerad betalleverantör, bara användbar vid utveckling
            LOG.debug("processPayment: simulerad kortbetalning godkänd för faktura {}", invoiceId);
            successful = true;

        } else if (paymentType.equalsIgnoreCase("SWISH")) {

            LOG.debug("processPayment: simulerad Swish-betalning godkänd för faktura {}", invoiceId);
            successful = true;

        } else if (paymentType.equalsIgnoreCase("CASH")) {

            LOG.debug("processPayment: kontantbetalning för faktura {}", invoiceId);
            successful = true;

        }

        payment.setSuccessful(successful);
        paymentRepository.save(payment);

        if (successful) {
            invoice.setPaid(true);
            invoiceService.updatePaidInvoice(invoice);

            LOG.info("processPayment: betalning {} registrerad för faktura {} ({})",
                    payment.getId(), invoiceId, paymentType);
        } else {
            LOG.warn("processPayment: betalning för faktura {} misslyckades ({})", invoiceId, paymentType);
        }
        return payment;
    }
    //Alexander
    //kunden hämtas via fakturan, ger null om fakturan eller kunde inte hittas

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
