package com.wac.autocore.service.interf;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.ServiceItem;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    List<Booking> getBookings();

    Booking findBooking(int id);

    Booking createBooking(int vehicleId,
                          LocalDate date,
                          String description,
                          int... serviceItemIds);

    boolean isWorkStarted(Booking booking);

    boolean addServiceToBooking(int bookingId, int serviceItemId);

    boolean removeServiceFromBooking(int bookingId, int serviceItemId);

    List<ServiceItem> getServicesForBooking(int bookingId);

    int calculateTotalMinutes(List<ServiceItem> serviceItems);

    double calculateTotalPrice(List<ServiceItem> serviceItems);

    Booking createBookingFromPreviousBooking(int originalBookingId, LocalDate newDate);

    void updateStatus(Booking booking);

}

