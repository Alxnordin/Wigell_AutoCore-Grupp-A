package com.wac.autocore.service.impl;

import com.wac.autocore.model.InvoiceLine;
import com.wac.autocore.service.interf.InvoiceLineService;
import com.wac.autocore.repository.InvoiceLineRepository;
import java.util.ArrayList;
import java.util.List;

public class InvoiceLineServiceImpl implements InvoiceLineService {

     private final InvoiceLineRepository invoiceLineRepository;

    public InvoiceLineServiceImpl(InvoiceLineRepository invoiceLineRepository) {
        this.invoiceLineRepository = invoiceLineRepository;
    }

    public List<InvoiceLine> findInvoiceLinesByInvoiceId(int invoiceId) {

        List<InvoiceLine> invoiceLines = new ArrayList<>();

        for (InvoiceLine invoiceLine : invoiceLineRepository.findAll()) {
            if (invoiceLine.getInvoiceId() == invoiceId) {
                invoiceLines.add(invoiceLine);
            }
        }

        return invoiceLines;
    }

}
