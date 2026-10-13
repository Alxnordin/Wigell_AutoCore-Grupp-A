package com.wac.autocore.service.interf;

import com.wac.autocore.model.Payment;

import java.util.List;

public interface PaymentService {

    List<Payment> findAll();

    Payment processPayment(int invoiceId, String paymentType);
}
