package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class OrderView {

    private final Parent root;

    //Skapa arbetsorder
    private TextField bookingIdField;
    private TextField mechanicIdField;
    private TextField serviceItemIdsField;
    private Button createOrderButton;


    //Starta arbetsorder
    private TextField startOrderIdField;
    private Button startOrderButton;

    //Slutför arbetsorder
    private TextField completeOrderIdField;
    private Button completeOrderButton;

    private ListView<String> orderListView;
    private Button backButton;

    public OrderView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label createLabel = new Label("Skapa arbetsorder");
        bookingIdField = new TextField();
        bookingIdField.setPromptText("Boknings-ID");
        mechanicIdField = new TextField();
        mechanicIdField.setPromptText("Mekaniker-ID");
        serviceItemIdsField = new TextField();
        serviceItemIdsField.setPromptText("Service-ID:n");
        createOrderButton = new Button("Skapa arbetsorder");

        Label startLabel = new Label("Starta arbetsorder");
        startOrderIdField = new TextField();
        startOrderIdField.setPromptText("Arbetsorder-ID");
        startOrderButton = new Button("Starta");

        Label completeLabel = new Label("Slutför arbetsorder");
        completeOrderIdField = new TextField();
        completeOrderIdField.setPromptText("Arbetsorder-ID");
        completeOrderButton = new Button("Slutför");

        orderListView = new ListView<>();
        backButton = new Button("Tillbaka");

        box.getChildren().addAll(createLabel,bookingIdField,mechanicIdField,
                serviceItemIdsField,createOrderButton,startLabel,startOrderIdField,
                startOrderButton,completeLabel,completeOrderIdField,completeOrderButton,
                orderListView,backButton
        );

        this.root = box;
    }

    public Parent getView() {return root;}

    public TextField getBookingIdField() {return bookingIdField;}
    public TextField getMechanicIdField() {return mechanicIdField;}
    public TextField getServiceItemIdsField() {return serviceItemIdsField;}
    public Button getCreateOrderButton() {return createOrderButton;}

    public TextField getStartOrderIdField() {return startOrderIdField;}
    public Button getStartOrderButton() {return startOrderButton;}

    public TextField getCompleteOrderIdField() {return completeOrderIdField;}
    public Button getCompleteOrderButton() {return completeOrderButton;}

    public ListView<String> getOrderListView() {return orderListView;}
    public Button getBackButton() {return backButton;}
}
