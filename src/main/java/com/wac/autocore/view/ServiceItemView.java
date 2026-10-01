package com.wac.autocore.view;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//Visar listan över tillgängliga tjänster
public class ServiceItemView {
    private final Parent root;

    private Label title;
    private Label subtitle;

    private TableView<ServiceItem> serviceTable;

    private TableColumn<ServiceItem, String> serviceColumn;
    private TableColumn<ServiceItem, String> descriptionColumn;
    private TableColumn<ServiceItem, String> timeColumn;
    private TableColumn<ServiceItem, String> priceColumn;

    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();


    public ServiceItemView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        // Rubrik
        title = new Label(languageManager.getString("serviceLabel"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-wrench");

        // Underrubrik
        subtitle = new Label(languageManager.getString("serviceSubtitle"));
        subtitle.getStyleClass().add("page-subtitle");

        // Tabell
        serviceTable = UIComponents.createTable();


        // Kolumner
        serviceColumn = new TableColumn<>(languageManager.getString("serviceItemName"));
        descriptionColumn = new TableColumn<>(languageManager.getString("serviceItemDescription"));
        timeColumn = new TableColumn<>(languageManager.getString("serviceItemEstimatedMinutes"));
        priceColumn = new TableColumn<>(languageManager.getString("serviceItemPrice"));


        // Vad som ska visas i varje kolumn
        serviceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));
        timeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstimatedMinutes() + " min"));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getPrice())));

        // Kolumnbredder
        serviceColumn.prefWidthProperty().bind(serviceTable.widthProperty().multiply(0.25));
        descriptionColumn.prefWidthProperty().bind(serviceTable.widthProperty().multiply(0.40));
        timeColumn.prefWidthProperty().bind(serviceTable.widthProperty().multiply(0.15));
        priceColumn.prefWidthProperty().bind(serviceTable.widthProperty().multiply(0.20));

        serviceTable.getColumns().addAll(serviceColumn, descriptionColumn, timeColumn, priceColumn);

        // Tillbaka-knapp
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        // Lägg allt på sidan
        box.getChildren().addAll(titleBox, subtitle, serviceTable, backButton);

        this.root = box;

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    changeTextAllComponents();
                }
        );
    }

    public Parent getView() { return root; }
    public TableView<ServiceItem> getServiceTable() {return serviceTable; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("serviceLabel"));
        subtitle.setText(languageManager.getString("serviceSubtitle"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        descriptionColumn.setText(languageManager.getString("serviceItemDescription"));
        timeColumn.setText(languageManager.getString("serviceItemEstimatedMinutes"));
        priceColumn.setText(languageManager.getString("serviceItemPrice"));
        backButton.setText(languageManager.getString("backButton"));
    }
}