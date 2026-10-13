package com.wac.autocore.service.impl;

import com.wac.autocore.dao.InvoiceLineDAO;
import com.wac.autocore.model.InvoiceLine;
import com.wac.autocore.service.interf.InvoiceLineService;

import java.util.ArrayList;
import java.util.List;

public class InvoiceLineServiceImpl implements InvoiceLineService {

    private final InvoiceLineDAO invoiceLineDAO = new InvoiceLineDAO();

    public List<InvoiceLine> findInvoiceLinesByInvoiceId(int invoiceId) {

        List<InvoiceLine> invoiceLines = new ArrayList<>();

        for (InvoiceLine invoiceLine : invoiceLineDAO.findAll()) {
            if (invoiceLine.getInvoiceId() == invoiceId) {
                invoiceLines.add(invoiceLine);
            }
        }

        return invoiceLines;
    }


}
