package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Booking;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.BookingView;
import javafx.scene.Parent;
import java.time.LocalDate;

public class BookingController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final BookingView bookingView;

    public BookingController(GarageSystem garageSystem,AutoCoreApplication app,BookingView bookingView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.bookingView = bookingView;
        wireEvents();
        refreshBookingList();
    }
    private void wireEvents() {
        bookingView.getCreateBookingButton().setOnAction(actionEvent -> {
            String vehicleIdText = bookingView.getVehicleIdField().getText();
            LocalDate date = bookingView.getDate().getValue();
            String description = bookingView.getDescriptionField().getText();

            if (date == null) {
                System.out.println("Du måste välja ett datum.");
                return;
            }

            try {
                int vehicleId = Integer.parseInt(vehicleIdText);

                Booking booking = garageSystem.createBooking(vehicleId, date, description);

                if (booking != null) {
                    refreshBookingList();
                    bookingView.getVehicleIdField().clear();
                    bookingView.getDate().setValue(null);
                    bookingView.getDescriptionField().clear();
                }
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt fordons-ID — måste vara ett heltal.");
            }
        });

        bookingView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private void refreshBookingList() {
        bookingView.getBookingListView().getItems().clear();

        for (Booking booking : Database.getBookings()) {
            bookingView.getBookingListView().getItems().add(booking.toString());
        }
    }

    public Parent getView() {
        return bookingView.getView();
    }
}
