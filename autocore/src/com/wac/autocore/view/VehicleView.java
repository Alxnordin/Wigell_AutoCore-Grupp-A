package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


//UI klass med formulär för att skapa och visa fordon
public class VehicleView {

    private final Parent root;

    private TextField registrationNumberField;
    private TextField brandField;
    private TextField modelField;
    private TextField yearField;
    private TextField customerIdField;
    private Button createVehicleButton;
    private Button backButton;
    private ListView<String> vehicleListView;

    LanguageManager languageManager =  LanguageManager.getInstance();

    public VehicleView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        registrationNumberField = new TextField();
        registrationNumberField.setPromptText(languageManager.getString("registrationNumber"));

        brandField = new TextField();
        brandField.setPromptText(languageManager.getString("vehicleBrand"));

        modelField = new TextField();
        modelField.setPromptText(languageManager.getString("vehicleModel"));

        yearField = new TextField();
        yearField.setPromptText(languageManager.getString("vehicleYear"));

        customerIdField= new TextField();
        customerIdField.setPromptText(languageManager.getString("customerId"));

        createVehicleButton = new Button(languageManager.getString("createVehicleButton"));
        backButton = new Button(languageManager.getString("backButton"));

        vehicleListView = new ListView<>();

        box.getChildren().addAll(registrationNumberField, brandField,
                modelField,yearField,vehicleListView,customerIdField,createVehicleButton,
                backButton);
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return  root;}

    public TextField getRegistrationNumberField() {return registrationNumberField;}
    public TextField getBrandField() {return brandField;}
    public TextField getModelField() {return modelField;}
    public TextField getYearField() {return yearField;}
    public TextField getCustomerIdField() {return customerIdField;}
    public Button getCreateVehicleButton() {return createVehicleButton;}
    public Button getBackButton() {return backButton;
    }
    public ListView<String> getVehicleListView() {return vehicleListView;}

    public void changeTextAllComponents() {
        registrationNumberField.setText(languageManager.getString("registrationNumber"));
        brandField.setText(languageManager.getString("vehicleBrand"));
        modelField.setText(languageManager.getString("vehicleModel"));
        yearField.setText(languageManager.getString("vehicleYear"));
        customerIdField.setText(languageManager.getString("customerId"));
        createVehicleButton.setText(languageManager.getString("createVehicleButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}
