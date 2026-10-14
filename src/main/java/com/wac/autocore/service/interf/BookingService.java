package com.wac.autocore.service.interf;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.ServiceItem;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    List<Booking> getBookings();

    Booking findBooking(int id);

    //Alexander
    //fordon kan saknas (null) - servicen validerar och kastar exception vid fel
    Booking createBooking(Integer vehicleId,
                          LocalDate date,
                          String description,
                          int... serviceItemIds);

    boolean isWorkStarted(Booking booking);

    //Alexander
    //kastar exception om tjänsten inte kan läggas till/tas bort, istället för att svara false
    void addServiceToBooking(int bookingId, int serviceItemId);

    void removeServiceFromBooking(int bookingId, int serviceItemId);

    List<ServiceItem> getServicesForBooking(int bookingId);

    int calculateTotalMinutes(List<ServiceItem> serviceItems);

    double calculateTotalPrice(List<ServiceItem> serviceItems);

    Booking createBookingFromPreviousBooking(int originalBookingId, LocalDate newDate);

    void updateStatus(Booking booking);

}