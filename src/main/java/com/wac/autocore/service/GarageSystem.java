package com.wac.autocore.service;

import com.wac.autocore.dao.*;
import com.wac.autocore.model.*;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

public class GarageSystem {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceItemDAO serviceItemDAO = new ServiceItemDAO();
    private final MechanicDAO mechanicDAO = new MechanicDAO();
    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final InvoiceLineDAO invoiceLineDAO = new InvoiceLineDAO();

    public void showCustomers() {
        System.out.println();
        System.out.println("=== CUSTOMERS ===");

        if(customerDAO.findAll().isEmpty()){
            System.out.println("No customers found.");
            return;
        }
        for (Customer customer : customerDAO.findAll()){
            System.out.println(customer);
        }



    }

    public void showVehicles() {
        System.out.println();
        System.out.println("=== VEHICLES ===");


        if(vehicleDAO.findAll().isEmpty()){
            System.out.println("No vehicles found.");
            return;
        }
        for (Vehicle vehicle : vehicleDAO.findAll()){
            System.out.println(vehicle);
        }


    }

    public void showBookings() {
        System.out.println();
        System.out.println("=== BOOKINGS ===");

        if (bookingDAO.findAll().isEmpty()){
            System.out.println("No bookings found.");
            return;
        }
        for(Booking booking : bookingDAO.findAll()){
            System.out.println(booking);
        }


    }

    public void showServiceItems() {
        System.out.println();
        System.out.println("=== SERVICES ===");
        //FREDRIK - ändrat
        if (serviceItemDAO.findAll().isEmpty()) {
            System.out.println("No services found.");
            return;
        }

        for (ServiceItem serviceItem : serviceItemDAO.findAll()) {
            System.out.println(serviceItem);
        }

    }

    public void showMechanics() {
        System.out.println();
        System.out.println("=== MECHANICS ===");

        if (mechanicDAO.findAll().isEmpty()) {
            System.out.println("No mechanics found.");
            return;
        }

        for (Mechanic mechanic : mechanicDAO.findAll()) {
            System.out.println(mechanic);
        }

    }

    public void showWorkOrders() {
        System.out.println();
        System.out.println("=== WORK ORDERS ===");

        if (workOrderDAO.findAll().isEmpty()) {
            System.out.println("No work orders found.");
            return;
        }

        for (WorkOrder workOrder : workOrderDAO.findAll()) {
            System.out.println(workOrder);
        }

    }

    public void showInvoices() {
        System.out.println();
        System.out.println("=== INVOICES ===");

        if (invoiceDAO.findAll().isEmpty()) {
            System.out.println("No invoices found.");
            return;
        }

        for (Invoice invoice : invoiceDAO.findAll()) {
            System.out.println(invoice);
        }

    }

    public void showPayments() {
        System.out.println();
        System.out.println("=== PAYMENTS ===");

        if (paymentDAO.findAll().isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        for (Payment payment : paymentDAO.findAll()) {
            System.out.println(payment);
        }

    }

    public Customer createCustomer(String name, String phone, String email) {

        Customer customer = new Customer(0, name, phone, email);
        customerDAO.save(customer);

        System.out.println("Customer created successfully.");
        System.out.println(customer);

        return customer;
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {

        Customer customer = findCustomer(customerId);

        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
        }
        Vehicle vehicle = new Vehicle(0, registrationNumber, brand, model, year, customerId);
        vehicleDAO.save(vehicle);

        System.out.println("Vehicle created successfully.");
        System.out.println(vehicle);

        return vehicle;
    }

    //Alexander Lägger till:
    //bokning skapas med de tjänster som valts
    public Booking createBooking(int vehicleId,
                                 LocalDate date,
                                 String description,
                                 int... serviceItemIds) {

        Vehicle vehicle = findVehicle(vehicleId);

        if (vehicle == null) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
        }

        //Alexander
        //Valda tjänster måste finnas
        for (int serviceItemId : serviceItemIds) {
            if (findServiceItem(serviceItemId) == null) {
                System.out.println("Service item with ID " + serviceItemId + " does not exist.");
                return null;
            }
        }
        //Alexander - ändrat
        Booking booking = new Booking(
                0,
                vehicleId,
                date,
                description);
        for (int serviceItemId : serviceItemIds) {
            if (!booking.containsServiceItem(serviceItemId)) {
                booking.addServiceItem(serviceItemId);
            }
        }
        bookingDAO.save(booking); //sparar bokning + tjänster i en transaktion (commit/rollback);

        System.out.println("Booking created successfully.");
        System.out.println(booking);

        return booking;

    }

    //Alexander
    //arbetet räknas som påbörjat när arbetsordern har startats
    // startWorkOrder/completeWorkOrder sparar då bokningens status som IN_PROGRESS/COMPLETED
    public boolean isWorkStarted(Booking booking) {
        return "IN_PROGRESS".equals(booking.getStatus())
                || "COMPLETED".equals(booking.getStatus());
    }

    //Alexander
    //lägg till en tjänst i en bokning, bara innan arbetet har påbörjats
    public boolean addServiceToBooking(int bookingId, int serviceItemId) {
        Booking booking = findBooking(bookingId);   // hämtas färskt från databasen

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return false;
        }

        if (isWorkStarted(booking)) {
            System.out.println("Work on booking " + bookingId + " has started. Services can no longer be changed.");
            return false;
        }

        if (findServiceItem(serviceItemId) == null) {
            System.out.println("Service item with ID " + serviceItemId + " does not exist.");
            return false;
        }

        if (booking.containsServiceItem(serviceItemId)) {
            System.out.println("Service item " + serviceItemId + " is already in booking " + bookingId + ".");
            return false;
        }

        bookingDAO.addServiceItem(bookingId, serviceItemId);
        System.out.println("Service item " + serviceItemId + " added to booking " + bookingId + ".");
        return true;
    }

    //Alexander
    //ta bort en tjänst från en bokning, bara innan arbetet har påbörjats
    //en bokning ska innehålla minst en tjänst, så den sista kan inte tas bort
    public boolean removeServiceFromBooking(int bookingId, int serviceItemId) {
        Booking booking = findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return false;
        }

        if (isWorkStarted(booking)) {
            System.out.println("Work on booking " + bookingId + " has started. Services can no longer be changed.");
            return false;
        }

        if (!booking.containsServiceItem(serviceItemId)) {
            System.out.println("Service item " + serviceItemId + " is not in booking " + bookingId + ".");
            return false;
        }

        if (booking.getServiceItemIds().size() <= 1) {
            System.out.println("A booking must contain at least one service.");
            return false;
        }

        bookingDAO.removeServiceItem(bookingId, serviceItemId);
        System.out.println("Service item " + serviceItemId + " removed from booking " + bookingId + ".");
        return true;
    }

    //Alexander
    //UI:t hämtar data via GarageSystem
    public List<Booking> getBookings() {
        return bookingDAO.findAll();
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItemDAO.findAll();
    }

    //Alexander
    //gör om bokningens tjänste-ID:n till hela ServiceItem-objekt (namn, pris, tid)
    public List<ServiceItem> getServicesForBooking(int bookingId) {
        List<ServiceItem> result = new ArrayList<>();
        Booking booking = findBooking(bookingId);

        if (booking == null) {
            return result;  //tom lista istället för null
        }

        for (int serviceItemId : booking.getServiceItemIds()) {
            ServiceItem serviceItem = findServiceItem(serviceItemId);
            if (serviceItem != null) {
                result.add(serviceItem);
            }
        }
        return result;
    }
    //Alexander
    //total beräknad arbetstid i minuter för en lista av tjänster
    public int calculateTotalMinutes(List<ServiceItem> serviceItems) {
        int totalMinutes = 0;

        for (ServiceItem serviceItem : serviceItems) {
            totalMinutes += serviceItem.getEstimatedMinutes();
        }
        return totalMinutes;
    }

    //Alexander
    //totalt pris för en lista av tjänster
    public double calculateTotalPrice(List<ServiceItem> serviceItems) {
        double totalPrice = 0;

        for (ServiceItem serviceItem : serviceItems) {
            totalPrice += serviceItem.getPrice();
        }
        return totalPrice;
    }

    //Alexander
    //true om bokningen redan har en arbetsorder (en bokning kan bara ha en)
    public boolean hasWorkOrder(int bookingId) {
        for (WorkOrder workOrder : workOrderDAO.findAll()) {
            if (workOrder.getBookingId() == bookingId) {
                return true;
            }
        }
        return false;
    }


    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     int... serviceItemIds) {

        Booking booking = findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return null;
        }

        Mechanic mechanic = findMechanic(mechanicId);

        if (mechanic == null) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
        }

        if (!mechanic.isAvailable()) {
            System.out.println("Mechanic " + mechanic.getName() + " is not available.");
            return null;
        }

        for (int serviceItemId : serviceItemIds) {
            if (findServiceItem(serviceItemId) == null) {
                System.out.println(
                        "Service item with ID " + serviceItemId + " does not exist."
                );
                return null;
            }
        }
        WorkOrder workOrder = new WorkOrder(
                0,
                bookingId,
                mechanicId
        );

        for (int serviceItemId : serviceItemIds) {
            ServiceItem serviceItem = findServiceItem(serviceItemId);
            workOrder.addServiceItem(serviceItemId, serviceItem.getPrice());
        }

        workOrderDAO.save(workOrder);

        booking.setStatus("WORK_ORDER_CREATED");

        System.out.println("Work order created successfully.");
        System.out.println(workOrder);

        return workOrder;
    }

    public void startWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        if (!workOrder.getStatus().equals("CREATED")) {
            System.out.println("Work order cannot be started.");
            return;
        }

        Mechanic mechanic = findMechanic(workOrder.getMechanicId());
        Booking booking = findBooking(workOrder.getBookingId());

        if (mechanic != null) {
            mechanic.setAvailable(false);
            mechanicDAO.updateAvailable(mechanic);
        }

        if (booking != null) {
            booking.setStatus("IN_PROGRESS");
            bookingDAO.updateStatus(booking);
        }

        workOrder.setStatus("IN_PROGRESS");
        workOrderDAO.updateStatus(workOrder);

        System.out.println("Work order " + workOrderId + " has been started.");
    }

    public void completeWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        if (!workOrder.getStatus().equals("IN_PROGRESS")) {
            System.out.println("Only work orders in progress can be completed.");
            return;
        }

        Mechanic mechanic = findMechanic(workOrder.getMechanicId());
        Booking booking = findBooking(workOrder.getBookingId());

        workOrder.setStatus("COMPLETED");
        workOrderDAO.updateStatus(workOrder);

        if (mechanic != null) {
            mechanic.setAvailable(true);
            mechanicDAO.updateAvailable(mechanic);
        }

        if (booking != null) {
            booking.setStatus("COMPLETED");
            bookingDAO.updateStatus(booking);
        }

        System.out.println("Work order " + workOrderId + " has been completed.");
    }
    //Ändrat så att det tidigare priset hämtas från workOrder istället för det aktuella
//    public Invoice createInvoice(int workOrderId, String discountCode) {
//        WorkOrder workOrder = findWorkOrder(workOrderId);
//
//        if (workOrder == null) {
//            System.out.println("Work order with ID " + workOrderId + " does not exist.");
//            return null;
//        }
//
//        if (!workOrder.getStatus().equals("COMPLETED")) {
//            System.out.println("Invoice can only be created for a completed work order.");
//            return null;
//        }
//
//        double amount = 0.0;
//
//        for (Integer serviceItemId : workOrder.getServiceItemIds()) {
//            Double price = workOrder.getServiceItemPrices().get(serviceItemId);
//
//            if (price != null) {
//                amount += price;
//            }
//        }
//
//        double discount = 0.0;
//
//        Booking booking = findBooking(workOrder.getBookingId());
//
//        if (booking != null) {
//            Vehicle vehicle = findVehicle(booking.getVehicleId());
//
//            if (vehicle != null) {
//                Customer customer = findCustomer(vehicle.getCustomerId());
//
//                if (customer != null && customer.isVip()) {
//                    discount += amount * 0.10;
//                    System.out.println("VIP discount applied: 10%");
//                }
//            }
//        }
//
//        if (discountCode != null && !discountCode.trim().isEmpty()) {
//
//            if (discountCode.equalsIgnoreCase("WELCOME10")) {
//                discount += amount * 0.10;
//                System.out.println("Discount code WELCOME10 applied.");
//
//            } else if (discountCode.equalsIgnoreCase("SERVICE200")) {
//                discount += 200.0;
//                System.out.println("Discount code SERVICE200 applied.");
//
//            } else {
//                System.out.println("Unknown discount code. No code discount applied.");
//            }
//        }
//
//        if (discount > amount) {
//            discount = amount;
//        }
//
//        Invoice invoice = new Invoice(
//                0,
//                workOrderId,
//                LocalDate.now(),
//                amount
//        );
//
//        invoice.setDiscount(discount);
//        invoiceDAO.save(invoice);
//
//        System.out.println("Invoice created successfully.");
//        System.out.println(invoice);
//
//        System.out.println("Sending invoice notification to customer...");
//        System.out.println("Notification sent.");
//
//        return invoice;
//    }

    public Invoice createInvoice(int workOrderId, String discountCode) {

        WorkOrder workOrder = findWorkOrder(workOrderId);
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

        Booking booking = findBooking(workOrder.getBookingId());

        if (booking != null) {

            Vehicle vehicle = findVehicle(booking.getVehicleId());

            if (vehicle != null) {

                Customer customer = findCustomer(vehicle.getCustomerId());

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
            ServiceItem serviceItem = findServiceItem(serviceItemId);

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

    private Customer findCustomer(int id) {

        for (Customer customer : customerDAO.findAll()){
            if (customer.getId() == id){
                return customer;
            }
        }
        return null;
    }

    private Vehicle findVehicle(int id) {
        for (Vehicle vehicle : vehicleDAO.findAll()){
            if(vehicle.getId() == id){
                return vehicle;
            }
        }

        return null;
    }

    private Booking findBooking(int id) {
        for (Booking booking : bookingDAO.findAll()) {
            if (booking.getId() == id) {
                return booking;
            }
        }

        return null;
    }

    private Mechanic findMechanic(int id) {
        for (Mechanic mechanic : mechanicDAO.findAll()) {
            if (mechanic.getId() == id) {
                return mechanic;
            }
        }

        return null;
    }

    private ServiceItem findServiceItem(int id) {
        for (ServiceItem serviceItem : serviceItemDAO.findAll()) {
            if (serviceItem.getId() == id) {
                return serviceItem;
            }
        }

        return null;
    }

    private WorkOrder findWorkOrder(int id) {
        for (WorkOrder workOrder : workOrderDAO.findAll()) {
            if (workOrder.getId() == id) {
                return workOrder;
            }
        }


        return null;
    }

    private Invoice findInvoice(int id) {
        for (Invoice invoice : invoiceDAO.findAll()) {
            if (invoice.getId() == id) {
                return invoice;
            }
        }

        return null;
    }

    //NY METOD FÖR ATT UPPDATERA PRIS
    public void changeServicePrice(int serviceItemId, double newPrice){
        if (newPrice <= 0){
            System.out.println("Select price higher than 0");
            return;
        }
        ServiceItem serviceItem = findServiceItem(serviceItemId);
        if (serviceItem == null){
            System.out.println("ServiceItemId " + serviceItemId + " does not exist.");
            return;
        }
        serviceItemDAO.updatePrice(serviceItemId,newPrice);
        serviceItem.setPrice(newPrice);
    }

}