package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.WorkOrderDAO;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.OrderView;
import javafx.scene.Parent;

public class OrderController {

    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final OrderView orderView;
    //FREDRIK - lagt till
    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();

    public OrderController(GarageSystem garageSystem,AutoCoreApplication app,OrderView orderView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.orderView = orderView;
        wireEvents();
        refreshOrderList();
    }

    private void wireEvents() {
        orderView.getCreateOrderButton().setOnAction(actionEvent -> {
            try {
                int bookingId = Integer.parseInt(orderView.getBookingIdField().getText());
                int mechanicId = Integer.parseInt(orderView.getMechanicIdField().getText());
                int[] serviceItemIds = parseServiceItemIds(orderView.getServiceItemIdsField().getText());

                WorkOrder workOrder = garageSystem.createWorkOrder(bookingId, mechanicId, serviceItemIds);

                if (workOrder != null) {
                    refreshOrderList();
                    orderView.getBookingIdField().clear();
                    orderView.getMechanicIdField().clear();
                    orderView.getServiceItemIdsField().clear();
                }
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt ID — booking-ID, mekaniker-ID och service-ID:n måste vara heltal.");
            }
        });

        orderView.getStartOrderButton().setOnAction(actionEvent -> {
            try {
                int workOrderId = Integer.parseInt(orderView.getStartOrderIdField().getText());
                garageSystem.startWorkOrder(workOrderId);
                refreshOrderList();
                orderView.getStartOrderIdField().clear();
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt arbetsorder-ID — måste vara ett heltal.");
            }
        });

        orderView.getCompleteOrderButton().setOnAction(actionEvent -> {
            try {
                int workOrderId = Integer.parseInt(orderView.getCompleteOrderIdField().getText());
                garageSystem.completeWorkOrder(workOrderId);
                refreshOrderList();
                orderView.getCompleteOrderIdField().clear();
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt arbetsorder-ID — måste vara ett heltal.");
            }
        });

        orderView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private int[] parseServiceItemIds(String text) {
        String[] parts = text.split(",");
        int[] ids = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }

        return ids;
    }

    private void refreshOrderList() {
        orderView.getOrderListView().getItems().clear();

        //FREDRIK - Ändrat
        for (WorkOrder workOrder : workOrderDAO.findAll()) {
            orderView.getOrderListView().getItems().add(workOrder.toString());
        }
        /*
        for (WorkOrder workOrder : Database.getWorkOrders()) {
            orderView.getOrderListView().getItems().add(workOrder.toString());
        }

         */
    }

    public Parent getView() {
        return orderView.getView();
    }
}

