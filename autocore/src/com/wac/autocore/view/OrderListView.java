package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
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

    private Label startLabel;
    private final TextField startOrderIdField;
    private final Button startOrderButton;

    private Label completeLabel;
    private final TextField completeOrderIdField;
    private final Button completeOrderButton;

    private final Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public OrderListView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        orderListView = new ListView<>();

        startLabel = new Label(languageManager.getString("startOrder"));
        startOrderIdField = new TextField();
        startOrderIdField.setPromptText(languageManager.getString("startOrderIdField"));
        startOrderButton = new Button(languageManager.getString("startOrderButton"));

        completeLabel = new Label(languageManager.getString("completeOrder"));
        completeOrderIdField = new TextField();
        completeOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));
        completeOrderButton = new Button(languageManager.getString("completeOrderButton"));

        backButton = new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(orderListView,
                startLabel, startOrderIdField, startOrderButton,
                completeLabel, completeOrderIdField, completeOrderButton,
                backButton);
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}

    public ListView<String> getOrderListView() {return orderListView;}

    public TextField getStartOrderIdField() {return startOrderIdField;}
    public Button getStartOrderButton() {return startOrderButton;}

    public TextField getCompleteOrderIdField() {return completeOrderIdField;}
    public Button getCompleteOrderButton() {return completeOrderButton;}

    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        startLabel.setText(languageManager.getString("startOrder"));
        startOrderIdField.setText(languageManager.getString("startOrderIdField"));
        startOrderButton.setText(languageManager.getString("startOrderButton"));

        completeLabel.setText(languageManager.getString("completeOrder"));
        completeOrderIdField.setText(languageManager.getString("workOrderIdField"));
        completeOrderButton.setText(languageManager.getString("completeOrderButton"));

        //OBS! Den här ändrar nu alla backButton på alla sidor??
        backButton.setText(languageManager.getString("backButton"));
    }
}