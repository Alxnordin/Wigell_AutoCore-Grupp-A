package com.wac.autocore.service;

import com.wac.autocore.dao.PaymentDAO;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.repository.PaymentRepository;

import java.util.List;

public class PaymentService {

    //private final PaymentDAO paymentDAO = new PaymentDAO();
    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;

    public PaymentService(InvoiceService invoiceService) {
        this(invoiceService, new PaymentDAO());
    }

    public PaymentService(InvoiceService invoiceService, PaymentRepository paymentRepository){
        this.invoiceService = invoiceService;
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment processPayment(int invoiceId, String paymentType) {
        Invoice invoice = invoiceService.findInvoice(invoiceId);

        if (invoice == null) {
            System.out.println("Invoice with ID " + invoiceId + " does not exist.");
            return null;
        }

        if (invoice.isPaid()) {
            System.out.println("Invoice has already been paid.");
            return null;
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

        } else {

            System.out.println("Unknown payment type.");
        }

        payment.setSuccessful(successful);
        paymentRepository.save(payment);

        if (successful) {
            invoice.setPaid(true);
            invoiceService.updatePaidInvoice(invoice);

            System.out.println("Payment completed successfully.");
            System.out.println("Sending payment confirmation to customer...");
            System.out.println("Confirmation sent.");
        } else {
            System.out.println("Payment failed.");
        }

        return payment;
    }

}
