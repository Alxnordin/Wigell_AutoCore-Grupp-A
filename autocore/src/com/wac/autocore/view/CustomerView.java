package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CustomerView {

    private final Parent root;

    private TextField nameField;
    private TextField phoneField;
    private TextField emailField;
    private Button createCustomerButton;
    private Button backButton;
    private ListView<String> customerListView;

    public CustomerView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        nameField = new TextField();
        nameField.setPromptText("Namn");

        phoneField = new TextField();
        phoneField.setPromptText("Telefon");

        emailField = new TextField();
        emailField.setPromptText("Email");

        createCustomerButton = new Button("Skapa kund");
        backButton = new Button("Tillbaka");

        customerListView = new ListView<>();

        box.getChildren().addAll(nameField,
                phoneField,emailField,
                createCustomerButton,
                customerListView,backButton);
        this.root = box;
    }
    public Parent getView() {
        return root;
    }

    public TextField getNameField() {return nameField;}
    public TextField getPhoneField() {return phoneField;}
    public TextField getEmailField() {return emailField;}
    public Button getCreateCustomerButton() {return createCustomerButton;}
    public Button getBackButton() {return backButton;}
    public ListView<String> getCustomerListView() {return customerListView;}
}
