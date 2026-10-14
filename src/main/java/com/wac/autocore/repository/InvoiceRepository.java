package com.wac.autocore.repository;

import com.wac.autocore.model.Invoice;

import java.util.List;

public interface InvoiceRepository {
    List<Invoice> findAll();

    Invoice save(Invoice invoice);

    void updatePaid(Invoice invoice);
}
