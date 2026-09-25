package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

//UI-vy med Formulär för att skapa en ny arbetsorder (kopplad till en bokning, mekaniker och service delarna)
public class OrderFormView {

    private final Parent root;

    private Label createLabel;
    private final TextField bookingIdField;
    private final TextField mechanicIdField;
    private final TextField serviceItemIdsField;
    private final Button createOrderButton;

    private final Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public OrderFormView() {
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

        backButton = new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(createLabel, bookingIdField, mechanicIdField,
                serviceItemIdsField, createOrderButton, backButton);

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

    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        createLabel.setText(languageManager.getString("createOrderButton"));
        bookingIdField.setPromptText(languageManager.getString("bookingIdField"));
        mechanicIdField.setPromptText(languageManager.getString("mechanicIdField"));
        serviceItemIdsField.setPromptText(languageManager.getString("serviceItemIdsField"));
        createOrderButton.setText(languageManager.getString("createOrderButton"));
        backButton.setText(languageManager.getString("backButton"));
    }

}