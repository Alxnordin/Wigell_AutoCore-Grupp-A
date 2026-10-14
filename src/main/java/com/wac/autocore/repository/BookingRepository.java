package com.wac.autocore.repository;

import com.wac.autocore.model.Booking;

import java.util.List;

public interface BookingRepository {
    List<Booking> findAll();

    Booking findById(int bookingId);

    Booking save(Booking booking);

    void addServiceItem(int bookingId, int serviceItemId);

    void removeServiceItem(int bookingId, int serviceItemId);

    void updateStatus(Booking booking);

}
