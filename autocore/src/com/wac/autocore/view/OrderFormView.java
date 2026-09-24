package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

//UI-vy med Formulär för att skapa en ny arbetsorder (kopplad till en bokning, mekaniker och service delarna)
public class OrderFormView {

    private final Parent root;

    private final TextField bookingIdField;
    private final TextField mechanicIdField;
    private final TextField serviceItemIdsField;
    private final Button createOrderButton;

    private final Button backButton;

    public OrderFormView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label createLabel = new Label("Skapa arbetsorder");
        bookingIdField = new TextField();
        bookingIdField.setPromptText("Välj bokning, Ange boknings-ID");

        mechanicIdField = new TextField();
        mechanicIdField.setPromptText("Tilldela mekaniker, Ange mekaniker-ID");

        serviceItemIdsField = new TextField();
        serviceItemIdsField.setPromptText("Välj service, Ange service-ID");

        createOrderButton = new Button("Skapa arbetsorder");

        backButton = new Button("Tillbaka");

        box.getChildren().addAll(createLabel, bookingIdField, mechanicIdField,
                serviceItemIdsField, createOrderButton, backButton);

        this.root = box;
    }

    public Parent getView() {return root;}

    public TextField getBookingIdField() {return bookingIdField;}
    public TextField getMechanicIdField() {return mechanicIdField;}
    public TextField getServiceItemIdsField() {return serviceItemIdsField;}
    public Button getCreateOrderButton() {return createOrderButton;}

    public Button getBackButton() {return backButton;}
}