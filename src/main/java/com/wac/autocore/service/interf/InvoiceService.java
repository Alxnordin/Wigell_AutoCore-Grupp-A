package com.wac.autocore.service.interf;

import com.wac.autocore.model.*;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {

    Invoice findInvoice(int id);

    List<Invoice> findAll();

    Invoice createInvoice(int workOrderId, String discountCode);

    Invoice updatePaidInvoice(Invoice invoice);

    //Alexander
    //kunden som fakturan gäller, används för att visa vem aviseringen skickades till
    Customer findCustomerForInvoice(Invoice invoice);

}
