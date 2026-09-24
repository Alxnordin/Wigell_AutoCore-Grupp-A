package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
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

    public ServiceController(GarageSystem garageSystem, AutoCoreApplication app,
                             ServiceItemView serviceItemView, MechanicView mechanicView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.serviceItemView = serviceItemView;
        this.mechanicView = mechanicView;
        wireEvents();
        refreshLists();
    }

    public void refreshLists() {
        serviceItemView.getServiceItemListView().getItems().clear();
        for (ServiceItem item : Database.getServiceItems()) {
            serviceItemView.getServiceItemListView().getItems().add(item.toString());
        }

        mechanicView.getMechanicListView().getItems().clear();
        for (Mechanic mechanic : Database.getMechanics()) {
            mechanicView.getMechanicListView().getItems().add(mechanic.toString());
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
                mechanicView.getMechanicWorkListView().getItems().add(workOrder.toString());
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