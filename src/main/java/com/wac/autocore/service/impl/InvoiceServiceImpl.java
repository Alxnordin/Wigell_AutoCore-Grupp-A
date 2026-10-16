package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.model.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.repository.InvoiceLineRepository;
import com.wac.autocore.repository.InvoiceRepository;

import java.time.LocalDate;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {

    //private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceRepository invoiceRepository;
    //private final InvoiceLineDAO invoiceLineDAO = new InvoiceLineDAO();
    private final InvoiceLineRepository invoiceLineRepository;

    private final WorkOrderService workOrderService;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceLineRepository invoiceLineRepository,
                              WorkOrderService workOrderService, BookingService bookingService,
                              VehicleService vehicleService, CustomerService customerService,
                              ServiceItemService serviceItemService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.workOrderService = workOrderService;
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.serviceItemService = serviceItemService;
    }

    public Invoice findInvoice(int id) {
        for (Invoice invoice : invoiceRepository.findAll()) {
            if (invoice.getId() == id) {
                return invoice;
            }
        }

        return null;
    }

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }


    //Alexander
    //kontrollerar arbetsordern & rabattkoden först och kastar exception vi fel
    public Invoice createInvoice(int workOrderId, String discountCode) {

        WorkOrder workOrder = workOrderService.findWorkOrder(workOrderId);

        if (workOrder == null) {
          throw new NotFoundException("workOrderNotFoundError",String.valueOf(workOrderId));
        }

        if (!"COMPLETED".equals(workOrder.getStatus())){
            throw new BusinessRuleException("invoiceRequiresCompletedOrderError");
        }

        //en arbetsorder ska bara faktureras en gång
        for (Invoice existing : invoiceRepository.findAll()) {
            if (existing.getWorkOrderId() == workOrderId) {
                throw new BusinessRuleException("invoiceAlreadyExistsError", String.valueOf(workOrderId));
            }
        }

        double amount = 0.0;

        for (Integer serviceItemId : workOrder.getServiceItemIds()) {

            Double price = workOrder.getServiceItemPrices().get(serviceItemId);

            if (price != null) {
                amount += price;
            }
        }

        double discountPercentage = 0.0;
        double fixedDiscount = 0.0;

        Booking booking = bookingService.findBooking(workOrder.getBookingId());

        if (booking != null) {

            Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());

            if (vehicle != null) {

                Customer customer = customerService.findCustomer(vehicle.getCustomerId());

                if (customer != null && customer.isVip()) {
                    discountPercentage += 0.10;
                }
            }
        }

        if (discountCode != null && !discountCode.trim().isEmpty()) {

            if (discountCode.equalsIgnoreCase("WELCOME10")) {

                discountPercentage += 0.10;

            } else if (discountCode.equalsIgnoreCase("SERVICE200")) {

                fixedDiscount = 200.0;

            } else {
                //Alexander  okänd rabattkod ger ett fel
                throw new ValidationException("unknownDiscountCodeError", discountCode.trim());
            }
        }

        double totalDiscount = 0.0;

        for (Integer serviceItemId : workOrder.getServiceItemIds()) {

            Double price = workOrder.getServiceItemPrices().get(serviceItemId);

            if (price != null) {

                double lineDiscount = price * discountPercentage;

                if (fixedDiscount > 0 && amount > 0) {

                    double partOfTotal = price / amount;

                    lineDiscount += fixedDiscount * partOfTotal;
                }

                if (lineDiscount > price) {
                    lineDiscount = price;
                }

                totalDiscount += lineDiscount;
            }
        }

        Invoice invoice = new Invoice(
                0,
                workOrderId,
                LocalDate.now(),
                amount
        );

        invoice.setDiscount(totalDiscount);

        invoiceRepository.save(invoice);

        for (Integer serviceItemId : workOrder.getServiceItemIds()) {

            Double price = workOrder.getServiceItemPrices().get(serviceItemId);
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);

            if (price != null && serviceItem != null) {

                double lineDiscount = price * discountPercentage;

                if (fixedDiscount > 0 && amount > 0) {

                    double partOfTotal = price / amount;

                    lineDiscount += fixedDiscount * partOfTotal;
                }

                if (lineDiscount > price) {
                    lineDiscount = price;
                }

                double finalPrice = price - lineDiscount;

                InvoiceLine invoiceLine = new InvoiceLine(
                        0,
                        invoice.getId(),
                        serviceItem.getName(),
                        price,
                        lineDiscount,
                        finalPrice
                );

                invoiceLineRepository.save(invoiceLine);
            }
        }

        System.out.println("Sending invoice notification to customer...");
        System.out.println("Notification sent.");

        return invoice;
    }


    //Alexander
    //uppdaterar betald-status på den befintliga fakturan (UPDATE).
    //tidigare anropades save (INSERT), så varje betalning skapade en ny kopia av fakturan
    //medan originalet förblev obetalt
    public Invoice updatePaidInvoice(Invoice invoice) {
        invoiceRepository.updatePaid(invoice);
        return invoice;
    }

}
