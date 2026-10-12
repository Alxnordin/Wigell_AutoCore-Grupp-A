package com.wac.autocore.view.vehicle;

import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

//UI-vy med formulär för att skapa ett nytt fordon (kund, registreringsnummer, märke, modell, årsmodell).
//Listan över fordon ligger i VehicleListView.
public class VehicleView {

    private final Parent root;

    private ComboBox<Customer> customerComboBox;
    private TextField registrationNumberField;
    private TextField brandField;
    private TextField modelField;
    private TextField yearField;

    private Button createVehicleButton;
    private Button backButton;

    private Label title;
    private Label subtitle;
    private Label vehicleInformationTitle;
    private Label customerLabel;
    private Label registrationNumberLabel;
    private Label brandLabel;
    private Label modelLabel;
    private Label yearLabel;

    LanguageManager languageManager = LanguageManager.getInstance();

    public VehicleView() {
        //huvudcontainer
        VBox box = UIComponents.createVBoxForViews();

        //titel
        title = new Label(languageManager.getString("vehicleTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-car");

        //underrubrik
        subtitle = UIComponents.createSubtitle(languageManager.getString("vehicleSubtitle"));

        //sektion -> fordonsinformation
        vehicleInformationTitle = UIComponents.createSectionTitle(languageManager.getString("vehicleInformationTitle"));

        //kunden väljs i en rullista, controllern fyller den via GarageSystem
        customerComboBox = UIComponents.createComboBox();

        //visa kundens namn i rullistan
        customerComboBox.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer == null ? "" : customer.getName();
            }
            @Override
            public Customer fromString(String string) {
                return null;
            }
        });

        registrationNumberField = createField();
        brandField = createField();
        modelField = createField();
        yearField = createField();

        customerLabel = new Label(languageManager.getString("customerLabel"));
        registrationNumberLabel = new Label(languageManager.getString("registrationNumber"));
        brandLabel = new Label(languageManager.getString("vehicleBrand"));
        modelLabel = new Label(languageManager.getString("vehicleModel"));
        yearLabel = new Label(languageManager.getString("vehicleYear"));

        //rad 1: kund + registreringsnummer
        HBox ownerFields = new HBox(15);
        ownerFields.getChildren().addAll(
                createLabeledBox(customerLabel, customerComboBox),
                createLabeledBox(registrationNumberLabel, registrationNumberField));

        //rad 2: märke + modell + årsmodell
        HBox vehicleFields = new HBox(15);
        vehicleFields.getChildren().addAll(
                createLabeledBox(brandLabel, brandField),
                createLabeledBox(modelLabel, modelField),
                createLabeledBox(yearLabel, yearField));

        VBox vehicleInformation = UIComponents.createSectionBox();
        vehicleInformation.setSpacing(15);
        vehicleInformation.getChildren().addAll(vehicleInformationTitle, ownerFields, vehicleFields);

        //knappar
        createVehicleButton = new Button(languageManager.getString("createVehicleButton"));
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(backButton, createVehicleButton);

        box.getChildren().addAll(titleBox, subtitle, vehicleInformation, buttonBox);
        this.root = box;

        //ändra språk
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    //textfält i samma bredd som rullistan
    private TextField createField() {
        TextField textField = UIComponents.createTextField();
        textField.setPrefWidth(250);
        return textField;
    }

    //etikett ovanför fältet, på samma sätt som i BookingView
    private VBox createLabeledBox(Label label, javafx.scene.Node field) {
        VBox labeledBox = new VBox(5);
        labeledBox.getChildren().addAll(label, field);
        return labeledBox;
    }

    public Parent getView() {return root;}

    public ComboBox<Customer> getCustomerComboBox() {return customerComboBox;}
    public TextField getRegistrationNumberField() {return registrationNumberField;}
    public TextField getBrandField() {return brandField;}
    public TextField getModelField() {return modelField;}
    public TextField getYearField() {return yearField;}
    public Button getCreateVehicleButton() {return createVehicleButton;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("vehicleTitle"));
        subtitle.setText(languageManager.getString("vehicleSubtitle"));
        vehicleInformationTitle.setText(languageManager.getString("vehicleInformationTitle"));
        customerLabel.setText(languageManager.getString("customerLabel"));
        registrationNumberLabel.setText(languageManager.getString("registrationNumber"));
        brandLabel.setText(languageManager.getString("vehicleBrand"));
        modelLabel.setText(languageManager.getString("vehicleModel"));
        yearLabel.setText(languageManager.getString("vehicleYear"));
        createVehicleButton.setText(languageManager.getString("createVehicleButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}