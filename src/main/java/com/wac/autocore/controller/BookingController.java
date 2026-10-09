package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.*;
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
    private final ServicePackageService servicePackageService;
    private final BookingService bookingService;
    private final WorkOrderService workOrderService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;

    private final AutoCoreApplication app;
    private final BookingView bookingView;
    private final BookingListView bookingListView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public BookingController(ServiceItemService serviceItemService,
                             ServicePackageService servicePackageService,
                             BookingService bookingService, WorkOrderService workOrderService,
                             VehicleService vehicleService, CustomerService customerService,
                             AutoCoreApplication app, BookingView bookingView,
                             BookingListView bookingListView) {
        this.serviceItemService = serviceItemService;
        this.servicePackageService = servicePackageService;
        this.bookingService = bookingService;
        this.workOrderService = workOrderService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.app = app;
        this.bookingView = bookingView;
        this.bookingListView = bookingListView;
        wireEvents();
        refreshBookingList();

        //Alexander
        //rullistan "Välj tjänst" i Skapa bokning: först de enskilda tjänsterna, sedan servicepaketen.
        //Ett paket utan tjänster visas inte, eftersom det inte finns något att lägga till.
        bookingView.getServiceComboBox().getItems().addAll(serviceItemService.getServiceItems());
        for (ServicePackage servicePackage : servicePackageService.servicePackages()) {
            if (servicePackage.getServiceCount() > 0) {
                bookingView.getServiceComboBox().getItems().add(servicePackage);
            }
        }

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
                    showBookingConfirmationMessage("Bokning skapad. Det här är en tillfällig lösning " +
                    "på bekräftelse :)");
                }
            } catch (NumberFormatException e) {
                showWarning(languageManager.getString("invalidVehicleIdWarning"));
            }
        });

        bookingListView.setOnCreateWorkOrder(booking -> openCreateWorkOrder(booking));
        bookingListView.setOnViewBooking(booking -> openBookingDetails(booking));

        bookingView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        bookingListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    //Alexander
    //lägger det som är valt i rullistan i listan för den nya bokningen.
    //Composite: en tjänst lämnar ut sig själv och ett paket lämnar ut alla sina tjänster,
    //så tjänst och paket hanteras på samma sätt. Samma tjänst kan bara väljas en gång.
    private void addServiceToNewBooking() {
        ServiceComponent selected = bookingView.getServiceComboBox().getValue();
        List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

        if (selected == null) {
            return;
        }

        for (ServiceItem serviceItem : selected.getServiceItems()) {
            if (!containsService(selectedServices, serviceItem.getId())) {
                selectedServices.add(serviceItem);
            }
        }
        bookingView.getServiceComboBox().setValue(null);
    }

    //Alexander
    //true om tjänsten redan finns bland de valda tjänsterna (jämförs på ID)
    private boolean containsService(List<ServiceItem> services, int serviceItemId) {
        for (ServiceItem serviceItem : services) {
            if (serviceItem.getId() == serviceItemId) {
                return true;
            }
        }
        return false;
    }


    private void updateNewBookingSummary() {
        List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

        bookingView.showSummary(formatTotalTime(selectedServices), formatTotalPrice(selectedServices));
    }

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

    private void openCreateWorkOrder(Booking booking) {
        if (workOrderService.hasWorkOrder(booking.getId())) {
            showWarning(languageManager.getString("existingWorkOrder"));
            return;
        }

        app.showCreateWorkOrderView(booking);
    }

    private void openCreateBookingView(Booking booking) {
        fillFromBooking(bookingView, booking);
        bookingView.showWhenCreateBookingFromExistingBooking();
        app.showView(bookingView.getView());
    }

    private void fillFromBooking(BookingView view, Booking booking) {
        Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());
        if (vehicle != null) {
            view.getVehicleComboBox().setValue(vehicle);
        }

        Customer customer = customerService.findCustomer(vehicle.getCustomerId());
        if (customer != null) {
            view.getCustomerComboBox().setValue(customer);
        }

        view.getDescriptionField().setText(booking.getDescription());

        view.getServicesTable().getItems().clear();

        //view.setBookingServiceItemIds(booking.getServiceItemIds());

        for (int serviceItemId : booking.getServiceItemIds()) {

            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);

            if (serviceItem != null) {
                view.getServicesTable()
                        .getItems()
                        .add(serviceItem);
            }
        }

        view.showSummary(formatTotalTime(view.getServicesTable().getItems()),
                formatTotalPrice(view.getServicesTable().getItems()));
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

        detailsView.setOnBack(() -> {app.showView(bookingListView.getView());});

        detailsView.setOnCancelButton(() -> {
            List<ServiceItem> originalServices =
                    bookingService.getServicesForBooking(booking.getId());

            detailsView.getServicesTable().getItems().setAll(originalServices);

            detailsView.showSummary(
                    formatTotalTime(originalServices),
                    formatTotalPrice(originalServices)
            );

            detailsView.getServiceComboBox().setValue(null);

            detailsView.showBookingDetails();
        });

        bookingView.setOnCancelButton(() -> {
            app.showView(detailsView.getView());
        });

        detailsView.setOnBookingAction(action -> {
            if ("edit".equals(action)) {
                if (bookingService.isWorkStarted(booking)) {
                    showWarning(languageManager.getString("bookingLockedInfo"));
                    return;
                }

                detailsView.showEditServices();
                detailsView.getServiceComboBox().getItems().setAll(serviceItemService.getServiceItems());

                for (ServicePackage servicePackage : servicePackageService.servicePackages()) {
                    if (servicePackage.getServiceCount() > 0) {
                        detailsView.getServiceComboBox().getItems().add(servicePackage);
                    }
                }

                } else if ("createFromExisting".equals(action)) {
                    openCreateBookingView(booking);
            }
        });

        detailsView.getAddServiceButton().setOnAction(event -> {
            ServiceComponent selected = detailsView.getServiceComboBox().getValue();

            if (selected == null) {
                return;
            }

            for (ServiceItem serviceItem : selected.getServiceItems()) {

                boolean alreadyExists = detailsView.getServicesTable().getItems().stream()
                        .anyMatch(item -> item.getId() == serviceItem.getId());

                if (!alreadyExists) {
                    detailsView.getServicesTable().getItems().add(serviceItem);
                }
            }

            List<ServiceItem> updatedServices =
                    detailsView.getServicesTable().getItems();

            detailsView.showSummary(
                    formatTotalTime(updatedServices),
                    formatTotalPrice(updatedServices)
            );

            detailsView.getServiceComboBox().setValue(null);
        });

        detailsView.getRemoveServiceButton().setOnAction(event -> {
            ServiceItem service = detailsView.getServicesTable()
                    .getSelectionModel().getSelectedItem();

            if (service == null) {
                showWarning(languageManager.getString("selectServiceToRemoveWarning"));
                return;
            }

            if (detailsView.getServicesTable().getItems().size() <= 1) {
                showWarning(languageManager.getString("lastServiceWarning"));
                return;
            }

            detailsView.getServicesTable().getItems().remove(service);

            List<ServiceItem> updatedServices = detailsView.getServicesTable().getItems();

            detailsView.showSummary(
                    formatTotalTime(updatedServices),
                    formatTotalPrice(updatedServices)
            );
        });

        detailsView.getSaveEditingButton().setOnAction(event -> {

            List<ServiceItem> originalServices = bookingService.getServicesForBooking(booking.getId());

            List<ServiceItem> editedServices = detailsView.getServicesTable().getItems();

            //Lägg till nya tjänster
            for (ServiceItem service : editedServices) {
                boolean exists = originalServices.stream()
                        .anyMatch(item -> item.getId() == service.getId());

                if (!exists) {
                    addServiceToBooking(booking, service);
                }
            }

            //Ta bort borttagna tjänster
            for (ServiceItem service : originalServices) {
                boolean exists = editedServices.stream()
                        .anyMatch(item -> item.getId() == service.getId());

                if (!exists) {
                    removeServiceFromBooking(booking, service);
                }
            }

            showChangesConfirmationMessage("Ändringarna är sparade. Det här är en tillfällig lösning " +
                    "på bekräftelse :)");
            detailsView.showBookingDetails();
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

    private void showChangesConfirmationMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showBookingConfirmationMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
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