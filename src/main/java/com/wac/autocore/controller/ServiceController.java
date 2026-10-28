package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.interf.MechanicService;
import com.wac.autocore.service.interf.ServiceItemService;
import com.wac.autocore.service.interf.WorkOrderService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.UIComponents;
import com.wac.autocore.view.mechanic.MechanicView;
import com.wac.autocore.view.service.ServiceItemView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceController {

    private final ServiceItemService serviceItemService;
    private final MechanicService mechanicService;
    private final WorkOrderService workOrderService;

    private final AutoCoreApplication app;
    private final ServiceItemView serviceItemView;
    private final MechanicView mechanicView;

    LanguageManager languageManager = LanguageManager.getInstance();

    public ServiceController(ServiceItemService serviceItemService, MechanicService mechanicService, WorkOrderService workOrderService, AutoCoreApplication app,
                             ServiceItemView serviceItemView, MechanicView mechanicView) {
        this.serviceItemService = serviceItemService;
        this.mechanicService = mechanicService;
        this.workOrderService = workOrderService;
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

        serviceItemView.getServiceItemTableView().getItems().clear();

        for (ServiceItem item : serviceItemService.getServiceItems())   {
                serviceItemView.getServiceItemTableView().getItems().add(item);
        }

        // Fyller tabellen med mekanikerobjekt i stället för formaterade textrader.
        mechanicView.getMechanicListView().getItems().setAll(
                mechanicService.getMechanics()
        );

        mechanicView.getMechanicWorkListView().getItems().clear();
    }

    private void wireEvents() {
        // Hämtar den valda mekanikern direkt från tabellen.
        mechanicView.getMechanicListView()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldMechanic, selectedMechanic) ->
                        showMechanicWork(selectedMechanic)
                );

        serviceItemView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        mechanicView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        serviceItemView.setOnChangePrice(serviceItem -> {
//            TextInputDialog dialog = new TextInputDialog(String.valueOf(serviceItem.getPrice()));
//            dialog.setTitle(languageManager.getString("changePrice"));
//            dialog.setHeaderText(serviceItem.getName());
//            dialog.setContentText(languageManager.getString("newPrice"));
            TextInputDialog dialog = UIComponents.createPriceInputDialog(
                    languageManager.getString("changePrice"), serviceItem.getName(),
                    languageManager.getString("newPrice"), String.valueOf(serviceItem.getPrice()));

            //Alexander
            //servicen tolkar och kontrollerar priset. Vid fel visas orsaken via ErrorHandler
            //(tidigare skrevs felet bara ut i konsolen och användaren såg ingenting)
            dialog.showAndWait().ifPresent(input -> {
                try {
                    serviceItemService.changeServicePrice(serviceItem.getId(), input);
                    refreshLists();

                    String oldPrice = serviceItemView.getPrice(serviceItem);
                    showConfirmation(serviceItem, oldPrice);

                } catch (Exception e) {
                    ErrorHandler.show(e);
                }
            });
        });
    }

    private void showMechanicWork(Mechanic selectedMechanic) {
        mechanicView.getMechanicWorkListView().getItems().clear();

        // Avslutar om ingen mekaniker är vald.
        if (selectedMechanic == null) {
            return;
        }

        for (WorkOrder workOrder : workOrderService.findAllWorkOrders()) {
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

    private void showConfirmation(ServiceItem serviceItem, String oldPrice) {
        String header = languageManager.getString("confirmationPriceChangedHeader");
        String subheader = languageManager.getString("confirmationPriceChangedSubheader");

        Map<String, String> confirmationDetails = new LinkedHashMap<>();
        confirmationDetails.put(languageManager.getString("service"), serviceItem.getName());
        confirmationDetails.put(languageManager.getString("oldPrice"), oldPrice);
        confirmationDetails.put(languageManager.getString("newPrice"), String.valueOf(serviceItem.getPrice()));

        Alert alert = UIComponents.createConfirmationAlert(header, subheader, confirmationDetails);
        alert.showAndWait();
    }

    public Parent getServiceItemView() {
        return serviceItemView.getView();
    }

    public Parent getMechanicView() {
        return mechanicView.getView();
    }
}