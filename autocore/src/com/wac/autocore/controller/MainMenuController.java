package com.wac.autocore.controller;



import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.MainMenuView;
import javafx.scene.layout.BorderPane;

public class MainMenuController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final BorderPane borderPane;
    private final MainMenuView view;


    public MainMenuController (GarageSystem garageSystem,AutoCoreApplication app, BorderPane borderPane) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.borderPane = borderPane;
        this.view = new MainMenuView();
        wireEvents();
        borderPane.setLeft(view.getView());
    }
    public void wireEvents() {
        view.getShowCustomersButton().setOnAction(e -> app.showCustomerView());
        view.getAddCustomerButton().setOnAction(e -> app.showCustomerView());

        view.getShowVehiclesButton().setOnAction(e -> app.showVehicleView());
        view.getAddVehicleButton().setOnAction(e -> app.showVehicleView());

        view.getShowBookingsButton().setOnAction(e -> app.showBookingView());
        view.getAddBookingButton().setOnAction(e -> app.showBookingView());

        view.getShowServicesButton().setOnAction(e -> app.showServiceView());
        view.getShowMechanicsButton().setOnAction(e -> app.showServiceView());

        view.getShowOrdersButton().setOnAction(e -> app.showOrderView());
        view.getAddOrderButton().setOnAction(e -> app.showOrderView());
        view.getStartCompleteOrderButton().setOnAction(e -> app.showOrderView());

        view.getShowInvoicesButton().setOnAction(e -> app.showPaymentView());
        view.getAddInvoiceButton().setOnAction(e -> app.showPaymentView());
        view.getShowPaymentsButton().setOnAction(e -> app.showPaymentView());
        view.getAddPaymentButton().setOnAction(e -> app.showPaymentView());

        view.getExitButton().setOnAction(actionEvent -> app.exitApplication());
    }
}
