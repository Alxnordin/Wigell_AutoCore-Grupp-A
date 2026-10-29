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

    private Integer selectedMechanicId;

    LanguageManager languageManager = LanguageManager.getInstance();

    public ServiceController(ServiceItemService serviceItemService, MechanicService mechanicService, WorkOrderService workOrderService, AutoCoreApplication app,
                             ServiceItemView serviceItemView, MechanicView mechanicView) {
        this.serviceItemService = serviceItemService;
        this.mechanicService = mechanicService;
        this.workOrderService = workOrderService;
        this.app = app;
        this.serviceItemView = serviceItemView;
        this.mechanicView = mechanicView;
        this.mechanicView.setServiceNamesProvider(this::getServiceNames);
        wireEvents();
        refreshLists();

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // Uppdaterar tabellerna efter språkbytet.
                    refreshLists();

                    // Återställer arbetet utifrån det sparade mekaniker-ID:t.
                    if (selectedMechanicId != null) {
                        mechanicService.getMechanics().stream()
                                .filter(mechanic ->
                                        mechanic.getId() == selectedMechanicId)
                                .findFirst()
                                .ifPresent(this::showMechanicWork);
                    }
                }
        );
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
    }

    private void wireEvents() {
        // Sparar mekanikerns ID och visar dennes arbetsordrar.
        mechanicView.getMechanicListView()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldMechanic, selectedMechanic) -> {
                    if (selectedMechanic != null) {
                        selectedMechanicId = selectedMechanic.getId();
                        showMechanicWork(selectedMechanic);
                    }
                });

        // Tillbaka från tjänster går till serviceöversikten.
        serviceItemView.getBackButton().setOnAction(
                event -> app.showServiceOverviewView());

        // Tillbaka från mekaniker går till serviceöversikten.
        mechanicView.getBackButton().setOnAction(
                event -> app.showServiceOverviewView());

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

    // Hämtar tjänsternas namn utifrån arbetsorderns tjänste-ID:n.
    public String getServiceNames(WorkOrder workOrder) {
        return workOrder.getServiceItemIds().stream()
                .map(serviceId -> serviceItemService.getServiceItems().stream()
                        .filter(service -> service.getId() == serviceId)
                        .map(ServiceItem::getName)
                        .findFirst()
                        .orElse("ID: " + serviceId))
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private void showMechanicWork(Mechanic selectedMechanic) {
        mechanicView.getMechanicWorkTable().getItems().clear();

        // Avslutar om ingen mekaniker är vald.
        if (selectedMechanic == null) {
            return;
        }

        for (WorkOrder workOrder : workOrderService.findAllWorkOrders()) {
            if (workOrder.getMechanicId() == selectedMechanic.getId()) {
                mechanicView.getMechanicWorkTable().getItems().add(workOrder);
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