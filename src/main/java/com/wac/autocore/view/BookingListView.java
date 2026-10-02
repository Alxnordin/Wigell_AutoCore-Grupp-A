package com.wac.autocore.view;

import com.wac.autocore.model.Booking;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//UI-vyn som visar listan över befintliga bokningar.
public class BookingListView {

    private final Parent root;
    private final TableView<Booking> bookingTable;
    private final Button backButton;

    private Label title;
    private Label subtitle;

    private TableColumn<Booking, String> idColumn;
    private TableColumn<Booking, String> vehicleColumn;
    private TableColumn<Booking, String> dateColumn;
    private TableColumn<Booking, String> descriptionColumn;
    private TableColumn<Booking, String> statusColumn;

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingListView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        //title
        title = new Label(languageManager.getString("bookingListTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-calendar");

        //underrubrik
        subtitle = new Label(languageManager.getString("bookingListSubtitle"));
        subtitle.getStyleClass().add("page-subtitle");

        bookingTable = UIComponents.createTable();

        idColumn = new TableColumn<>(languageManager.getString("bookingIdInTable"));
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        vehicleColumn = new TableColumn<>(languageManager.getString("vehicleIdInTable"));
        vehicleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                        String.valueOf(cellData.getValue().getVehicleId())));

        dateColumn = new TableColumn<>(languageManager.getString("dateInTable"));
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getDate())));

        descriptionColumn = new TableColumn<>(languageManager.getString("descriptionInTable"));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        statusColumn = new TableColumn<>(languageManager.getString("statusInTable"));
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        //sätter kolumnernas bredd så dom fyller hela tabellen
        idColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.12));
        vehicleColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.15));
        dateColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.15));
        descriptionColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.43));
        statusColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.15));

        bookingTable.getColumns().addAll(
                idColumn,
                vehicleColumn,
                dateColumn,
                descriptionColumn,
                statusColumn);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(
                titleBox,
                subtitle,
                bookingTable,
                backButton
        );
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}

    public TableView<Booking> getBookingTable() {return bookingTable;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        idColumn.setText(languageManager.getString("bookingIdInTable"));
        vehicleColumn.setText(languageManager.getString("vehicleIdInTable"));
        dateColumn.setText(languageManager.getString("dateInTable"));
        descriptionColumn.setText(languageManager.getString("descriptionInTable"));
        statusColumn.setText(languageManager.getString("statusInTable"));
        backButton.setText(languageManager.getString("backButton"));
        title.setText(languageManager.getString("bookingListTitle"));
        subtitle.setText(languageManager.getString("bookingListSubtitle"));
    }
}
