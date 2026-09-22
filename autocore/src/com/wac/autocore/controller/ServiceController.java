package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.ServiceView;
import javafx.scene.Parent;

public class ServiceController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final ServiceView serviceView;


    public ServiceController(GarageSystem garageSystem, AutoCoreApplication app, ServiceView serviceView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.serviceView = serviceView;
        wireEvents();
        refreshLists();
    }

    public void refreshLists() {
        serviceView.getServiceItemListView().getItems().clear();
        for (ServiceItem item : Database.getServiceItems()) {
            serviceView.getServiceItemListView().getItems().add(item.toString());
        }

        serviceView.getMechanicListView().getItems().clear();
        for (Mechanic mechanic : Database.getMechanics()) {
            serviceView.getMechanicListView().getItems().add(mechanic.toString());
        }
    }

    private void wireEvents() {
        serviceView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }
    public Parent getView() {
        return serviceView.getView();
    }
}
