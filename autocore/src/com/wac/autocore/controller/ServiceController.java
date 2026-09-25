package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.MechanicView;
import com.wac.autocore.view.ServiceItemView;
import javafx.scene.Parent;


//Kopplar ServiceItemView/MechanicView till GarageSystem — visar tjänster, mekaniker och vald mekanikers tilldelade arbete
public class ServiceController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final ServiceItemView serviceItemView;
    private final MechanicView mechanicView;

    LanguageManager languageManager = LanguageManager.getInstance();

    public ServiceController(GarageSystem garageSystem, AutoCoreApplication app,
                             ServiceItemView serviceItemView, MechanicView mechanicView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.serviceItemView = serviceItemView;
        this.mechanicView = mechanicView;
        wireEvents();
        refreshLists();

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshLists();
        });
    }

    public void refreshLists() {
        serviceItemView.getServiceItemListView().getItems().clear();
        for (ServiceItem item : Database.getServiceItems()) {

            String serviceItemInfo =
                    item.getId() + " | "
                            + languageManager.getString("serviceItemName")
                            + ": " + item.getName() + " | "
                            + languageManager.getString("serviceItemDescription")
                            + ": " + item.getDescription() + " | "
                            + languageManager.getString("serviceItemPrice")
                            + ": " + item.getPrice() + " | "
                            + languageManager.getString("serviceItemEstimatedMinutes")
                            + ": " + item.getEstimatedMinutes() + " | ";

            serviceItemView.getServiceItemListView().getItems().add(serviceItemInfo);

        }

        mechanicView.getMechanicListView().getItems().clear();
        for (Mechanic mechanic : Database.getMechanics()) {

            String bookedYesOrNo;

            if (mechanic.isAvailable()) {
                bookedYesOrNo = languageManager.getString("availableYes");
            } else {
                bookedYesOrNo = languageManager.getString("availableNo");
            }

            String mechanicInfo =
                    mechanic.getId() + " | "
                            + languageManager.getString("mechanicName")
                            + ": " + mechanic.getName() + " | "
                            + languageManager.getString("mechanicPhone")
                            + ": " + mechanic.getPhone() + " | "
                            + languageManager.getString("mechanicSpecialization")
                            + ": " + mechanic.getSpecialization() + " | "
                            + languageManager.getString("available")
                            + ": " + bookedYesOrNo + " | ";

            mechanicView.getMechanicListView().getItems().add(mechanicInfo);

        }

        mechanicView.getMechanicWorkListView().getItems().clear();
    }

    private void wireEvents() {
        mechanicView.getMechanicListView().getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldIndex, newIndex) -> showMechanicWork(newIndex.intValue()));

        serviceItemView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        mechanicView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private void showMechanicWork(int selectedIndex) {
        mechanicView.getMechanicWorkListView().getItems().clear();

        if (selectedIndex < 0 || selectedIndex >= Database.getMechanics().size()) {
            return;
        }

        Mechanic selectedMechanic = Database.getMechanics().get(selectedIndex);

        for (WorkOrder workOrder : Database.getWorkOrders()) {
            if (workOrder.getMechanicId() == selectedMechanic.getId()) {

                String workOrderStatus;

                if (workOrder.getStatus().equals("CREATED")) {
                    workOrderStatus = languageManager.getString("statusCreated");

                } else if (workOrder.getStatus().equals("IN_PROGRESS")) {
                    workOrderStatus = languageManager.getString("statusInProgress");

                } else if (workOrder.getStatus().equals("COMPLETED")) {
                    workOrderStatus = languageManager.getString("statusCompleted");

                } else {
                    workOrderStatus = workOrder.getStatus();
                }

                String workOrderInfo =
                        workOrder.getId() + " | "
                                + languageManager.getString("workOrderBookingId")
                                + ": " + workOrder.getBookingId() + " | "
                                + languageManager.getString("workOrderMechanicId")
                                + ": " + workOrder.getMechanicId() + " | "
                                + languageManager.getString("workOrderServices")
                                + ": " + workOrder.getServiceItemIds() + " | "
                                + languageManager.getString("workOrderStatus")
                                + ": " + workOrderStatus;

                mechanicView.getMechanicWorkListView().getItems().add(workOrderInfo);
            }
        }
    }

    public Parent getServiceItemView() {
        return serviceItemView.getView();
    }

    public Parent getMechanicView() {
        return mechanicView.getView();
    }
}