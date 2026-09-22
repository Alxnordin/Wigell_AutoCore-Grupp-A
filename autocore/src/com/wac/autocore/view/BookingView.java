package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class BookingView {

    private final Parent root;

    private TextField vehicleIdField;
    private DatePicker date;
    private TextField descriptionField;
    private Button createBookingButton;
    private Button backButton;
    private ListView<String> bookingListView;

    public BookingView () {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        vehicleIdField = new TextField();
        vehicleIdField.setPromptText("Fordons-ID");

        date = new DatePicker();

        descriptionField =new TextField();
        descriptionField.setPromptText("Beskrivning");

        createBookingButton = new Button("Skapa bokning");
        backButton = new Button("Tillbaka");

        bookingListView = new ListView<>();

        box.getChildren().addAll(vehicleIdField, date,descriptionField,
                createBookingButton,bookingListView,backButton);
        this.root = box;
    }

    public Parent getView() {return root;}

    public TextField getVehicleIdField() {return vehicleIdField;}
    public DatePicker getDate() {return date;}
    public TextField getDescriptionField() {return descriptionField;}
    public Button getCreateBookingButton() {return createBookingButton;}
    public Button getBackButton() {return backButton;}
    public ListView<String> getBookingListView() {return bookingListView;}
}
