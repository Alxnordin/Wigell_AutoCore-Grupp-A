package com.wac.autocore.service.interf;

import com.wac.autocore.model.InvoiceLine;

import java.util.List;

public interface InvoiceLineService {

    List<InvoiceLine> findInvoiceLinesByInvoiceId(int invoiceId);
}


