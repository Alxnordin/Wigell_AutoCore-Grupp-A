package com.wac.autocore.factory;

import com.wac.autocore.model.*;
import com.wac.autocore.service.interf.WorkOrderService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.order.CreateWorkOrderView;
import javafx.scene.control.Alert;

public class DropInOrderCreator implements WorkOrderCreator{
    private final WorkOrderService workOrderService;
    private final LanguageManager languageManager =
            LanguageManager.getInstance();

    public DropInOrderCreator(WorkOrderService workOrderService) {
        // Återanvänder projektets befintliga service.
        this.workOrderService = workOrderService;
    }

    @Override
    public WorkOrderType getType() {
        // Anger vilken ordertyp fabriken kopplar till skaparen.
        return WorkOrderType.DROP_IN;
    }

    @Override
    public void configureView(CreateWorkOrderView view) {
        // Drop-in kräver ingen bokning, så formuläret behöver ingen bokningslista.
    }

    @Override
    public WorkOrder createOrder(CreateWorkOrderView view) {
        // Hämtar kund, fordon och mekaniker från formuläret.
        Customer customer = view.getCustomerComboBox().getValue();
        Vehicle vehicle = view.getVehicleComboBox().getValue();
        Mechanic mechanic = view.getMechanicComboBox().getValue();

        // Hämtar ID för de valda tjänsterna.
        int[] serviceItemIds = view.getServicesTable()
                .getItems()
                .stream()
                .mapToInt(ServiceItem::getId)
                .toArray();

        // Kontrollerar att kund och fordon har valts.
        if (customer == null || vehicle == null) {
            showWarning("selectCustomerAndVehicleWarning");
            return null;
        }

        // Säkerställer att fordonet tillhör den valda kunden.
        if (vehicle.getCustomerId() != customer.getId()) {
            showWarning("vehicleNotOwnedByCustomerWarning");
            return null;
        }

        if (mechanic == null) {
            showWarning("selectMechanicWarning");
            return null;
        }

        if (serviceItemIds.length == 0) {
            showWarning("noServiceSelectedWarning");
            return null;
        }

        // Anropar servicelagret för att skapa drop-in-arbetsordern.
        WorkOrder workOrder = workOrderService.createDropInWorkOrder(
                customer.getId(),
                vehicle.getId(),
                mechanic.getId(),
                serviceItemIds
        );

        if (workOrder == null) {
            showWarning("workOrderNotCreatedWarning");
        }

        return workOrder;
    }

    private void showWarning(String messageKey) {
        // Visar varningen på det språk som användaren valt.
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                languageManager.getString(messageKey)
        );

        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
