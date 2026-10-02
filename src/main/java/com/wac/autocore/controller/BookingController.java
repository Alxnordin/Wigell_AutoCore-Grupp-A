package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.BookingListView;
import com.wac.autocore.view.BookingView;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.util.List;

//Den klass som kopplar BookingView/BookingListView till GarageSystem samt hanterar skapande av bokningar och navigering.
public class BookingController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final BookingView bookingView;
    private final BookingListView bookingListView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public BookingController(GarageSystem garageSystem,AutoCoreApplication app,
                             BookingView bookingView,BookingListView bookingListView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.bookingView = bookingView;
        this.bookingListView = bookingListView;
        wireEvents();
        refreshBookingList();

        //Alexander
        //tjänsterna som kan läggas till i en befintlig bokning hämtas via GarageSystem
        bookingListView.getServiceComboBox().getItems().addAll(garageSystem.getServiceItems());

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshBookingList();
            updateNewBookingSummary();
        });
    }

    private void wireEvents() {
        // ===== Skapa bokning (BookingView) =====

        //Alexander
        // "Lägg till tjänst" lägger den valda tjänsten i tabellen med valda tjänster
        bookingView.getAddServiceButton().setOnAction(actionEvent -> addServiceToNewBooking());

        //Alexander
        //summeringen räknas om varje gång listan med valda tjänster ändras
        //(tjänst läggs till, tas bort med soptunnan eller listan töms efter en bokning)
        bookingView.getServicesTable().getItems().addListener(
                (ListChangeListener<ServiceItem>) change -> updateNewBookingSummary());

        bookingView.getCreateBookingButton().setOnAction(actionEvent -> {
            Vehicle selectedVehicle = bookingView.getVehicleComboBox().getValue();
            LocalDate date = bookingView.getDate().getValue();
            String description = bookingView.getDescriptionField().getText();
            //Alexander
            //tjänsterna som användaren har valt
            List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

            if (selectedVehicle == null) {
                showWarning(languageManager.getString("missingVehicleWarning"));
                return;
            }

            if (date == null) {
                showWarning(languageManager.getString("missingDateWarning"));
                return;
            }

            //Alexander
            //en bokning ska innehålla minst en tjänst
            if (selectedServices.isEmpty()) {
                showWarning(languageManager.getString("noServiceSelectedWarning"));
                return;
            }

            try {
                int vehicleId = selectedVehicle.getId();

                //Alexander
                //gör om de valda tjänsterna till deras ID:n, det är dem GarageSystem tar emot
                int[] serviceItemIds = new int[selectedServices.size()];
                for (int i = 0; i < selectedServices.size(); i++) {
                    serviceItemIds[i] = selectedServices.get(i).getId();
                }

                Booking booking = garageSystem.createBooking(vehicleId, date, description, serviceItemIds);

                if (booking != null) {
                    refreshBookingList();
                    bookingView.getVehicleComboBox().setValue(null);
                    bookingView.getDate().setValue(null);
                    bookingView.getDescriptionField().clear();
                    //Alexander
                    //töm de valda tjänsterna, summeringen nollställs då av lyssnaren ovan
                    bookingView.getServiceComboBox().setValue(null);
                    bookingView.getServicesTable().getItems().clear();
                }
            } catch (NumberFormatException e) {
                showWarning(languageManager.getString("invalidVehicleIdWarning"));
            }
        });

        // ===== Bokningslistan (BookingListView) =====

        //Alexander
        //när en bokning markeras visas dess tjänster och summering
        bookingListView.getBookingTable().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldBooking, newBooking) -> showServicesForSelectedBooking());

        bookingListView.getAddServiceButton().setOnAction(actionEvent -> addServiceToSelectedBooking());
        bookingListView.getRemoveServiceButton().setOnAction(actionEvent -> removeServiceFromSelectedBooking());

        //Alexander
        //knappen "Skapa arbetsorder" på varje bokning
        bookingListView.setOnCreateWorkOrder(booking -> openCreateWorkOrder(booking));

        bookingView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        bookingListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    //Alexander
    //lägger vald tjänst i listan för den nya bokningen (samma tjänst kan bara väljas en gång)
    private void addServiceToNewBooking() {
        ServiceItem selectedService = bookingView.getServiceComboBox().getValue();
        List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

        if (selectedService != null && !selectedServices.contains(selectedService)) {
            selectedServices.add(selectedService);
            bookingView.getServiceComboBox().setValue(null);
        }
    }

    //Alexander
    //total tid och totalt pris för de valda tjänsterna i den nya bokningen
    private void updateNewBookingSummary() {
        List<ServiceItem> selectedServices = bookingView.getServicesTable().getItems();

        bookingView.showSummary(formatTotalTime(selectedServices), formatTotalPrice(selectedServices));
    }

    //Alexander
    //visar tjänsterna och summeringen för den bokning som är markerad i bokningslistan
    private void showServicesForSelectedBooking() {
        Booking booking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();

        if (booking == null) {
            bookingListView.getBookingServicesTable().getItems().clear();
            bookingListView.showSummary("--", "--");
            bookingListView.setServiceEditingDisabled(true);
            bookingListView.setLockedMessageVisible(false);
            return;
        }

        List<ServiceItem> services = garageSystem.getServicesForBooking(booking.getId());
        bookingListView.getBookingServicesTable().getItems().setAll(services);
        bookingListView.showSummary(formatTotalTime(services), formatTotalPrice(services));

        //tjänsterna får bara ändras innan arbetet har påbörjats (krav 2)
        boolean workStarted = garageSystem.isWorkStarted(booking);
        bookingListView.setServiceEditingDisabled(workStarted);
        bookingListView.setLockedMessageVisible(workStarted);
    }

    //Alexander
    //lägg till en tjänst i en befintlig bokning
    private void addServiceToSelectedBooking() {
        Booking booking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();
        ServiceItem service = bookingListView.getServiceComboBox().getValue();

        if (booking == null || service == null) {
            showWarning(languageManager.getString("selectServiceToAddWarning"));
            return;
        }

        if (booking.containsServiceItem(service.getId())) {
            showWarning(languageManager.getString("serviceAlreadyInBookingWarning"));
            return;
        }

        //GarageSystem kontrollerar reglerna och sparar i databasen
        if (!garageSystem.addServiceToBooking(booking.getId(), service.getId())) {
            showWarning(languageManager.getString("serviceChangeFailedWarning"));
        }

        bookingListView.getServiceComboBox().setValue(null);
        refreshBookingList();
    }

    //Alexander
    //ta bort den markerade tjänsten från en befintlig bokning
    private void removeServiceFromSelectedBooking() {
        Booking booking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();
        ServiceItem service = bookingListView.getBookingServicesTable().getSelectionModel().getSelectedItem();

        if (booking == null || service == null) {
            showWarning(languageManager.getString("selectServiceToRemoveWarning"));
            return;
        }

        if (booking.getServiceItemIds().size() <= 1) {
            showWarning(languageManager.getString("lastServiceWarning"));
            return;
        }

        //GarageSystem kontrollerar reglerna och sparar i databasen
        if (!garageSystem.removeServiceFromBooking(booking.getId(), service.getId())) {
            showWarning(languageManager.getString("serviceChangeFailedWarning"));
        }

        refreshBookingList();
    }

    //Alexander
    //går till sidan "Skapa arbetsorder" med bokningen ifylld.
    //En bokning kan bara ha en arbetsorder, så finns det redan en visas en varning istället.
    private void openCreateWorkOrder(Booking booking) {
        if (garageSystem.hasWorkOrder(booking.getId())) {
            showWarning(languageManager.getString("existingWorkOrder"));
            return;
        }
        app.showOrderFormView(booking);
    }

    //Alexander
    //beräkningen görs i GarageSystem, här görs resultatet bara om till text
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
        return String.format("%,.0f kr", garageSystem.calculateTotalPrice(services));
    }

    //Alexander
    //bokningarna hämtas via GarageSystem, den bokning som var markerad markeras igen,
    //dess tjänster visas direkt efter att en tjänst lagts till eller tagits bort.
    private void refreshBookingList() {
        Booking selectedBooking = bookingListView.getBookingTable().getSelectionModel().getSelectedItem();

        bookingListView.getBookingTable().getItems().setAll(garageSystem.getBookings());

        if (selectedBooking != null) {
            for (Booking booking : bookingListView.getBookingTable().getItems()) {
                if (booking.getId() == selectedBooking.getId()) {
                    bookingListView.getBookingTable().getSelectionModel().select(booking);
                }
            }
        }
        showServicesForSelectedBooking();
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