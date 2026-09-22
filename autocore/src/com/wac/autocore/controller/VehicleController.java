package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.VehicleView;
import javafx.scene.Parent;

public class VehicleController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    public final VehicleView vehicleView;

    public VehicleController(GarageSystem garageSystem, AutoCoreApplication app, VehicleView vehicleView){
        this.garageSystem = garageSystem;
        this.app = app;
        this.vehicleView = vehicleView;
        wireEvents();
        refreshVehicleList();
    }

    public void wireEvents() {
        vehicleView.getCreateVehicleButton().setOnAction(actionEvent -> {
                String registerNumber = vehicleView.getRegistrationNumberField().getText();
                String brand = vehicleView.getBrandField().getText();
                String model = vehicleView.getModelField().getText();
                String yearText = vehicleView.getYearField().getText();
                String customerIdText = vehicleView.getCustomerIdField().getText();

                try {
                    int year = Integer.parseInt(yearText);
                    int customerId = Integer.parseInt(customerIdText);

                    Vehicle vehicle = garageSystem.createVehicle(registerNumber, brand, model, year,customerId);

                    if (vehicle != null) {
                        refreshVehicleList();
                        vehicleView.getRegistrationNumberField().clear();
                        vehicleView.getBrandField().clear();
                        vehicleView.getModelField().clear();
                        vehicleView.getYearField().clear();
                        vehicleView.getCustomerIdField().clear();
                    }
                }catch (NumberFormatException e) {
                    System.out.println("Ogiltigt år eller kund-ID måste vara ett heltal.");
                }
        });

        vehicleView.getBackButton().setOnAction(actionEvent ->
                app.showMainMenu());

    }

    private void refreshVehicleList() {
    vehicleView.getVehicleListView().getItems().clear();

    for (Vehicle vehicle : Database.getVehicles()) {
        vehicleView.getVehicleListView().getItems().add(vehicle.toString());
    }
}

public Parent getView() {
        return vehicleView.getView();
    }
}
