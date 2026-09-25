package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.MechanicDAO;
import com.wac.autocore.dao.ServiceItemDAO;
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
    //FREDRIK - lagt till
    private final ServiceItemDAO serviceItemDAO = new ServiceItemDAO();
    private final MechanicDAO mechanicDAO = new MechanicDAO();


    public ServiceController(GarageSystem garageSystem, AutoCoreApplication app, ServiceView serviceView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.serviceView = serviceView;
        wireEvents();
        refreshLists();
    }

    public void refreshLists() {
        serviceView.getServiceItemListView().getItems().clear();
        //FREDRIK - ändrat
        for (ServiceItem item : serviceItemDAO.findAll()) {
            serviceView.getServiceItemListView().getItems().add(item.toString());
        }
        /*
        for (ServiceItem item : Database.getServiceItems()) {
            serviceView.getServiceItemListView().getItems().add(item.toString());
        }

         */

        serviceView.getMechanicListView().getItems().clear();
        for (Mechanic mechanic : mechanicDAO.findAll()) {
            serviceView.getMechanicListView().getItems().add(mechanic.toString());
        }
        /*
        for (Mechanic mechanic : Database.getMechanics()) {
            serviceView.getMechanicListView().getItems().add(mechanic.toString());
        }

         */
    }

    private void wireEvents() {
        serviceView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }
    public Parent getView() {
        return serviceView.getView();
    }
}
