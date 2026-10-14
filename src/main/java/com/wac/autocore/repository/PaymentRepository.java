package com.wac.autocore.repository;

import com.wac.autocore.model.Payment;

import java.util.List;

public interface PaymentRepository {
    List<Payment> findAll();

    Payment save(Payment payment);
}
