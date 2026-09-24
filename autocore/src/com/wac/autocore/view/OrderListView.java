package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


//UI-vy som visar arbetsorder listan samt kontroller för att starta och slutföra en arbetsorder
public class OrderListView {

    private final Parent root;
    private final ListView<String> orderListView;

    private final TextField startOrderIdField;
    private final Button startOrderButton;

    private final TextField completeOrderIdField;
    private final Button completeOrderButton;

    private final Button backButton;

    public OrderListView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        orderListView = new ListView<>();

        Label startLabel = new Label("Starta arbetsorder");
        startOrderIdField = new TextField();
        startOrderIdField.setPromptText("Ange arbetsorder-ID");
        startOrderButton = new Button("Starta");

        Label completeLabel = new Label("Slutför arbetsorder");
        completeOrderIdField = new TextField();
        completeOrderIdField.setPromptText("Ange arbetsorder-ID");
        completeOrderButton = new Button("Slutför");

        backButton = new Button("Tillbaka");

        box.getChildren().addAll(orderListView,
                startLabel, startOrderIdField, startOrderButton,
                completeLabel, completeOrderIdField, completeOrderButton,
                backButton);
        this.root = box;
    }

    public Parent getView() {return root;}

    public ListView<String> getOrderListView() {return orderListView;}

    public TextField getStartOrderIdField() {return startOrderIdField;}
    public Button getStartOrderButton() {return startOrderButton;}

    public TextField getCompleteOrderIdField() {return completeOrderIdField;}
    public Button getCompleteOrderButton() {return completeOrderButton;}

    public Button getBackButton() {return backButton;}
}