package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.UIComponents;
import com.wac.autocore.view.booking.BookingDetailsView;
import com.wac.autocore.view.booking.BookingListView;
import com.wac.autocore.view.booking.BookingView;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private Booking originalBookingForCopy;

    private boolean fromCreateBookingWindow = false;

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
        bookingView.getCustomerComboBox().getItems().setAll(customerService.getAllCustomers());
        bookingView.getVehicleComboBox().getItems().setAll(vehicleService.getVehicles());

        wireEvents();
        refreshBookingList();

        //Alexander
        //rullistan "Välj tjänst" i Skapa bokning: först de enskilda tjänsterna, sedan servicepaketen.
        //Ett paket utan tjänster visas inte, eftersom det inte finns något att lägga till.
        bookingView.getServiceComboBox().getItems().addAll(serviceItemService.getServiceItems());
        for (ServicePackage servicePackage : servicePackageService.getServicePackages()) {
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

        bookingView.getCreateBookingButton().setOnAction(actionEvent -> createBooking());

        bookingListView.setOnCreateWorkOrder(booking -> openCreateWorkOrder(booking));
        bookingListView.setOnViewBooking(booking -> openBookingDetails(booking));
        bookingListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        bookingListView.setGetRegistrationNumber(vehicleId -> getRegistrationNumber(vehicleId));
        bookingView.getBackButton().setOnAction(actionEvent -> app.showBookingListView());
        bookingListView.getAddBookingButton().setOnAction(event -> app.showBookingView());
    }

    //Alexander
    //läser formuläret och låter servicen validera. Vid fel kastar servicen en exception
    //som visas via ErrorHandler - samma mönster som Customer och Vehicle.
    private void createBooking() {
        Vehicle selectedVehicle = bookingView.getVehicleComboBox().getValue();
        LocalDate date = bookingView.getDate().getValue();
        String description = bookingView.getDescriptionField().getText();
        List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

        //null om inget fordon är valt, servicen ger då rätt felmeddelande
        Integer vehicleId = selectedVehicle == null ? null : selectedVehicle.getId();

        int[] serviceItemIds = new int[selectedServices.size()];
        for (int i = 0; i < selectedServices.size(); i++) {
            serviceItemIds[i] = selectedServices.get(i).getId();
        }

        try {
            //Vanlig booking använder createBooking, bokning skapad av tidigare bokning använder
            //createBookingFromPreviousBooking. tjänsterna hämtas då från originalbokningen.
            if (originalBookingForCopy != null) {
                bookingService.createBookingFromPreviousBooking(originalBookingForCopy.getId(), date);
            } else {
                bookingService.createBooking(vehicleId, date, description, serviceItemIds);
            }

            refreshBookingList();
            bookingView.getVehicleComboBox().setValue(null);
            bookingView.getDate().setValue(null);
            bookingView.getDescriptionField().clear();
            bookingView.getServiceComboBox().setValue(null);
            bookingView.getServicesTable().getItems().clear();

            Booking booking;

            if (originalBookingForCopy != null) {
                booking = bookingService.createBookingFromPreviousBooking(
                        originalBookingForCopy.getId(), date);
            } else {
                booking = bookingService.createBooking(
                        vehicleId, date, description, serviceItemIds);
            }

            fromCreateBookingWindow = true;
            showBookingConfirmation(booking, fromCreateBookingWindow);

            app.showBookingListView();

        } catch (Exception e) {
            ErrorHandler.show(e);
        }
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

    private String getRegistrationNumber(int vehicleId) {
        Vehicle vehicle = vehicleService.findVehicle(vehicleId);

        if (vehicle != null) {
            return vehicle.getRegistrationNumber();
        }

        return "";
    }

    private void openCreateWorkOrder(Booking booking) {
        if (workOrderService.hasWorkOrder(booking.getId())) {
            showWarning(languageManager.getString("existingWorkOrder"));
            return;
        }

        app.showCreateWorkOrderView(booking);
    }

    private void openCreateBookingView(Booking booking) {
        originalBookingForCopy = booking;
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

                for (ServicePackage servicePackage : servicePackageService.getServicePackages()) {
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

            //Alexander
            //servicen kastar exception om en ändring inte är tillåten (t.ex. arbetet har påbörjats).
            //Då visas felet och bekräftelsen visas inte.
            try {
                //Lägg till nya tjänster
                for (ServiceItem service : editedServices) {
                    boolean exists = originalServices.stream()
                            .anyMatch(item -> item.getId() == service.getId());

                    if (!exists) {
                        bookingService.addServiceToBooking(booking.getId(), service.getId());
                    }
                }

                //Ta bort borttagna tjänster
                for (ServiceItem service : originalServices) {
                    boolean exists = editedServices.stream()
                            .anyMatch(item -> item.getId() == service.getId());

                    if (!exists) {
                        bookingService.removeServiceFromBooking(booking.getId(), service.getId());
                    }
                }

                fromCreateBookingWindow = false;
                showBookingConfirmation(booking, fromCreateBookingWindow);

                detailsView.showBookingDetails();

            } catch (Exception e) {
                ErrorHandler.show(e);
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
    }

    private void showWarning(String message) {
        Alert alert = UIComponents.createWarningAlert(Alert.AlertType.WARNING,
        message, languageManager.getString("headingAlertWarning"));
        alert.showAndWait();
    }

    private void showBookingConfirmation(Booking booking, boolean fromCreateBookingWindow) {

        String heading;

        if (!fromCreateBookingWindow)   {
             heading = languageManager.getString("confirmationChangedBookingHeading");
        }

        else  {
            heading = languageManager.getString("confirmationNewBookingHeading");
        }

        String subHeading = languageManager.getString("confirmationNewBookingSubHeading");

        Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());
        Customer customer = customerService.findCustomer(vehicle.getCustomerId());

        List<ServiceItem> services = bookingService.getServicesForBooking(booking.getId());

        String serviceNames = services.stream()
                .map(ServiceItem::getName)
                .collect(Collectors.joining(", "));

        Map<String, String> confirmationDetails = new LinkedHashMap<>();

        confirmationDetails.put(languageManager.getString("bookingIdField"),
                String.valueOf(booking.getId()));
        confirmationDetails.put(languageManager.getString("name"), customer.getName());
        confirmationDetails.put(languageManager.getString("registrationNumber"),
                vehicle.getRegistrationNumber());
        confirmationDetails.put(languageManager.getString("date"),
                String.valueOf(booking.getDate()));
        confirmationDetails.put(languageManager.getString("services"), serviceNames);
        confirmationDetails.put(languageManager.getString("estimatedTotalTime"),
                formatTotalTime(services));
        confirmationDetails.put(languageManager.getString("estimatedTotalPrice"),
                formatTotalPrice(services));

        Alert alert = UIComponents.createConfirmationAlert(heading, subHeading, confirmationDetails);
        alert.showAndWait();
    }

    public Parent getBookingFormView() {
        return bookingView.getView();
    }

    public Parent getBookingListPane() {
        return bookingListView.getView();
    }
}