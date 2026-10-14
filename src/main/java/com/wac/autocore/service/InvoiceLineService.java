package com.wac.autocore.service;

import com.wac.autocore.dao.InvoiceLineDAO;
import com.wac.autocore.model.InvoiceLine;
import com.wac.autocore.repository.InvoiceLineRepository;

import java.util.ArrayList;
import java.util.List;

public class InvoiceLineService {

    //private final InvoiceLineDAO invoiceLineDAO = new InvoiceLineDAO();
     private final InvoiceLineRepository invoiceLineRepository;

     public InvoiceLineService(){
         this(new InvoiceLineDAO());
     }

     public InvoiceLineService(InvoiceLineRepository invoiceLineRepository){
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
