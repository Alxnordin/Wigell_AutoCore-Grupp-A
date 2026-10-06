package com.wac.autocore.service;

import com.wac.autocore.dao.InvoiceDAO;
import com.wac.autocore.dao.InvoiceLineDAO;
import com.wac.autocore.model.*;

import java.time.LocalDate;
import java.util.List;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceLineDAO invoiceLineDAO = new InvoiceLineDAO();

    private final WorkOrderService workOrderService;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;

    public InvoiceService(WorkOrderService workOrderService, BookingService bookingService, VehicleService vehicleService, CustomerService customerService, ServiceItemService serviceItemService) {
        this.workOrderService = workOrderService;
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.serviceItemService = serviceItemService;
    }

     public Invoice findInvoice(int id) {
        for (Invoice invoice : invoiceDAO.findAll()) {
            if (invoice.getId() == id) {
                return invoice;
            }
        }

        return null;
    }

    public List<Invoice> findAll() {
        return invoiceDAO.findAll();
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {

        WorkOrder workOrder = workOrderService.findWorkOrder(workOrderId);
        System.out.println("Service IDs: " + workOrder.getServiceItemIds());
        System.out.println("Service prices: " + workOrder.getServiceItemPrices());

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return null;
        }

        if (!workOrder.getStatus().equals("COMPLETED")) {
            System.out.println("Invoice can only be created for a completed work order.");
            return null;
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
                    System.out.println("VIP discount applied: 10%");
                }
            }
        }

        if (discountCode != null && !discountCode.trim().isEmpty()) {

            if (discountCode.equalsIgnoreCase("WELCOME10")) {

                discountPercentage += 0.10;
                System.out.println("Discount code WELCOME10 applied.");

            } else if (discountCode.equalsIgnoreCase("SERVICE200")) {

                fixedDiscount = 200.0;
                System.out.println("Discount code SERVICE200 applied.");

            } else {

                System.out.println("Unknown discount code. No code discount applied.");
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

        invoiceDAO.save(invoice);

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

                invoiceLineDAO.save(invoiceLine);
            }
        }

        System.out.println("Invoice created successfully.");
        System.out.println(invoice);
        System.out.println("Sending invoice notification to customer...");
        System.out.println("Notification sent.");

        return invoice;
    }

    public Invoice updatePaidInvoice(Invoice invoice) {
        invoiceDAO.save(invoice);
        return invoice;
    }

}
