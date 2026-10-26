package com.wac.autocore.view.vehicle;

import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;

import java.util.List;

//UI-vyn som visar listan över befintliga fordon.
//Nya fordon skapas i VehicleView.
public class VehicleListView {

    private final Parent root;
    private final TableView<Vehicle> vehicleTable;
    private final Button backButton;

    private Button createVehicleButton;

    private Label title;
    private final Label subtitle;

    private TextField searchField;
    private ObservableList<Vehicle> observableVehiclesList;
    private FilteredList<Vehicle> filteredVehicleList;

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

        // Rubrik och knapp för att lägga till fordon.
        HBox headerBox = buildHeaderSection();

        //underrubrik
        subtitle = UIComponents.createSubtitle(languageManager.getString("vehicleListSubtitle"));

        //tabell
        vehicleTable = UIComponents.createTable();

        // Skapar sökrutan ovanför fordonslistan.
        HBox searchBox = buildSearchSection();

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

        box.getChildren().addAll(headerBox, subtitle, searchBox, vehicleTable, backButton);
        this.root = box;

        //ändra språk
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }


    private HBox buildHeaderSection() {
        // Skapar rubriken med fordonsikonen.
        title = new Label(languageManager.getString("vehicleListTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-car");

        // Skapar knappen med projektets gemensamma knappdesign.
        createVehicleButton = UIComponents.createCreateButton(
                languageManager.getString("addVehicleButton")
        );

        // Skapar mellanrum så att knappen hamnar längst till höger.
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer, createVehicleButton);

        return header;
    }

    // Skapar sökrutan och filtrerar fordonen medan användaren skriver.
    private HBox buildSearchSection() {
        searchField = new TextField();

        // Återanvänder sökrutan med samma ikon och styling som Customer.
        HBox searchBox = UIComponents.createSearchBox(searchField);
        searchField.setPromptText(languageManager.getString("searchTextField"));

        // Skapar en lista som kan uppdateras och filtreras.
        observableVehiclesList = FXCollections.observableArrayList();
        filteredVehicleList = new FilteredList<>(observableVehiclesList, vehicle -> true);

        // Visar bara fordon som matchar sökningen.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue == null
                    ? ""
                    : newValue.trim().toLowerCase();

            filteredVehicleList.setPredicate(vehicle -> {
                if (searchText.isEmpty()) {
                    return true;
                }

                return String.valueOf(vehicle.getId()).contains(searchText)
                        || matches(vehicle.getRegistrationNumber(), searchText)
                        || matches(vehicle.getBrand(), searchText)
                        || matches(vehicle.getModel(), searchText)
                        || String.valueOf(vehicle.getYear()).contains(searchText);
            });
        });

        // Tabellen använder den filtrerade listan i stället för originallistan.
        vehicleTable.setItems(filteredVehicleList);

        return searchBox;
    }

    // Jämför text utan att vara känslig för stora och små bokstäver.
    private boolean matches(String value, String searchText) {
        return value != null && value.toLowerCase().contains(searchText);
    }


    // Gör knappen tillgänglig för VehicleController.
    public Button getCreateVehicleButton() {
        return createVehicleButton;
    }

    // Uppdaterar originallistan när fordon hämtas från servicen.
    public void setVehicles(List<Vehicle> vehicles) {
        observableVehiclesList.setAll(vehicles);
    }

    public Parent getView() {return root;}

    public TableView<Vehicle> getVehicleTable() {return vehicleTable;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("vehicleListTitle"));
        subtitle.setText(languageManager.getString("vehicleListSubtitle"));
        createVehicleButton.setText(languageManager.getString("addVehicleButton"));
        idColumn.setText(languageManager.getString("vehicleIdInTable"));
        registrationNumberColumn.setText(languageManager.getString("registrationNumber"));
        brandColumn.setText(languageManager.getString("vehicleBrand"));
        modelColumn.setText(languageManager.getString("vehicleModel"));
        yearColumn.setText(languageManager.getString("vehicleYear"));
        customerIdColumn.setText(languageManager.getString("customerId"));
        searchField.setPromptText(languageManager.getString("searchTextField"));
        backButton.setText(languageManager.getString("backButton"));
    }
}