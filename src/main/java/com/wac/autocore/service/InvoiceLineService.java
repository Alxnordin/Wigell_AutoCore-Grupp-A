package com.wac.autocore.service;

import com.wac.autocore.dao.InvoiceLineDAO;
import com.wac.autocore.model.InvoiceLine;

import java.util.ArrayList;
import java.util.List;

public class InvoiceLineService {

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
