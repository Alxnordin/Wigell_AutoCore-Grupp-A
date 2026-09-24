package com.wac.autocore.view;

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

    public VehicleView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        registrationNumberField = new TextField();
        registrationNumberField.setPromptText("Registreringsnummer");

        brandField = new TextField();
        brandField.setPromptText("Märke");

        modelField = new TextField();
        modelField.setPromptText("Modell");

        yearField = new TextField();
        yearField.setPromptText("Årsmodell");

        customerIdField= new TextField();
        customerIdField.setPromptText("Kund-ID");

        createVehicleButton = new Button("Skapa fordon");
        backButton = new Button("Tillbaka");

        vehicleListView = new ListView<>();

        box.getChildren().addAll(registrationNumberField, brandField,
                modelField,yearField,vehicleListView,customerIdField,createVehicleButton,
                backButton);
        this.root = box;
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
}
