package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


//UI klass med formulär för att skapa och visa kunder
public class CustomerView {

    private final Parent root;

    private TextField nameField;
    private TextField phoneField;
    private TextField emailField;
    private Button createCustomerButton;
    private Button backButton;
    private ListView<String> customerListView;

    LanguageManager languageManager = LanguageManager.getInstance();

    public CustomerView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        nameField = new TextField();
        nameField.setPromptText(languageManager.getString("nameField"));

        phoneField = new TextField();
        phoneField.setPromptText(languageManager.getString("phoneField"));

        emailField = new TextField();
        emailField.setPromptText(languageManager.getString("emailField"));

        createCustomerButton = new Button(languageManager.getString("createCustomerButton"));
        backButton = new Button(languageManager.getString("backButton"));

        customerListView = new ListView<>();

        box.getChildren().addAll(nameField,
                phoneField,emailField,
                createCustomerButton,
                customerListView,backButton);
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });

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

    public void changeTextAllComponents() {
        nameField.setPromptText(languageManager.getString("nameField"));
        phoneField.setPromptText(languageManager.getString("phoneField"));
        emailField.setPromptText(languageManager.getString("emailField"));
        createCustomerButton.setText(languageManager.getString("createCustomerButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}
