package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.*;
import com.wac.autocore.model.*;
import com.wac.autocore.service.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.order.CreateWorkOrderView;
import com.wac.autocore.view.order.OrderDetailsView;
import com.wac.autocore.view.order.OrderFormView;
import com.wac.autocore.view.order.OrderListView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import java.text.MessageFormat;
import java.util.List;

public class OrderController {

    private final BookingService bookingService;
    private final WorkOrderService workOrderService;
    private final MechanicService mechanicService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;

    private final AutoCoreApplication app;

    private final OrderFormView orderFormView;
    private final OrderListView orderListView;

    private final LanguageManager languageManager =
            LanguageManager.getInstance();

    public OrderController(
            BookingService bookingService, WorkOrderService workOrderService, MechanicService mechanicService, ServiceItemService serviceItemService, VehicleService vehicleService, CustomerService customerService,
            AutoCoreApplication app,
            OrderFormView orderFormView,
            OrderListView orderListView) {

        this.bookingService = bookingService;
        this.workOrderService = workOrderService;
        this.mechanicService = mechanicService;
        this.serviceItemService = serviceItemService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
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

    private void wireEvents() {
        // Skapa arbetsorder
        orderFormView.getCreateOrderButton().setOnAction(actionEvent -> {

            try {

                int bookingId = Integer.parseInt(orderFormView.getBookingIdField().getText());
                int mechanicId = Integer.parseInt(orderFormView.getMechanicIdField().getText());
                int[] serviceItemIds = parseServiceItemIds(orderFormView.getServiceItemIdsField().getText());

                Booking booking = bookingService.findBooking(bookingId);

                if (booking == null) {
                    showWarning(MessageFormat.format(languageManager.getString("bookingNotFound"),
                                    bookingId));
                    return;
                }

                if (workOrderService.hasWorkOrder(bookingId))   {
                    showWarning(languageManager.getString("existingWorkOrder"));
                    return;
                }

                if (workOrderService.isMechanicBookedOnDate(mechanicId, booking.getDate(), bookingId)) {
                    showWarning(languageManager.getString("mechanicAlreadyBooked"));
                    return;
                }

                WorkOrder workOrder = workOrderService.createWorkOrder(bookingId,
                                mechanicId, serviceItemIds);

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
            Booking booking = bookingService.findBooking(workOrder.getBookingId());

            if (booking == null) {
                return;
            }

            Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());

            if (vehicle == null) {
                return;
            }

            Customer customer = customerService.findCustomer(vehicle.getCustomerId());

            if (customer == null) {
                return;
            }

            List<Mechanic> mechanics = workOrderService.getAvailableMechanics(booking, workOrder);
            List<ServiceItem> orderServices = workOrderService.getServicesForWorkOrder(workOrder);

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
                workOrderService.removeServiceItemFromWorkOrder(workOrder,serviceItem.getId());
                detailsView.removeServiceFromTable(serviceItem);
            });

            detailsView.showSummary(
                    formatTotalTime(orderServices),
                    formatTotalPrice(orderServices)
            );

            detailsView.setOnStatusChange(newStatus -> {

                if ("IN_PROGRESS".equals(newStatus)) {
                    workOrderService.startWorkOrder(workOrder.getId());

                } else if ("COMPLETED".equals(newStatus)) {
                    workOrderService.completeWorkOrder(workOrder.getId());
                }
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
        CreateWorkOrderView createWorkOrderView = new CreateWorkOrderView(orderType, null);

        if ("dropIn".equals(orderType)) {

            createWorkOrderView.getCustomerComboBox()
                    .getItems()
                    .addAll(customerService.getAllCustomers());

            createWorkOrderView.getVehicleComboBox()
                    .getItems()
                    .addAll(vehicleService.getVehicles());

            createWorkOrderView.getMechanicComboBox()
                    .getItems()
                    .addAll(mechanicService.getMechanics());
        }

        // Om det är en planned order ska alla befintliga
        // bokningar visas i dropdownen
        if ("planned".equals(orderType)) {
            createWorkOrderView.getBookingComboBox()
                    .getItems()
                    .addAll(bookingService.getBookings());
        }

        createWorkOrderView.getBookingComboBox()
                .setOnAction(event -> {

                    Booking selectedBooking = createWorkOrderView.getBookingComboBox().getValue();
                    fillFromBooking(createWorkOrderView, selectedBooking);
                    updateAvailableMechanics(createWorkOrderView, selectedBooking);
                });

        createWorkOrderView.getBackButton().setOnAction(event -> {
            app.showView(orderListView.getView());
        });

        createWorkOrderView.getAddServiceButton().setOnAction(event -> {
            addServiceToWorkOrder(createWorkOrderView);
        });

        app.showView(createWorkOrderView.getView());
    }


    public void openCreateWorkOrderView(Booking booking) {
        CreateWorkOrderView createWorkOrderView = new CreateWorkOrderView("planned", booking);

        createWorkOrderView.getBookingComboBox()
                .setValue(booking);

        fillFromBooking(
                createWorkOrderView,
                booking
        );

        updateAvailableMechanics(
                createWorkOrderView,
                booking
        );

        createWorkOrderView.getBookingComboBox()
                .getItems()
                .addAll(bookingService.getBookings());

        createWorkOrderView.getBookingComboBox()
                .setValue(booking);

        createWorkOrderView.getBookingComboBox()
                .setOnAction(event -> {

                    Booking selectedBooking =
                            createWorkOrderView.getBookingComboBox().getValue();

                    fillFromBooking(
                            createWorkOrderView,
                            selectedBooking
                    );
                });

        createWorkOrderView.getBackButton().setOnAction(event -> {
            app.showView(orderListView.getView());
        });

        createWorkOrderView.getAddServiceButton().setOnAction(event -> {
            addServiceToWorkOrder(createWorkOrderView);
        });
        app.showView(createWorkOrderView.getView());
    }

    private void fillFromBooking(
            CreateWorkOrderView view,
            Booking booking) {

        if (booking == null) {
            return;
        }

        // Datum
        view.getDatePicker().setValue(booking.getDate());

        // Beskrivning
        view.getDescriptionField().setText(booking.getDescription());

        // Hämta fordonet från bookingens vehicleId
        Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());

        if (vehicle != null) {
            view.getVehicleComboBox()
                    .getItems()
                    .clear();

            view.getVehicleComboBox()
                    .getItems()
                    .add(vehicle);

            view.getVehicleComboBox()
                    .setValue(vehicle);

            Customer customer = customerService.findCustomer(vehicle.getCustomerId());

            if (customer != null) {

                view.getCustomerComboBox()
                        .getItems()
                        .clear();

                view.getCustomerComboBox()
                        .getItems()
                        .add(customer);

                view.getCustomerComboBox()
                        .setValue(customer);
            }
        }

        // Hämta tjänster från bokningen
        view.getServicesTable()
                .getItems()
                .clear();

        view.setBookingServiceItemIds(
                booking.getServiceItemIds()
        );

        for (int serviceItemId : booking.getServiceItemIds()) {

            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);

            if (serviceItem != null) {
                view.getServicesTable()
                        .getItems()
                        .add(serviceItem);
            }
        }

        view.showSummary(
                formatTotalTime(view.getServicesTable().getItems()),
                formatTotalPrice(view.getServicesTable().getItems())
        );
    }

    private void updateAvailableMechanics(
            CreateWorkOrderView view,
            Booking booking) {

        if (booking == null) {
            view.getMechanicComboBox()
                    .getItems()
                    .clear();
            return;
        }

        List<Mechanic> allMechanics =
                mechanicService.getMechanics();

        view.getMechanicComboBox()
                .getItems()
                .clear();

        for (Mechanic mechanic : allMechanics) {

            if (!workOrderService.isMechanicBookedOnDate(
                    mechanic.getId(),
                    booking.getDate(),
                    booking.getId())) {

                view.getMechanicComboBox()
                        .getItems()
                        .add(mechanic);
            }
        }

        view.getMechanicComboBox()
                .getSelectionModel()
                .clearSelection();
    }


    private void addServiceToWorkOrder(CreateWorkOrderView view) {
        ServiceItem selectedService = view.getServiceComboBox().getValue();
        List<ServiceItem> selectedServices = view.getServicesTable().getItems();

        if (selectedService == null) {
            return;
        }
        boolean alreadyExists = selectedServices.stream()
                .anyMatch(service ->
                        service.getId() == selectedService.getId()
                );

        if (!alreadyExists) {
            selectedServices.add(selectedService);

            view.getServiceComboBox().setValue(null);

            view.showSummary(
                    formatTotalTime(selectedServices),
                    formatTotalPrice(selectedServices)
            );
        }
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
                .setAll(workOrderService.findAllWorkOrders());
    }

    // Varningsruta
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
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

    private String formatTotalTime(List<ServiceItem> services) {
        if (services.isEmpty()) {
            return "--";
        }

        return bookingService.calculateTotalMinutes(services) + " min";
    }

    private String formatTotalPrice(List<ServiceItem> services) {
        if (services.isEmpty()) {
            return "--";
        }

        return String.format(
                "%,.0f kr",
                bookingService.calculateTotalPrice(services)
        );
    }
}