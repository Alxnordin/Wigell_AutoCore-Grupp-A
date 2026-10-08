package com.wac.autocore.service;

import com.wac.autocore.dao.BookingDAO;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO =  new BookingDAO();

    private final VehicleService vehicleService;
    private final ServiceItemService serviceItemService;

    public BookingService(VehicleService vehicleService, ServiceItemService serviceItemService) {
        this.vehicleService = vehicleService;
        this.serviceItemService = serviceItemService;
    }

    public List<Booking> getBookings() {
        return bookingDAO.findAll();
    }

    public Booking findBooking(int id) {
        for (Booking booking : bookingDAO.findAll()) {
            if (booking.getId() == id) {
                return booking;
            }
        }

        return null;
    }

    public Booking createBooking(int vehicleId,
                                 LocalDate date,
                                 String description,
                                 int... serviceItemIds) {

        Vehicle vehicle = vehicleService.findVehicle(vehicleId);

        if (vehicle == null) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
        }

        //Valda tjänster måste finnas
        for (int serviceItemId : serviceItemIds) {

            if (serviceItemService.findServiceItem(serviceItemId) == null) {
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

    //arbetet räknas som påbörjat när arbetsordern har startats
    // startWorkOrder/completeWorkOrder sparar då bokningens status som IN_PROGRESS/COMPLETED
    public boolean isWorkStarted(Booking booking) {
        return "IN_PROGRESS".equals(booking.getStatus())
                || "COMPLETED".equals(booking.getStatus());
    }

    //lägg till en tjänst i en bokning, bara innan arbetet har påbörjats
    public boolean addServiceToBooking(int bookingId, int serviceItemId) {
        Booking booking = findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return false;
        }

        if (isWorkStarted(booking)) {
           System.out.println("Work on booking " + bookingId + " has started. Services can no longer be changed.");
           return false;
        }

        if (serviceItemService.findServiceItem(serviceItemId) == null) {
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

    //gör om bokningens tjänste-ID:n till hela ServiceItem-objekt (namn, pris, tid)
    public List<ServiceItem> getServicesForBooking(int bookingId) {
        List<ServiceItem> result = new ArrayList<>();
        Booking booking = findBooking(bookingId);

        if (booking == null) {
            return result;  //tom lista istället för null
        }

        for (int serviceItemId : booking.getServiceItemIds()) {
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);
            if (serviceItem != null) {
                result.add(serviceItem);
            }
        }
        return result;
    }

    //total beräknad arbetstid i minuter för en lista av tjänster
    public int calculateTotalMinutes(List<ServiceItem> serviceItems) {
        int totalMinutes = 0;

        for (ServiceItem serviceItem : serviceItems) {
            totalMinutes += serviceItem.getEstimatedMinutes();
        }
        return totalMinutes;
    }

    //totalt pris för en lista av tjänster
    public double calculateTotalPrice(List<ServiceItem> serviceItems) {
        double totalPrice = 0;

        for (ServiceItem serviceItem : serviceItems) {
            totalPrice += serviceItem.getPrice();
        }
        return totalPrice;
    }

    public void updateStatus(Booking booking) {
        bookingDAO.updateStatus(booking);
    }

    //EJ KLAR!! Använda Prototype, kolla hur Fredrik gjort createComplaint() i WorkOrderService.
    // Det som ska följa med från gamla bokningen är:
    //- Kunden (alla fält)
    //- Fordon? Eller ska man kunna kunna välja om man vill använda samma fordon eller ett annat?
    //- ServicItem och fälten name, description, EJ PRICE och estimatedMinutes
    public Booking createBookingFromPreviousBooking(int vehicleId,
                                 LocalDate date,
                                 String description,
                                 int... serviceItemIds) {

        Vehicle vehicle = vehicleService.findVehicle(vehicleId);
        if (vehicle == null) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
        }

        for (int serviceItemId : serviceItemIds) {
            if (serviceItemService.findServiceItem(serviceItemId) == null) {
                System.out.println("Service item with ID " + serviceItemId + " does not exist.");
                return null;
            }
        }

        Booking booking = new Booking(0, vehicleId, date, description);
        for (int serviceItemId : serviceItemIds) {
            if (!booking.containsServiceItem(serviceItemId)) {
                booking.addServiceItem(serviceItemId);
            }
        }
        bookingDAO.save(booking);

        System.out.println("Booking created successfully.");
        System.out.println(booking);

        return booking;

    }



}
