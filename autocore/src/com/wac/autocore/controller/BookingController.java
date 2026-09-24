package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Booking;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.BookingListView;
import com.wac.autocore.view.BookingView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import java.time.LocalDate;

//Den klass som kopplar BookingView/BookingListView till GarageSystem samt hanterar skapande av bokningar och navigering.
public class BookingController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final BookingView bookingView;
    private final BookingListView bookingListView;

    public BookingController(GarageSystem garageSystem,AutoCoreApplication app,
                             BookingView bookingView,BookingListView bookingListView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.bookingView = bookingView;
        this.bookingListView = bookingListView;
        wireEvents();
        refreshBookingList();
    }
    private void wireEvents() {
        bookingView.getCreateBookingButton().setOnAction(actionEvent -> {
            String vehicleIdText = bookingView.getVehicleIdField().getText();
            LocalDate date = bookingView.getDate().getValue();
            String description = bookingView.getDescriptionField().getText();

            if (date == null) {
                showWarning("Du måste välja ett datum.");
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
                showWarning("Ogiltigt fordons-ID — måste vara ett heltal.");
            }
        });

        bookingView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        bookingListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private void refreshBookingList() {
        bookingListView.getBookingListView().getItems().clear();

        for (Booking booking : Database.getBookings()) {
            bookingListView.getBookingListView().getItems().add(booking.toString());
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public Parent getBookingFormView() {
        return bookingView.getView();
    }

    public Parent getBookingListPane() {
        return bookingListView.getView();
    }
}