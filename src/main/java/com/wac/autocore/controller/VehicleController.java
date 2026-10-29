package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.service.interf.VehicleService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.UIComponents;
import com.wac.autocore.view.vehicle.VehicleListView;
import com.wac.autocore.view.vehicle.VehicleView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class VehicleController {

    private final VehicleService vehicleService;

    private final AutoCoreApplication app;
    private final VehicleView vehicleView;
    private final VehicleListView vehicleListView;

    private final LanguageManager languageManager = LanguageManager.getInstance();
    private Consumer<Vehicle> onVehicleCreated;
    private Runnable onBack;

    public VehicleController(VehicleService vehicleService, CustomerService customerService,
                             AutoCoreApplication app, VehicleView vehicleView,
                             VehicleListView vehicleListView) {
        this.vehicleService = vehicleService;
        this.app = app;
        this.vehicleView = vehicleView;
        this.vehicleListView = vehicleListView;

        vehicleListView.setGetCustomerName(customerId -> {
            Customer customer = customerService.findCustomer(customerId);

            if (customer == null) {
                return "-";
            }
            return customer.getName();
        });

        wireEvents();
        refreshVehicleList();

        vehicleView.getCustomerComboBox().getItems().addAll(customerService.getAllCustomers());
    }

    private void wireEvents() {
        vehicleView.getCreateVehicleButton().setOnAction(actionEvent -> createVehicle());

        vehicleView.getBackButton().setOnAction(actionEvent -> {
                    if (onBack != null) {
                        onBack.run();
                        ;
                    } else {
                        app.showVehicleListView();
                    }
                });

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
            String customerName = "";
            if(selectedCustomer != null) {
                customerName = selectedCustomer.getName();
            }

            showConfirmation(vehicle, customerName);
            if (onVehicleCreated != null) {
                onVehicleCreated.accept(vehicle);
            } else {
                app.showVehicleListView();
            }

        } catch (Exception e) {
            ErrorHandler.show(e);
        }
    }

    // Hämtar fordonen och uppdaterar listan som sökningen filtrerar.
    private void refreshVehicleList() {
        vehicleListView.setVehicles(vehicleService.getVehicles());
    }

    private void showConfirmation(Vehicle vehicle, String customerName) {
        String heading = languageManager.getString("confirmationNewVehicleHeading");
        String subHeading = languageManager.getString("confirmationNewVehicleSubHeading");

        Map<String, String> confirmationDetails = new LinkedHashMap<>();
        confirmationDetails.put(languageManager.getString("name"), customerName);
        confirmationDetails.put("Reg. nr", vehicle.getRegistrationNumber());
        confirmationDetails.put(languageManager.getString("brandAndModel"), vehicle.getBrand() + " " + vehicle.getModel());
        confirmationDetails.put(languageManager.getString("vehicleYear"), String.valueOf(vehicle.getYear()));

        Alert alert = UIComponents.createConfirmationAlert(heading, subHeading, confirmationDetails);
        alert.showAndWait();
    }

    public Parent getVehicleFormView() {
        return vehicleView.getView();
    }

    public Parent getVehicleListPane() {
        return vehicleListView.getView();
    }

    public void setOnVehicleCreated(Consumer<Vehicle> action) {
        this.onVehicleCreated = action;
    }

    public void setOnBack(Runnable action) {
        this.onBack = action;
    }

    //Alexander
    //förväljer en kund i formuläret t.ex. den kund som redan är vald i bokningen
    public void preselectCustomer(Customer customer) {
        if (customer == null) {
            return;
        }
        for (Customer item : vehicleView.getCustomerComboBox().getItems()) {
            if (item.getId() == customer.getId()) {
                vehicleView.getCustomerComboBox().setValue(item);
                return;
            }
        }
    }
}