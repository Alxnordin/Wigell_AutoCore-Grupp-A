package com.wac.autocore.view;

import com.wac.autocore.model.*;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class InvoiceDetailsView {

    private final Parent root;

    private Label title;
    private Label subtitle;

    private final Invoice invoice;

    private final TableView<InvoiceLine> invoiceTableView;
    private final TableColumn<InvoiceLine, String> serviceColumn;
    private final TableColumn<InvoiceLine, String> priceColumn;
    private final TableColumn<InvoiceLine, String> discountColumn;
    private final TableColumn<InvoiceLine, String> totalAmount;

    Label invoiceID;
    Label invoiceDate;
    Label customerNameLabel;
    Label customerPhoneLabel;
    Label vehicleLabel;
    Label mechanicNameLabel;

    Label companyNameLabel;
    Label companyAddressLabel1;
    Label companyAddressLabel2;
    Label organizationNumberLabel;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceDetailsView(Invoice invoice, Booking booking,
            Vehicle vehicle, Customer customer,
            Mechanic mechanic, List<InvoiceLine> invoiceLines) {

        this.invoice = invoice;

        //FIXSkapa en metod i UIComponents
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        //FIXA. Rubriken visar vilken faktura användaren tittar på.
        title = new Label(languageManager.getString("invoiceTitle") + " #" + invoice.getId());

        HBox titleBox = UIComponents.createPageTitle(title, "fa-file-text-o");
        subtitle = UIComponents.createSubtitle("Underrubrik");

        //En sektion för information om fakturan.
        VBox invoiceInfoBox = UIComponents.createSectionBox();

        //Faktura-ID
        invoiceID = new Label(languageManager.getString("invoiceIDNumber"));
        invoiceID.getStyleClass().add("info-label");
        Label invoiceIDValue = new Label(String.valueOf(invoice.getId()));
        invoiceIDValue.getStyleClass().add("info-value");

        VBox invoiceIDBox = new VBox(6);
        invoiceIDBox.getChildren().addAll(invoiceID, invoiceIDValue);

        // Fakturadatum
        invoiceDate = new Label(languageManager.getString("invoiceDate"));
        invoiceDate.getStyleClass().add("info-label");
        Label invoiceDateValue = new Label(invoice.getInvoiceDate().toString());
        invoiceDateValue.getStyleClass().add("info-value");

        VBox invoiceDateBox = new VBox(6);
        invoiceDateBox.getChildren().addAll(invoiceDate, invoiceDateValue);

        //Kundens namn
        customerNameLabel = new Label(languageManager.getString("customerNameLabel"));
        customerNameLabel.getStyleClass().add("info-label");
        Label customerNameValue = new Label(customer.getName());
        customerNameValue.getStyleClass().add("info-value");
        VBox customerNameBox = new VBox(6);
        customerNameBox.getChildren().addAll(customerNameLabel, customerNameValue);

        //Kundens telefon
        customerPhoneLabel = new Label(languageManager.getString("customerPhoneLabel"));
        customerPhoneLabel.getStyleClass().add("info-label");
        Label customerPhoneValue = new Label(customer.getPhone());
        customerPhoneValue.getStyleClass().add("info-value");
        VBox customerPhoneBox = new VBox(6);
        customerPhoneBox.getChildren().addAll(customerPhoneLabel, customerPhoneValue);

        //Fordon
        vehicleLabel = new Label(languageManager.getString("vehicleLabel"));
        vehicleLabel.getStyleClass().add("info-label");
        Label vehicleValue = new Label(vehicle.getRegistrationNumber());
        vehicleValue.getStyleClass().add("info-value");
        VBox vehicleBox = new VBox(6);
        vehicleBox.getChildren().addAll(vehicleLabel, vehicleValue);

        mechanicNameLabel = new Label(languageManager.getString("mechanicNameLabel"));
        mechanicNameLabel.getStyleClass().add("info-label");
        Label mechanicNameValue = new Label(mechanic.getName());
        mechanicNameValue.getStyleClass().add("info-value");
        VBox mechanicBox = new VBox(6);
        mechanicBox.getChildren().addAll(mechanicNameLabel, mechanicNameValue);

        HBox informationRow = new HBox(45);
        informationRow.getChildren().addAll(invoiceIDBox, invoiceDateBox,
                customerNameBox, customerPhoneBox, vehicleBox, mechanicBox);

        invoiceInfoBox.getChildren().add(informationRow);

        //Egen sektion för fakturaraderna.
        VBox invoiceLinesBox = UIComponents.createSectionBox();

        //Tabellen innehåller riktiga InvoiceLine-objekt.
        invoiceTableView = UIComponents.createTable();
        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));
        discountColumn = new TableColumn<>(languageManager.getString("discountColumn"));
        totalAmount = new TableColumn<>(languageManager.getString("totalPriceColumn"));

        serviceColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue()
                        .getServiceItemDescription()));

        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                        String.format("%.2f kr", cellData.getValue().getServiceItemPrice())));

        discountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getDiscount())));

        totalAmount.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getFinalPricePerServiceAfterDiscount())));

        serviceColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.45));
        priceColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.20));
        discountColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.15));
        totalAmount.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.20));
        invoiceTableView.getColumns().addAll(serviceColumn, priceColumn, discountColumn, totalAmount);

        invoiceTableView.getItems().addAll(invoiceLines);
        invoiceTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        invoiceLinesBox.getChildren().add(invoiceTableView);

        companyNameLabel = new Label("Wigell AutoCore");
        companyAddressLabel1 = new Label("Låtsasgatan 15");
        companyAddressLabel2 = new Label("855 92 Ankeborg");
        organizationNumberLabel = new Label("Org.nr. 12464-4544");

        VBox companyBox = new VBox(4);
        companyBox.getChildren().addAll(companyNameLabel, companyAddressLabel1,
                companyAddressLabel2, organizationNumberLabel);

        box.getChildren().addAll(titleBox, subtitle, invoiceInfoBox,
                invoiceLinesBox, companyBox);

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    changeTextAllComponents();});

        this.root = box;
    }

    public Parent getView() {return root;}

    public void changeTextAllComponents() {

        title.setText(languageManager.getString("invoiceTitle") + " #" + invoice.getId());
        subtitle.setText(languageManager.getString("invoiceSubtitle"));
        invoiceID.setText(languageManager.getString("invoiceIDNumber"));
        invoiceDate.setText(languageManager.getString("invoiceDate"));
        customerNameLabel.setText(languageManager.getString("customerNameLabel"));
        customerPhoneLabel.setText(languageManager.getString("customerPhoneLabel"));
        vehicleLabel.setText(languageManager.getString("vehicleLabel"));
        mechanicNameLabel.setText(languageManager.getString("mechanicNameLabel"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        discountColumn.setText(languageManager.getString("discountColumn"));
        totalAmount.setText(languageManager.getString("totalPriceColumn"));
    }
}

