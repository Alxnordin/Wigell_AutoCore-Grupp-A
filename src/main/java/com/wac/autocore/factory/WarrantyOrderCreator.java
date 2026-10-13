package com.wac.autocore.factory;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderType;
import com.wac.autocore.service.impl.WorkOrderService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.order.CreateWorkOrderView;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.stream.Collectors;

public class WarrantyOrderCreator implements WorkOrderCreator{
    private final WorkOrderService workOrderService;
    private final LanguageManager languageManager =
            LanguageManager.getInstance();

    public WarrantyOrderCreator(WorkOrderService workOrderService) {
        // Återanvänder projektets befintliga service.
        this.workOrderService = workOrderService;
    }

    @Override
    public WorkOrderType getType() {
        return WorkOrderType.WARRANTY;
    }


    @Override
    public void configureView(CreateWorkOrderView view) {
        // Hämtar endast slutförda arbetsordrar som kan reklameras.
        List<WorkOrder> completedWorkOrders = workOrderService
                .findAllWorkOrders()
                .stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .collect(Collectors.toList());

        // Visar de möjliga originalordrarna i formuläret.
        view.getOriginalWorkOrderComboBox()
                .getItems()
                .setAll(completedWorkOrders);
    }


    @Override
    public WorkOrder createOrder(CreateWorkOrderView view) {
        // Hämtar den ursprungliga arbetsordern som reklamationen gäller.
        WorkOrder originalOrder =
                view.getOriginalWorkOrderComboBox().getValue();

        if (originalOrder == null) {
            showWarning("selectOriginalWorkOrderWarning");
            return null;
        }

        // Skapar reklamationen via den befintliga servicelogiken.
        WorkOrder warrantyOrder = workOrderService.createComplaint(
                originalOrder.getId(),
                view.getWarrantyDescriptionField().getText()
        );

        if (warrantyOrder == null) {
            showWarning("workOrderNotCreatedWarning");
        }

        return warrantyOrder;
    }

    private void showWarning(String messageKey) {
        // Hämtar varningstexten från den aktiva språkfilen.
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                languageManager.getString(messageKey)
        );
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
