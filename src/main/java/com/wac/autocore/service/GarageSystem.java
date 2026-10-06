package com.wac.autocore.service;

import com.wac.autocore.dao.*;
import com.wac.autocore.model.*;

import java.time.LocalDate;

public class GarageSystem {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final InvoiceLineDAO invoiceLineDAO = new InvoiceLineDAO();

    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;
    private final BookingService bookingService;
    private final WorkOrderService workOrderService;

    public GarageSystem(VehicleService vehicleService, CustomerService customerService,
                        ServiceItemService serviceItemService, BookingService bookingService, MechanicService mechanicService, WorkOrderService workOrderService) {
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.serviceItemService = serviceItemService;
        this.bookingService = bookingService;
        this.workOrderService = workOrderService;
    }

//    public void showCustomers() {
//        System.out.println();
//        System.out.println("=== CUSTOMERS ===");
//
//        if(customerDAO.findAll().isEmpty()){
//            System.out.println("No customers found.");
//            return;
//        }
//        for (Customer customer : customerDAO.findAll()){
//            System.out.println(customer);
//        }
//    }

//    public void showVehicles() {
//        System.out.println();
//        System.out.println("=== VEHICLES ===");
//
//
//        if(vehicleDAO.findAll().isEmpty()){
//            System.out.println("No vehicles found.");
//            return;
//        }
//        for (Vehicle vehicle : vehicleDAO.findAll()){
//            System.out.println(vehicle);
//        }
//    }

//    public void showBookings() {
//        System.out.println();
//        System.out.println("=== BOOKINGS ===");
//
//        if (bookingDAO.findAll().isEmpty()){
//            System.out.println("No bookings found.");
//            return;
//        }
//        for(Booking booking : bookingDAO.findAll()){
//            System.out.println(booking);
//        }
//    }

//    public void showServiceItems() {
//        System.out.println();
//        System.out.println("=== SERVICES ===");
//        //FREDRIK - ändrat
//        if (serviceItemDAO.findAll().isEmpty()) {
//            System.out.println("No services found.");
//            return;
//        }
//
//        for (ServiceItem serviceItem : serviceItemDAO.findAll()) {
//            System.out.println(serviceItem);
//        }
//
//    }
//
//    public void showMechanics() {
//        System.out.println();
//        System.out.println("=== MECHANICS ===");
//
//        if (mechanicDAO.findAll().isEmpty()) {
//            System.out.println("No mechanics found.");
//            return;
//        }
//
//        for (Mechanic mechanic : mechanicDAO.findAll()) {
//            System.out.println(mechanic);
//        }
//
//    }
//
//    public void showWorkOrders() {
//        System.out.println();
//        System.out.println("=== WORK ORDERS ===");
//
//        if (workOrderDAO.findAll().isEmpty()) {
//            System.out.println("No work orders found.");
//            return;
//        }
//
//        for (WorkOrder workOrder : workOrderDAO.findAll()) {
//            System.out.println(workOrder);
//        }
//
//    }
//
//    public void showInvoices() {
//        System.out.println();
//        System.out.println("=== INVOICES ===");
//
//        if (invoiceDAO.findAll().isEmpty()) {
//            System.out.println("No invoices found.");
//            return;
//        }
//
//        for (Invoice invoice : invoiceDAO.findAll()) {
//            System.out.println(invoice);
//        }
//
//    }
//
//    public void showPayments() {
//        System.out.println();
//        System.out.println("=== PAYMENTS ===");
//
//        if (paymentDAO.findAll().isEmpty()) {
//            System.out.println("No payments found.");
//            return;
//        }
//
//        for (Payment payment : paymentDAO.findAll()) {
//            System.out.println(payment);
//        }
//
//    }

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

    public Payment processPayment(int invoiceId, String paymentType) {
        Invoice invoice = findInvoice(invoiceId);

        if (invoice == null) {
            System.out.println("Invoice with ID " + invoiceId + " does not exist.");
            return null;
        }

        if (invoice.isPaid()) {
            System.out.println("Invoice has already been paid.");
            return null;
        }

        Payment payment = new Payment(
                0,
                invoiceId,
                invoice.getTotalAmount(),
                paymentType);

        boolean successful = false;

        if (paymentType.equalsIgnoreCase("CARD")) {

            System.out.println("Connecting directly to SuperCardPayment...");
            System.out.println("Card payment approved.");
            successful = true;

        } else if (paymentType.equalsIgnoreCase("SWISH")) {

            System.out.println("Calling Swish payment service...");
            System.out.println("Swish payment approved.");
            successful = true;

        } else if (paymentType.equalsIgnoreCase("CASH")) {

            System.out.println("Registering cash payment...");
            successful = true;

        } else {

            System.out.println("Unknown payment type.");
        }

        payment.setSuccessful(successful);
        paymentDAO.save(payment);

        if (successful) {
            invoice.setPaid(true);
            invoiceDAO.updatePaid(invoice);

            System.out.println("Payment completed successfully.");
            System.out.println("Sending payment confirmation to customer...");
            System.out.println("Confirmation sent.");
        } else {
            System.out.println("Payment failed.");
        }

        return payment;
    }

    public Invoice findInvoice(int id) {
        for (Invoice invoice : invoiceDAO.findAll()) {
            if (invoice.getId() == id) {
                return invoice;
            }
        }

        return null;
    }

}