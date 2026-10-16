package com.wac.autocore.service.interf;

import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Payment;

import java.util.List;

public interface PaymentService {

    List<Payment> findAll();

    Payment processPayment(int invoiceId, String paymentType);

    //Alexander
    //kunden som betalningen gäller, används för att visa vem bekräftelsen skickades till
    Customer findCustomerForPayment(Payment payment);
}
