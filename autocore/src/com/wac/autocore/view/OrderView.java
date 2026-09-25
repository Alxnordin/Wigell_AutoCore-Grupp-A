package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

//SKA DEN HÄR KLASSEN TAS BORT?
public class OrderView {

    private final Parent root;

    //Skapa arbetsorder
    private Label createLabel;
    private TextField bookingIdField;
    private TextField mechanicIdField;
    private TextField serviceItemIdsField;
    private Button createOrderButton;


    //Starta arbetsorder
    private Label startLabel;
    private TextField startOrderIdField;
    private Button startOrderButton;

    //Slutför arbetsorder
    private Label completeLabel;
    private TextField completeOrderIdField;
    private Button completeOrderButton;

    private ListView<String> orderListView;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public OrderView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        createLabel = new Label(languageManager.getString("createOrderButton"));
        bookingIdField = new TextField();
        bookingIdField.setPromptText(languageManager.getString("bookingIdField"));
        mechanicIdField = new TextField();
        mechanicIdField.setPromptText(languageManager.getString("mechanicIdField"));
        serviceItemIdsField = new TextField();
        serviceItemIdsField.setPromptText(languageManager.getString("serviceItemIdsField"));
        createOrderButton = new Button(languageManager.getString("createOrderButton"));

        startLabel = new Label(languageManager.getString("startOrder"));
        startOrderIdField = new TextField();
        startOrderIdField.setPromptText(languageManager.getString("startOrderIdField"));
        startOrderButton = new Button(languageManager.getString("startOrderButton"));

        completeLabel = new Label(languageManager.getString("completeOrder"));
        completeOrderIdField = new TextField();
        completeOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));
        completeOrderButton = new Button(languageManager.getString("completeOrderButton"));

        orderListView = new ListView<>();
        backButton = new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(createLabel,bookingIdField,mechanicIdField,
                serviceItemIdsField,createOrderButton,startLabel,startOrderIdField,
                startOrderButton,completeLabel,completeOrderIdField,completeOrderButton,
                orderListView,backButton
        );

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
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

    public void changeTextAllComponents() {

        createLabel.setText(languageManager.getString("createOrderButton"));
        bookingIdField.setPromptText(languageManager.getString("bookingIdField"));
        mechanicIdField.setPromptText(languageManager.getString("mechanicIdField"));
        serviceItemIdsField.setPromptText(languageManager.getString("serviceItemIdsField"));
        createOrderButton.setText(languageManager.getString("createOrderButton"));
        startLabel.setText(languageManager.getString("startOrder"));
        startOrderIdField.setPromptText(languageManager.getString("startOrderIdField"));
        startOrderButton.setText(languageManager.getString("startOrderButton"));
        completeLabel.setText(languageManager.getString("completeOrder"));
        completeOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));
        completeOrderButton.setText(languageManager.getString("completeOrderButton"));
        backButton.setText(languageManager.getString("backButton"));
    }

}
