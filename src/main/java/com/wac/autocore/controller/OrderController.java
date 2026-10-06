package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.*;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.order.CreateWorkOrderView;
import com.wac.autocore.view.order.OrderDetailsView;
import com.wac.autocore.view.order.OrderFormView;
import com.wac.autocore.view.order.OrderListView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Kopplar OrderFormView/OrderListView till GarageSystem
// och hanterar skapande och visning av arbetsordrar.
public class OrderController {

    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;

    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final MechanicDAO mechanicDAO = new MechanicDAO();
    private final ServiceItemDAO serviceItemDAO = new ServiceItemDAO();

    private final OrderFormView orderFormView;
    private final OrderListView orderListView;

    private final LanguageManager languageManager =
            LanguageManager.getInstance();

    public OrderController(
            GarageSystem garageSystem,
            AutoCoreApplication app,
            OrderFormView orderFormView,
            OrderListView orderListView) {

        this.garageSystem = garageSystem;
        this.app = app;
        this.orderFormView = orderFormView;
        this.orderListView = orderListView;

        wireEvents();
        refreshOrderList();

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    refreshOrderList();
                }
        );
    }

    private List<Mechanic> getAvailableMechanics(
            Booking currentBooking,
            WorkOrder currentWorkOrder) {

        List<Mechanic> allMechanics = mechanicDAO.findAll();
        List<WorkOrder> allWorkOrders = workOrderDAO.findAll();

        List<Mechanic> availableMechanics = new ArrayList<>(allMechanics);

        for (WorkOrder otherWorkOrder : allWorkOrders) {

            // Hoppa över den arbetsorder vi just tittar på
            if (otherWorkOrder.getId() == currentWorkOrder.getId()) {
                continue;
            }

            Booking otherBooking = findBookingById(
                    otherWorkOrder.getBookingId()
            );

            if (otherBooking == null) {
                continue;
            }

            // Kontrollera om arbetsordern ligger samma dag
            if (!otherBooking.getDate().equals(currentBooking.getDate())) {
                continue;
            }

            // Hitta mekanikern som redan är bokad
            for (Mechanic mechanic : allMechanics) {

                if (mechanic.getId() == otherWorkOrder.getMechanicId()) {
                    availableMechanics.remove(mechanic);
                }
            }
        }

        return availableMechanics;
    }

    private void wireEvents() {
        // Skapa arbetsorder
        orderFormView.getCreateOrderButton().setOnAction(actionEvent -> {

            try {

                int bookingId = Integer.parseInt(orderFormView.getBookingIdField().getText());
                int mechanicId = Integer.parseInt(orderFormView.getMechanicIdField().getText());
                int[] serviceItemIds = parseServiceItemIds(orderFormView.getServiceItemIdsField().getText());

                Booking booking = findBookingById(bookingId);


                if (booking == null) {
                    showWarning(
                            MessageFormat.format(
                                    languageManager.getString("bookingNotFound"),
                                    bookingId
                            )
                    );

                    return;
                }

                if (hasExistingWorkOrder(bookingId)) {
                    showWarning(languageManager.getString("existingWorkOrder"));
                    return;
                }

                if (isMechanicBookedOnDate(
                        mechanicId,
                        booking.getDate(),
                        bookingId)) {

                    showWarning(languageManager.getString(
                            "mechanicAlreadyBooked"));
                    return;
                }

                WorkOrder workOrder = garageSystem.createWorkOrder(
                                bookingId,
                                mechanicId,
                                serviceItemIds
                );


                if (workOrder != null) {
                    refreshOrderList();

                    orderFormView
                            .getBookingIdField()
                            .clear();

                    orderFormView
                            .getMechanicIdField()
                            .clear();

                    orderFormView
                            .getServiceItemIdsField()
                            .clear();
                }

            } catch (NumberFormatException e) {
                showWarning(
                        languageManager.getString("invalidOrderIds")
                );
            }
        });

        // Tillbaka
        orderFormView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        orderListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        // Visa detaljer för vald arbetsorder
        orderListView.setOnViewOrder(workOrder -> {
            Booking booking = findBookingById(workOrder.getBookingId());

            if (booking == null) {
                return;
            }

            Vehicle vehicle = findVehicleById(booking.getVehicleId());

            if (vehicle == null) {
                return;
            }

            Customer customer = findCustomerById(vehicle.getCustomerId());

            if (customer == null) {
                return;
            }

            List<Mechanic> mechanics = getAvailableMechanics(booking, workOrder);

            List<ServiceItem> allServices = serviceItemDAO.findAll();
            List<ServiceItem> orderServices = new ArrayList<>();

            for (ServiceItem serviceItem : allServices) {

                if (workOrder.getServiceItemIds().contains(serviceItem.getId())) {
                    orderServices.add(serviceItem);
                }
            }


            OrderDetailsView detailsView = new OrderDetailsView(
                    workOrder,
                    booking,
                    vehicle,
                    customer,
                    mechanics,
                    orderServices
            );

            detailsView.setOnDeleteService(serviceItem -> {
                System.out.println("Antal services före delete " + workOrder.getServiceItemIds().size());
                if(workOrder.getServiceItemIds().size() <=1){
                    showWarning(languageManager.getString("lastServiceWarning"));
                    return;
                }
                garageSystem.removeServiceItemFromWorkOrder(workOrder,serviceItem.getId());
                detailsView.removeServiceFromTable(serviceItem);
            });

            detailsView.showSummary(
                    formatTotalTime(orderServices),
                    formatTotalPrice(orderServices)
            );

            detailsView.setOnStatusChange(newStatus -> {
                workOrder.setStatus(newStatus);
                workOrderDAO.updateStatus(workOrder);
                detailsView.updateStatus();
            });

            detailsView.setOnBack(() -> {
                app.showView(orderListView.getView());
            });

            app.showView(detailsView.getView());
        });

        orderListView.setOnCreateOrderType(this::openCreateWorkOrderView);

    }

    private void openCreateWorkOrderView(String orderType) {
        CreateWorkOrderView createWorkOrderView =
                new CreateWorkOrderView(orderType);

        createWorkOrderView.getBackButton().setOnAction(event -> {
            app.showView(orderListView.getView());
        });

        app.showView(createWorkOrderView.getView());
    }

    //Alexander
    //fyller i formuläret från en bokning (används när man kommer från bokningslistan):
    //bokningens ID och bokningens tjänster
    public void prefillFromBooking(Booking booking) {
        StringBuilder serviceItemIds = new StringBuilder();

        for (int serviceItemId : booking.getServiceItemIds()) {
            if (serviceItemIds.length() > 0) {
                serviceItemIds.append(", ");
            }
            serviceItemIds.append(serviceItemId);
        }

        orderFormView.getBookingIdField().setText(String.valueOf(booking.getId()));
        orderFormView.getServiceItemIdsField().setText(serviceItemIds.toString());
    }

    // Hantera service-ID:n
    private int[] parseServiceItemIds(String text) {

        if (text == null || text.trim().isEmpty()) {
            return new int[0];
        }

        String[] parts = text.split(",");
        int[] ids = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {

            ids[i] = Integer.parseInt(
                    parts[i].trim()
            );
        }
        return ids;
    }

    // Uppdatera arbetsorder-tabellen
    private void refreshOrderList() {
        orderListView
                .getOrderTable()
                .getItems()
                .setAll(
                        workOrderDAO.findAll()
                );
    }

    // Hitta bokning
    private Booking findBookingById(int bookingId) {
        for (Booking booking : bookingDAO.findAll()) {

            if (booking.getId() == bookingId) {
                return booking;
            }
        }
        return null;
    }


    // Kontrollera om arbetsorder redan finns
    private boolean hasExistingWorkOrder(int bookingId) {
        for (WorkOrder workOrder : workOrderDAO.findAll()) {

            if (workOrder.getBookingId() == bookingId) {
                return true;
            }
        }
        return false;
    }

    // Kontrollera mekaniker
    private boolean isMechanicBookedOnDate(
            int mechanicId,
            LocalDate date,
            int excludingBookingId) {

        for (WorkOrder workOrder : workOrderDAO.findAll()) {

            if (workOrder.getMechanicId() == mechanicId
                    && workOrder.getBookingId() != excludingBookingId) {

                Booking otherBooking =
                        findBookingById(
                                workOrder.getBookingId()
                        );

                if (otherBooking != null
                        && otherBooking.getDate().equals(date)) {

                    return true;
                }
            }
        }
        return false;
    }

    // Varningsruta
    private void showWarning(String message) {

        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                message
        );
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Views
    public Parent getOrderFormView() {
        return orderFormView.getView();
    }

    public Parent getOrderListPane() {
        return orderListView.getView();
    }

    private Vehicle findVehicleById(int vehicleId) {
        for (Vehicle vehicle : vehicleDAO.findAll()) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }

    private Customer findCustomerById(int customerId) {
        for (Customer customer : customerDAO.findAll()) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    private String formatTotalTime(List<ServiceItem> services) {
        if (services.isEmpty()) {
            return "--";
        }

        return garageSystem.calculateTotalMinutes(services) + " min";
    }

    private String formatTotalPrice(List<ServiceItem> services) {
        if (services.isEmpty()) {
            return "--";
        }

        return String.format(
                "%,.0f kr",
                garageSystem.calculateTotalPrice(services)
        );
    }
}