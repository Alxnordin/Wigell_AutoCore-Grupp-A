package com.wac.autocore;

import com.wac.autocore.controller.*;
import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AutoCoreApplication extends Application {

    private final GarageSystem garageSystem = new GarageSystem();
    private BorderPane borderPane;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        //FREDRIK - lagt till
        DatabaseConnection.initializeDatabase();

        this.stage = primaryStage;
        stage.setTitle("Wigell AutoCore");

        borderPane = new BorderPane();

        Label headerLabel = new Label("WAC");
        HBox header = new HBox(headerLabel);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header");
        borderPane.setTop(header);

        Label footerLabel = new Label("Footer");
        HBox footer = new HBox(footerLabel);
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("footer");
        borderPane.setBottom(footer);

        new MainMenuController(garageSystem, this, borderPane);

        Scene scene = new Scene(borderPane, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }

    public void showMainMenu() {
        borderPane.setCenter(null);
    }

    public void showCustomerView() {
        borderPane.setCenter(new CustomerController(garageSystem, this, new CustomerView()).getView());
    }

    public void showVehicleView() {
        borderPane.setCenter(new VehicleController(garageSystem, this, new VehicleView()).getView());
    }

    public void showServiceView() {
        borderPane.setCenter(new ServiceController(garageSystem, this, new ServiceView()).getView());
    }

    public void showBookingView() {
        borderPane.setCenter(new BookingController(garageSystem, this, new BookingView()).getView());
    }

    public void showOrderView() {
       borderPane.setCenter(new OrderController(garageSystem, this, new OrderView()).getView());
    }

    public void showPaymentView() {
        borderPane.setCenter(new PaymentController(garageSystem, this, new PaymentView()).getView());
    }

    public void exitApplication() {
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}