package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.OrderFormView;
import com.wac.autocore.view.OrderListView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.text.MessageFormat;
import java.time.LocalDate;


//Kopplar OrderFormView/OrderListView till GarageSystem — som hanterar skapande, start och slutförande av arbetsordrar
public class OrderController {

    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final OrderFormView orderFormView;
    private final OrderListView orderListView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public OrderController(GarageSystem garageSystem, AutoCoreApplication app,
                           OrderFormView orderFormView, OrderListView orderListView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.orderFormView = orderFormView;
        this.orderListView = orderListView;
        wireEvents();
        refreshOrderList();

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshOrderList();
        });
    }

    private void wireEvents() {
        orderFormView.getCreateOrderButton().setOnAction(actionEvent -> {
            try {
                int bookingId = Integer.parseInt(orderFormView.getBookingIdField().getText());
                int mechanicId = Integer.parseInt(orderFormView.getMechanicIdField().getText());
                int[] serviceItemIds = parseServiceItemIds(orderFormView.getServiceItemIdsField().getText());

                Booking booking = findBookingById(bookingId);

                if (booking == null) {
                    showWarning(MessageFormat.format(languageManager.getString("bookingNotFound"), bookingId));
                    return;
                }

                if (hasExistingWorkOrder(bookingId)) {
                    showWarning(languageManager.getString("existingWorkOrder"));
                    return;
                }

                if (isMechanicBookedOnDate(mechanicId, booking.getDate(), bookingId)) {
                    showWarning(languageManager.getString("mechanicAlreadyBooked"));
                    return;
                }

                WorkOrder workOrder = garageSystem.createWorkOrder(bookingId, mechanicId, serviceItemIds);

                if (workOrder != null) {
                    refreshOrderList();
                    orderFormView.getBookingIdField().clear();
                    orderFormView.getMechanicIdField().clear();
                    orderFormView.getServiceItemIdsField().clear();
                }
            } catch (NumberFormatException e) {
                showWarning(languageManager.getString("invalidOrderIds"));
            }
        });

        orderListView.getStartOrderButton().setOnAction(actionEvent -> {
            try {
                int workOrderId = Integer.parseInt(orderListView.getStartOrderIdField().getText());
                garageSystem.startWorkOrder(workOrderId);
                refreshOrderList();
                orderListView.getStartOrderIdField().clear();
            } catch (NumberFormatException e) {
                showWarning(languageManager.getString("invalidWorkOrderId"));
            }
        });

        orderListView.getCompleteOrderButton().setOnAction(actionEvent -> {
            try {
                int workOrderId = Integer.parseInt(orderListView.getCompleteOrderIdField().getText());
                garageSystem.completeWorkOrder(workOrderId);
                refreshOrderList();
                orderListView.getCompleteOrderIdField().clear();
            } catch (NumberFormatException e) {
                showWarning(languageManager.getString("invalidWorkOrderId"));
            }
        });

        orderFormView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        orderListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private int[] parseServiceItemIds(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new int[0];
        }
        String[] parts = text.split(",");
        int[] ids = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }

        return ids;
    }

    private void refreshOrderList() {
        orderListView.getOrderListView().getItems().clear();

        for (WorkOrder workOrder : Database.getWorkOrders()) {

            String orderInfo = workOrder.getId() + " | "
                    + languageManager.getString("bookingIdInTable")
                    + ": " + workOrder.getBookingId() + " | "
                    + languageManager.getString("mechanicIdInTable")
                    + ": " + workOrder.getMechanicId() + " | "
                    + languageManager.getString("serviceItemIdsInTable")
                    + ": " + workOrder.getServiceItemIds() + " | "
                    + languageManager.getString("statusInTable")
                    + ": " + workOrder.getStatus();

            orderListView.getOrderListView().getItems().add(orderInfo);
        }
    }

    private Booking findBookingById(int bookingId) {
        for (Booking booking : Database.getBookings()) {
            if (booking.getId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    private boolean hasExistingWorkOrder(int bookingId) {
        for (WorkOrder workOrder : Database.getWorkOrders()) {
            if (workOrder.getBookingId() == bookingId) {
                return true;
            }
        }
        return false;
    }

    private boolean isMechanicBookedOnDate(int mechanicId, LocalDate date, int excludingBookingId) {
        for (WorkOrder workOrder : Database.getWorkOrders()) {
            if (workOrder.getMechanicId() == mechanicId && workOrder.getBookingId() != excludingBookingId) {
                Booking otherBooking = findBookingById(workOrder.getBookingId());
                if (otherBooking != null && otherBooking.getDate().equals(date)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showWarning (String message){
        Alert alert = new Alert(Alert.AlertType.WARNING,message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public Parent getOrderFormView() {
        return orderFormView.getView();
    }

    public Parent getOrderListPane() {
        return orderListView.getView();
    }
}