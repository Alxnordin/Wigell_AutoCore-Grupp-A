package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


//UI vy klass för att skapa en ny bokning (fordons-ID, datum, beskrivning).
public class BookingView {

    private final Parent root;

    private TextField vehicleIdField;
    private DatePicker date;
    private TextField descriptionField;
    private Button createBookingButton;
    private Button backButton;
    private ListView<String> bookingListView;

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingView () {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        vehicleIdField = new TextField();
        vehicleIdField.setPromptText(languageManager.getString("vehicleIdField"));

        date = new DatePicker();

        descriptionField =new TextField();
        descriptionField.setPromptText(languageManager.getString("descriptionField"));

        createBookingButton = new Button(languageManager.getString("createBookingButton"));
        backButton = new Button(languageManager.getString("backButton"));

        bookingListView = new ListView<>();

        box.getChildren().addAll(vehicleIdField, date,descriptionField,
                createBookingButton,bookingListView,backButton);
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}

    public TextField getVehicleIdField() {return vehicleIdField;}
    public DatePicker getDate() {return date;}
    public TextField getDescriptionField() {return descriptionField;}
    public Button getCreateBookingButton() {return createBookingButton;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        vehicleIdField.setPromptText(languageManager.getString("vehicleIdField"));
        descriptionField.setPromptText(languageManager.getString("descriptionField"));
        createBookingButton.setText(languageManager.getString("createBookingButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}
