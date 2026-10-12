package com.wac.autocore.view.service;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePackage;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;


//Alexander
//sektionen "Servicepaket" på sidan Show services
//visar alla paket med antal tjänster, total tid & totalt pris. När ett paket markeras visas dess tjänster,
//där kan tjänster läggas till och tas bort. Ett nytt paket skapas med ett namn
public class ServicePackageView {

    private final VBox root;

    private Label servicePackagesTitle;

    private TableView<ServicePackage> packageTable;
    private TableColumn<ServicePackage, String> packageNameColumn;
    private TableColumn<ServicePackage, String> serviceCountColumn;
    private TableColumn<ServicePackage, String> totalTimeColumn;
    private TableColumn<ServicePackage, String> totalPriceColumn;
    private Label noPackagesLabel;

    private TextField packageNameField;
    private Button createPackageButton;

    private Label packageServicesTitle;
    private TableView<ServiceItem> packageServicesTable;
    private TableColumn<ServiceItem, String> serviceColumn;
    private TableColumn<ServiceItem, String> timeColumn;
    private TableColumn<ServiceItem, String> priceColumn;
    private Label packageServicesHint;

    //true när ett paket är markerat, styr vilken text som visas i en tom tabell

    private boolean packageSelected = false;

    private ComboBox<ServiceItem> serviceComboBox;
    private Button addServiceButton;
    private Button removeServiceButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public ServicePackageView() {
        servicePackagesTitle = UIComponents.createSectionTitle(languageManager.getString("servicePackagesTitle"));

        //lista över paket
        packageTable = UIComponents.createTable();
        packageTable.setPrefHeight(150);
        packageTable.setMinHeight(110);

        noPackagesLabel = new Label(languageManager.getString("noServicePackagesHint"));
        noPackagesLabel.getStyleClass().add("placeholder-text");

        //Composite-paketet svarar själv på namn, antal tjänster, total tid & total pris
        packageNameColumn = new TableColumn<>(languageManager.getString("" +
                "packageNameColumn"));
        packageNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        serviceCountColumn = new TableColumn<>(languageManager.getString("" +
                "packageServiceCountColumn"));
        serviceCountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getServiceCount())));

        totalTimeColumn = new TableColumn<>(languageManager.getString("" +
                "packageTotalTimeColumn"));
        totalTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstimatedMinutes() + " min"));

        totalPriceColumn = new TableColumn<>(languageManager.getString("" +
                "packageTotalPriceColumn"));
        totalPriceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%,.0f kr",cellData.getValue().getPrice())));


        //paketets namn får mer plats än dom andra kolumnerna
        packageNameColumn.setPrefWidth(300);
        serviceCountColumn.setPrefWidth(150);
        totalTimeColumn.setPrefWidth(150);
        totalPriceColumn.setPrefWidth(150);

        packageTable.getColumns().addAll(packageNameColumn, serviceCountColumn,
                totalTimeColumn,totalPriceColumn);

        //nytt paket
        packageNameField = UIComponents.createTextField();
        packageNameField.setPrefWidth(250);
        packageNameField.setPromptText(languageManager.getString("packageNamePrompt"));

        createPackageButton= new Button(languageManager.getString("" +
                "createPackageButton"));
        createPackageButton.getStyleClass().add("add-service-button");

        HBox newPackageBox = new HBox(10);
        newPackageBox.setAlignment(Pos.CENTER_LEFT);
        newPackageBox.getChildren().addAll(packageNameField, createPackageButton);


        //tjänsterna i det markerade paketet
        packageServicesTitle = UIComponents.createSectionTitle(languageManager.getString("" +
                "packageServicesTitle"));

        packageServicesTable = UIComponents.createTable();
        packageServicesTable.setPrefHeight(150);
        packageServicesTable.setMinHeight(110);

        packageServicesHint = new Label(languageManager.getString("" +
                "selectPackageHint"));
        packageServicesHint.getStyleClass().add("placeHolder-text");
        packageServicesTable.setPlaceholder(packageServicesHint);

        serviceColumn = new TableColumn<>(languageManager.getString("" +
                "serviceColumn"));
        serviceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        timeColumn = new TableColumn<>(languageManager.getString("" +
                "timeColumn"));
        timeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstimatedMinutes() + " min"));

        priceColumn =new TableColumn<>(languageManager.getString("" +
                "priceColumn"));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getPrice())));

        serviceColumn.setPrefWidth(300);
        timeColumn.setPrefWidth(150);
        priceColumn.setPrefWidth(150);

        packageServicesTable.getColumns().addAll(serviceColumn,timeColumn,priceColumn);

        //välj tjänst, lägg till tjänst, ta bort tjänst
        serviceComboBox = UIComponents.createComboBox();
        serviceComboBox.setConverter(new StringConverter<ServiceItem>() {
            @Override
            public String toString(ServiceItem serviceItem) {
                return serviceItem == null ? "": serviceItem.getName();
            }

            @Override
            public ServiceItem fromString(String string) {
                return null;
            }
        });

        addServiceButton = new Button(languageManager.getString("" +
                "addService"));
        addServiceButton.getStyleClass().add("add-service-button");
        removeServiceButton = new Button(languageManager.getString("" +
                "removeService"));
        removeServiceButton.getStyleClass().add("add-service-button");

        HBox serviceButtons = new HBox(10);
        serviceButtons.setAlignment(Pos.CENTER_LEFT);
        serviceButtons.getChildren().addAll(serviceComboBox, addServiceButton,
                removeServiceButton);

        VBox section = UIComponents.createSectionBox();
        section.setSpacing(12);
        section.getChildren().addAll(
                servicePackagesTitle,
                packageTable,
                newPackageBox,
                packageServicesTitle,
                packageServicesTable,
                serviceButtons
        );
        this.root = section;

        //ingen redigering förrän ett paket är markerat
        setServiceEditingDisabled(true);

        //ändra språk
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}

    public TableView<ServicePackage> getPackageTable() {return packageTable;}
    public TextField getPackageNameField() {return packageNameField;}
    public Button getCreatePackageButton() {return createPackageButton;}
    public TableView<ServiceItem> getPackageServicesTable() {return packageServicesTable;}
    public ComboBox<ServiceItem> getServiceComboBox() {return serviceComboBox;}
    public Button getAddServiceButton() {return addServiceButton;}
    public Button getRemoveServiceButton() {return removeServiceButton;}

    //rullistan och knapparna går bara att använda när ett paket är markerat
    public void setServiceEditingDisabled(boolean disabled) {
        serviceComboBox.setDisable(disabled);
        addServiceButton.setDisable(disabled);
        removeServiceButton.setDisable(disabled);

    }

    //texten i en tom tabell: "markera ett paket" eller "paketet har inga tjänster än"
    public void setPackageSelected(boolean packageSelected) {
        this.packageSelected = packageSelected;
        packageServicesHint.setText(languageManager.getString(
                packageSelected ? "emptyPackageHint" : "selectPackageHint"));
    }

    public void changeTextAllComponents() {
        servicePackagesTitle.setText(languageManager.getString("servicePackagesTitle"));
        packageNameColumn.setText(languageManager.getString("packageNameColumn"));
        serviceCountColumn.setText(languageManager.getString("packageServiceCountColumn"));
        totalTimeColumn.setText(languageManager.getString("packageTotalTimeColumn"));
        totalPriceColumn.setText(languageManager.getString("packageTotalPriceColumn"));
        noPackagesLabel.setText(languageManager.getString("noServicePackagesHint"));
        packageNameField.setPromptText(languageManager.getString("packageNamePrompt"));
        createPackageButton.setText(languageManager.getString("createPackageButton"));
        packageServicesTitle.setText(languageManager.getString("packageServicesTitle"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        timeColumn.setText(languageManager.getString("timeColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        addServiceButton.setText(languageManager.getString("addService"));
        removeServiceButton.setText(languageManager.getString("removeService"));
        setPackageSelected(packageSelected);
    }
}
