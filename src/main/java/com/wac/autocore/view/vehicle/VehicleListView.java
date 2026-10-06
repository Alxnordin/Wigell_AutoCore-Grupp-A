package com.wac.autocore.view.vehicle;

import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//UI-vyn som visar listan över befintliga fordon.
//Nya fordon skapas i VehicleView.
public class VehicleListView {

    private final Parent root;
    private final TableView<Vehicle> vehicleTable;
    private final Button backButton;

    private final Label title;
    private final Label subtitle;

    private final TableColumn<Vehicle, Integer> idColumn;
    private final TableColumn<Vehicle, String> registrationNumberColumn;
    private final TableColumn<Vehicle, String> brandColumn;
    private final TableColumn<Vehicle, String> modelColumn;
    private final TableColumn<Vehicle, Integer> yearColumn;
    private final TableColumn<Vehicle, Integer> customerIdColumn;

    LanguageManager languageManager = LanguageManager.getInstance();

    public VehicleListView() {
        //huvudcontainer
        VBox box = UIComponents.createVBoxForViews();

        //titel
        title = new Label(languageManager.getString("vehicleListTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-car");

        //underrubrik
        subtitle = UIComponents.createSubtitle(languageManager.getString("vehicleListSubtitle"));

        //tabell
        vehicleTable = UIComponents.createTable();

        idColumn = new TableColumn<>(languageManager.getString("vehicleIdInTable"));
        registrationNumberColumn = new TableColumn<>(languageManager.getString("registrationNumber"));
        brandColumn = new TableColumn<>(languageManager.getString("vehicleBrand"));
        modelColumn = new TableColumn<>(languageManager.getString("vehicleModel"));
        yearColumn = new TableColumn<>(languageManager.getString("vehicleYear"));
        customerIdColumn = new TableColumn<>(languageManager.getString("customerId"));

        //koppla kolumnerna till fordonets fält
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        registrationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        vehicleTable.getColumns().addAll(
                idColumn,
                registrationNumberColumn,
                brandColumn,
                modelColumn,
                yearColumn,
                customerIdColumn);

        //kolumnerna delar på tabellens bredd (registreringsnumret får lite mer plats)
        vehicleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        registrationNumberColumn.setPrefWidth(150);

        //tillbakaknapp
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(titleBox, subtitle, vehicleTable, backButton);
        this.root = box;

        //ändra språk
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}

    public TableView<Vehicle> getVehicleTable() {return vehicleTable;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("vehicleListTitle"));
        subtitle.setText(languageManager.getString("vehicleListSubtitle"));
        idColumn.setText(languageManager.getString("vehicleIdInTable"));
        registrationNumberColumn.setText(languageManager.getString("registrationNumber"));
        brandColumn.setText(languageManager.getString("vehicleBrand"));
        modelColumn.setText(languageManager.getString("vehicleModel"));
        yearColumn.setText(languageManager.getString("vehicleYear"));
        customerIdColumn.setText(languageManager.getString("customerId"));
        backButton.setText(languageManager.getString("backButton"));
    }
}