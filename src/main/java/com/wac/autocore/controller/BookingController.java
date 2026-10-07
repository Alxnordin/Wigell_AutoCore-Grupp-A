package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.booking.BookingDetailsView;
import com.wac.autocore.view.booking.BookingListView;
import com.wac.autocore.view.booking.BookingView;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;

public class BookingController {

    private final ServiceItemService serviceItemService;
    private final BookingService bookingService;
    private final WorkOrderService workOrderService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;

    private final AutoCoreApplication app;
    private final BookingView bookingView;
    private final BookingListView bookingListView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public BookingController(ServiceItemService serviceItemService,
                             BookingService bookingService, WorkOrderService workOrderService,
                             VehicleService vehicleService, CustomerService customerService,
                             AutoCoreApplication app, BookingView bookingView,
                             BookingListView bookingListView) {
        this.serviceItemService = serviceItemService;
        this.bookingService = bookingService;
        this.workOrderService = workOrderService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.app = app;
        this.bookingView = bookingView;
        this.bookingListView = bookingListView;
        wireEvents();
        refreshBookingList();

//        bookingListView.getServiceComboBox().getItems().addAll(serviceItemService.getServiceItems());

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshBookingList();
            updateNewBookingSummary();
        });
    }

    private void wireEvents() {

        bookingView.getAddServiceButton().setOnAction(actionEvent -> addServiceToNewBooking());

        bookingView.getServicesTable().getItems().addListener(
                (ListChangeListener<ServiceItem>) change -> updateNewBookingSummary());

        bookingView.getCreateBookingButton().setOnAction(actionEvent -> {
            Vehicle selectedVehicle = bookingView.getVehicleComboBox().getValue();
            LocalDate date = bookingView.getDate().getValue();
            String description = bookingView.getDescriptionField().getText();
            List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

            if (selectedVehicle == null) {
                showWarning(languageManager.getString("missingVehicleWarning"));
                return;
            }

            if (date == null) {
                showWarning(languageManager.getString("missingDateWarning"));
                return;
            }

            if (selectedServices.isEmpty()) {
                showWarning(languageManager.getString("noServiceSelectedWarning"));
                return;
            }

            try {
                int vehicleId = selectedVehicle.getId();

                int[] serviceItemIds = new int[selectedServices.size()];
                for (int i = 0; i < selectedServices.size(); i++) {
                    serviceItemIds[i] = selectedServices.get(i).getId();
                }

                Booking booking = bookingService.createBooking(vehicleId, date, description, serviceItemIds);

                if (booking != null) {
                    refreshBookingList();
                    bookingView.getVehicleComboBox().setValue(null);
                    bookingView.getDate().setValue(null);
                    bookingView.getDescriptionField().clear();
                    bookingView.getServiceComboBox().setValue(null);
                    bookingView.getServicesTable().getItems().clear();
                }
            } catch (NumberFormatException e) {
                showWarning(languageManager.getString("invalidVehicleIdWarning"));
            }
        });

//        bookingListView.getBookingTable().getSelectionModel().selectedItemProperty().addListener(
//                (observable, oldBooking, newBooking) -> showServicesForSelectedBooking());
//        bookingListView.getAddServiceButton().setOnAction(actionEvent -> addServiceToSelectedBooking());
//        bookingListView.getRemoveServiceButton().setOnAction(actionEvent -> removeServiceFromSelectedBooking());

        bookingListView.setOnCreateWorkOrder(booking -> openCreateWorkOrder(booking));
        bookingListView.setOnViewBooking(booking -> openBookingDetails(booking));

        bookingView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        bookingListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private void addServiceToNewBooking() {
        ServiceItem selectedService = bookingView.getServiceComboBox().getValue();
        List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

        if (selectedService != null && !selectedServices.contains(selectedService)) {
            selectedServices.add(selectedService);
            bookingView.getServiceComboBox().setValue(null);
        }
    }

    private void updateNewBookingSummary() {
        List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

        bookingView.showSummary(formatTotalTime(selectedServices), formatTotalPrice(selectedServices));
    }

//    private void showServicesForSelectedBooking() {
//        Booking booking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();
//
//        if (booking == null) {
//            bookingListView.getBookingServicesTable().getItems().clear();
//            bookingListView.showSummary("--", "--");
//            bookingListView.setServiceEditingDisabled(true);
//            bookingListView.setLockedMessageVisible(false);
//            return;
//        }
//
//        List<ServiceItem> services = bookingService.getServicesForBooking(booking.getId());
//        bookingListView.getBookingServicesTable().getItems().setAll(services);
//        bookingListView.showSummary(formatTotalTime(services), formatTotalPrice(services));
//
//        boolean workStarted = bookingService.isWorkStarted(booking);
//        bookingListView.setServiceEditingDisabled(workStarted);
//        bookingListView.setLockedMessageVisible(workStarted);
//    }

    private boolean addServiceToBooking(Booking booking, ServiceItem service) {
        if (service == null) {
            showWarning(languageManager.getString("selectServiceToAddWarning"));
            return false;
        }

        if (booking.containsServiceItem(service.getId())) {
            showWarning(languageManager.getString("serviceAlreadyInBookingWarning"));
            return false;
        }

        if (!bookingService.addServiceToBooking(booking.getId(), service.getId())) {
            showWarning(languageManager.getString("serviceAlreadyInBookingWarning"));
            return false;
        }

        return true;
    }

//    private void addServiceToSelectedBooking() {
//        Booking booking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();
//        ServiceItem service = bookingListView.getServiceComboBox().getValue();
//
//        if (booking == null) {
//            showWarning(languageManager.getString("selectServiceToAddWarning"));
//            return;
//        }
//
//        if (addServiceToBooking(booking, service)) {
//            bookingListView.getServiceComboBox().setValue(null);
//            refreshBookingList();
//        }
//    }

    private boolean removeServiceFromBooking(Booking booking, ServiceItem service) {
        if (service == null) {
            showWarning(languageManager.getString("selectServiceToRemoveWarning"));
            return false;
        }

        List<ServiceItem> services = bookingService.getServicesForBooking(booking.getId());

        if (services.size() <= 1) {
            showWarning(languageManager.getString("lastServiceWarning"));
            return false;
        }

        if (!bookingService.removeServiceFromBooking(booking.getId(), service.getId())) {
            showWarning(languageManager.getString("serviceChangeFailedWarning"));
            return false;
        }

        return true;
    }

//    private void removeServiceFromSelectedBooking() {
//        Booking booking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();
//        ServiceItem service = bookingListView.getBookingServicesTable().getSelectionModel().getSelectedItem();
//
//        if (booking == null) {
//            showWarning(languageManager.getString("selectServiceToRemoveWarning"));
//            return;
//        }
//
//        if (removeServiceFromBooking(booking, service)) {
//            refreshBookingList();
//        }
//    }

    private void openCreateWorkOrder(Booking booking) {
        if (workOrderService.hasWorkOrder(booking.getId())) {
            showWarning(languageManager.getString("existingWorkOrder"));
            return;
        }

        app.showOrderFormView(booking);
    }

    private void openBookingDetails(Booking booking) {
        Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());
        if (vehicle == null) {
            return;
        }

        Customer customer = customerService.findCustomer(vehicle.getCustomerId());
        if (customer == null) {
            return;
        }

        List<ServiceItem> services = bookingService.getServicesForBooking(booking.getId());

        BookingDetailsView detailsView = new BookingDetailsView(booking, vehicle, customer, services);

        detailsView.setOnBack(() -> {
            app.showView(bookingListView.getView());
        });

        detailsView.setOnBookingAction(action -> {
            if ("edit".equals(action)) {
                if (bookingService.isWorkStarted(booking)) {
                    showWarning(languageManager.getString("bookingLockedInfo"));
                    return;
                }

                detailsView.showEditServices();
                detailsView.getServiceComboBox().getItems().setAll(serviceItemService.getServiceItems());
            }
        });

        detailsView.getAddServiceButton().setOnAction(event -> {
            ServiceItem service = detailsView.getServiceComboBox().getValue();

            if (addServiceToBooking(booking, service)) {
                List<ServiceItem> updatedServices = bookingService.getServicesForBooking(booking.getId());

                detailsView.getServicesTable().getItems().setAll(updatedServices);
                detailsView.showSummary(
                        formatTotalTime(updatedServices),
                        formatTotalPrice(updatedServices)
                );

                detailsView.getServiceComboBox().setValue(null);
            }
        });

        detailsView.getRemoveServiceButton().setOnAction(event -> {
            ServiceItem service = detailsView.getServicesTable().getSelectionModel().getSelectedItem();

            if (removeServiceFromBooking(booking, service)) {
                List<ServiceItem> updatedServices = bookingService.getServicesForBooking(booking.getId());

                detailsView.getServicesTable().getItems().setAll(updatedServices);
                detailsView.showSummary(
                        formatTotalTime(updatedServices),
                        formatTotalPrice(updatedServices)
                );
            }
        });

        app.showView(detailsView.getView());
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

        return String.format("%,.0f kr", bookingService.calculateTotalPrice(services));
    }

    private void refreshBookingList() {
        Booking selectedBooking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();

        bookingListView.getBookingTable().getItems().setAll(bookingService.getBookings());

        if (selectedBooking != null) {
            for (Booking booking : bookingListView.getBookingTable().getItems()) {
                if (booking.getId() == selectedBooking.getId()) {
                    bookingListView.getBookingTable().getSelectionModel().select(booking);
                }
            }
        }

        //showServicesForSelectedBooking();
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