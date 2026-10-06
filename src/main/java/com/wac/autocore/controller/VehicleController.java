package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.vehicle.VehicleListView;
import com.wac.autocore.view.vehicle.VehicleView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.text.MessageFormat;

//Kopplar VehicleView/VehicleListView till GarageSystem — hanterar skapande och visning av fordon samt navigering.
public class VehicleController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final VehicleView vehicleView;
    private final VehicleListView vehicleListView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public VehicleController(GarageSystem garageSystem, AutoCoreApplication app,
                             VehicleView vehicleView, VehicleListView vehicleListView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.vehicleView = vehicleView;
        this.vehicleListView = vehicleListView;
        wireEvents();
        refreshVehicleList();

        //kunderna som kan väljas i formuläret hämtas via GarageSystem
        vehicleView.getCustomerComboBox().getItems().addAll(garageSystem.getCustomers());
    }

    private void wireEvents() {
        vehicleView.getCreateVehicleButton().setOnAction(actionEvent -> createVehicle());

        vehicleView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        vehicleListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    //kontrollerar formuläret och skapar fordonet via GarageSystem
    private void createVehicle() {
        Customer selectedCustomer = vehicleView.getCustomerComboBox().getValue();
        String registrationNumber = vehicleView.getRegistrationNumberField().getText().trim();
        String brand = vehicleView.getBrandField().getText().trim();
        String model = vehicleView.getModelField().getText().trim();
        String yearText = vehicleView.getYearField().getText().trim();

        if (selectedCustomer == null) {
            showWarning(languageManager.getString("missingCustomerWarning"));
            return;
        }

        if (registrationNumber.isEmpty() || brand.isEmpty() || model.isEmpty()) {
            showWarning(languageManager.getString("missingVehicleDetailsWarning"));
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            showWarning(languageManager.getString("invalidYearWarning"));
            return;
        }

        Vehicle vehicle = garageSystem.createVehicle(registrationNumber, brand, model, year, selectedCustomer.getId());

        if (vehicle == null) {
            showWarning(languageManager.getString("vehicleNotCreatedWarning"));
            return;
        }

        refreshVehicleList();
        vehicleView.getCustomerComboBox().setValue(null);
        vehicleView.getRegistrationNumberField().clear();
        vehicleView.getBrandField().clear();
        vehicleView.getModelField().clear();
        vehicleView.getYearField().clear();

        //listan ligger på en annan sida, så användaren får en bekräftelse här
        showInformation(MessageFormat.format(languageManager.getString("vehicleCreatedInfo"), registrationNumber));
    }

    //fordonen hämtas via GarageSystem
    private void refreshVehicleList() {
        vehicleListView.getVehicleTable().getItems().setAll(garageSystem.getVehicles());
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setHeaderText(null);
        alert.showAndWait();
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