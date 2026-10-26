package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.service.interf.VehicleService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.vehicle.VehicleListView;
import com.wac.autocore.view.vehicle.VehicleView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.text.MessageFormat;
import java.util.List;


public class VehicleController {

    private final VehicleService vehicleService;
    private final CustomerService customerService;

    private final AutoCoreApplication app;
    private final VehicleView vehicleView;
    private final VehicleListView vehicleListView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public VehicleController(VehicleService vehicleService, CustomerService customerService,
                             AutoCoreApplication app, VehicleView vehicleView,
                             VehicleListView vehicleListView) {
        this.customerService = customerService;
        this.vehicleService = vehicleService;
        this.app = app;
        this.vehicleView = vehicleView;
        this.vehicleListView = vehicleListView;
        wireEvents();
        refreshVehicleList();

        vehicleView.getCustomerComboBox().getItems().addAll(customerService.getAllCustomers());
    }

    private void wireEvents() {
        vehicleView.getCreateVehicleButton().setOnAction(actionEvent -> createVehicle());

        vehicleView.getBackButton().setOnAction(actionEvent -> app.showVehicleListView());
        vehicleListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        // Öppnar formuläret när användaren klickar på Lägg till fordon.
        vehicleListView.getCreateVehicleButton().setOnAction(
                actionEvent -> app.showVehicleView());

    }

    //Alexander
    //Controllern läser bara formuläret och anropar servicen.
    //Kontrollerna ligger nu i VehicleServiceImpl, och fel visas via ErrorHandler.
    private void createVehicle() {
        Customer selectedCustomer = vehicleView.getCustomerComboBox().getValue();
        Integer customerId = selectedCustomer != null ? selectedCustomer.getId() : null;

        try {
            Vehicle vehicle = vehicleService.createVehicle(
                    vehicleView.getRegistrationNumberField().getText(),
                    vehicleView.getBrandField().getText(),
                    vehicleView.getModelField().getText(),
                    vehicleView.getYearField().getText(),
                    customerId);

            refreshVehicleList();
            vehicleView.getCustomerComboBox().setValue(null);
            vehicleView.getRegistrationNumberField().clear();
            vehicleView.getBrandField().clear();
            vehicleView.getModelField().clear();
            vehicleView.getYearField().clear();

            //listan ligger på en annan sida, så användaren får en bekräftelse här
            showInformation(MessageFormat.format(languageManager.getString("vehicleCreatedInfo"),
                    vehicle.getRegistrationNumber()));

        } catch (Exception e) {
            ErrorHandler.show(e);
        }
    }

    // Hämtar fordonen och uppdaterar listan som sökningen filtrerar.
    private void refreshVehicleList() {
        vehicleListView.setVehicles(vehicleService.getVehicles());
    }


    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public Parent getVehicleFormView() {
        return vehicleView.getView();
    }

    public Parent getVehicleListPane() {
        return vehicleListView.getView();
    }
}