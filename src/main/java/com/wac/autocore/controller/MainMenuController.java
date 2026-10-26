package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.view.MainMenuView;
import javafx.scene.layout.BorderPane;


//Bygger och visar huvudmenyn, kopplar menyknapparna till respektive vy via AutoCoreApplication.
public class MainMenuController {
    private final AutoCoreApplication app;
    private final BorderPane borderPane;
    private final MainMenuView view;

    public MainMenuController (AutoCoreApplication app, BorderPane borderPane) {
        this.app = app;
        this.borderPane = borderPane;
        this.view = new MainMenuView();
        wireEvents();
        borderPane.setLeft(view.getView());
    }

    public void wireEvents() {
        view.getCustomersButton().setOnAction(event -> app.showCustomerListView());

        view.getVehiclesButton().setOnAction(event -> app.showVehicleListView());
//        view.getShowVehiclesButton().setOnAction(e -> app.showVehicleListView());
//        view.getAddVehicleButton().setOnAction(e -> app.showVehicleView());

        view.getBookingsButton().setOnAction(event -> app.showBookingListView());
//        view.getShowBookingsButton().setOnAction(e -> app.showBookingListView());
//        view.getAddBookingButton().setOnAction(e -> app.showBookingView());

        view.getShowServicesButton().setOnAction(e -> app.showServiceItemView());
        view.getShowMechanicsButton().setOnAction(e -> app.showMechanicView());

        view.getOrdersButton().setOnAction(e -> app.showOrderListView());

        view.getPaymentsButton().setOnAction(event -> app.showInvoiceListView());

//        view.getShowPaymentsButton().setOnAction(e -> app.showPaymentView());
//        view.getAddPaymentButton().setOnAction(e -> app.showPaymentView());

        view.getLogoutButton().setOnAction(e -> app.logout());

        view.getExitButton().setOnAction(actionEvent -> app.exitApplication());
    }
}
