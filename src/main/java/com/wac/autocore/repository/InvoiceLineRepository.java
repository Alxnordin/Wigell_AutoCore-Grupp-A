package com.wac.autocore.repository;

import com.wac.autocore.model.InvoiceLine;

import java.util.List;

public interface InvoiceLineRepository {

    List<InvoiceLine> findAll();

    InvoiceLine save(InvoiceLine invoiceLine);
}
