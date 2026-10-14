package com.wac.autocore.service.impl;

import com.wac.autocore.dao.BookingDAO;
import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.service.interf.BookingService;
import com.wac.autocore.service.interf.ServiceItemService;
import com.wac.autocore.service.interf.VehicleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingDAO bookingDAO;

    private final VehicleService vehicleService;
    private final ServiceItemService serviceItemService;

    public BookingServiceImpl(BookingDAO bookingDAO, VehicleService vehicleService, ServiceItemService serviceItemService) {
        this.bookingDAO = bookingDAO;
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

    //Alexander
    //validerar bokningen och kastar exception vid fel, så att controllern bara visar felet via ErrorHandler.
    //ValidationException = något saknas i formuläret, NotFoundException = fordon/tjänst finns inte
    public Booking createBooking(Integer vehicleId,
                                 LocalDate date,
                                 String description,
                                 int... serviceItemIds) {

        if (vehicleId == null) {
            throw new ValidationException("missingVehicleWarning");
        }
        validateBookingDate(date);

        if (serviceItemIds == null || serviceItemIds.length == 0) {
            throw new ValidationException("noServiceSelectedWarning");
        }

        if (vehicleService.findVehicle(vehicleId) == null) {
            throw new NotFoundException("vehicleNotFoundError", String.valueOf(vehicleId));
        }

        //valda tjänster måste finnas
        for (int serviceItemId : serviceItemIds) {
            requireServiceItem(serviceItemId);
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

        return booking;
    }

    //arbetet räknas som påbörjat när arbetsordern har startats
    // startWorkOrder/completeWorkOrder sparar då bokningens status som IN_PROGRESS/COMPLETED
    public boolean isWorkStarted(Booking booking) {
        return "IN_PROGRESS".equals(booking.getStatus())
                || "COMPLETED".equals(booking.getStatus());
    }

    //lägg till en tjänst i en bokning, bara innan arbetet har påbörjats
    //Alexander: verksamhetsregler bryts -> BusinessRuleException
    public void addServiceToBooking(int bookingId, int serviceItemId) {
        Booking booking = requireEditableBooking(bookingId);
        requireServiceItem(serviceItemId);

        if (booking.containsServiceItem(serviceItemId)) {
            throw new BusinessRuleException("serviceAlreadyInBookingWarning");
        }

        bookingDAO.addServiceItem(bookingId, serviceItemId);
    }

    //ta bort en tjänst från en bokning, bara innan arbetet har påbörjats
    //en bokning ska innehålla minst en tjänst, så den sista kan inte tas bort
    public void removeServiceFromBooking(int bookingId, int serviceItemId) {
        Booking booking = requireEditableBooking(bookingId);

        if (!booking.containsServiceItem(serviceItemId)) {
            throw new BusinessRuleException("serviceNotInBookingError");
        }

        if (booking.getServiceItemIds().size() <= 1) {
            throw new BusinessRuleException("lastServiceWarning");
        }

        bookingDAO.removeServiceItem(bookingId, serviceItemId);
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

    public Booking createBookingFromPreviousBooking(int originalBookingId, LocalDate newDate) {

        Booking original = findBooking(originalBookingId);

        if (original == null) {
            throw new NotFoundException("bookingNotFoundError", String.valueOf(originalBookingId));
        }

        if (vehicleService.findVehicle(original.getVehicleId()) == null) {
            throw new NotFoundException("vehicleNotFoundError", String.valueOf(original.getVehicleId()));
        }

        validateBookingDate(newDate);

        for (int serviceItemId : original.getServiceItemIds()) {
            requireServiceItem(serviceItemId);
        }

        Booking newBooking = original.cloneBooking();
        newBooking.setId(0);
        newBooking.setDate(newDate);
        newBooking.setStatus("BOOKED");
        newBooking.setOriginalBookingId(original.getId());

        bookingDAO.save(newBooking);

        return newBooking;
    }

    //Alexander
    //hjälpmetoder så att samma regel bara skrivs på ett ställe

    //datum måste vara valt och får inte ligga bakåt i tiden
    private void validateBookingDate(LocalDate date) {
        if (date == null) {
            throw new ValidationException("missingDateWarning");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new ValidationException("bookingDateInPastError");
        }
    }

    //tjänsten måste finnas
    private void requireServiceItem(int serviceItemId) {
        if (serviceItemService.findServiceItem(serviceItemId) == null) {
            throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
        }
    }

    //bokningen måste finnas och arbetet får inte ha påbörjats
    private Booking requireEditableBooking(int bookingId) {
        Booking booking = findBooking(bookingId);

        if (booking == null) {
            throw new NotFoundException("bookingNotFoundError", String.valueOf(bookingId));
        }
        if (isWorkStarted(booking)) {
            throw new BusinessRuleException("bookingLockedInfo");
        }
        return booking;
    }
}