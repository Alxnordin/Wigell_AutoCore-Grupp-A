package com.wac.autocore.view.invoice;

import com.wac.autocore.model.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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

    private Label invoiceDate;
    private Label customerNameLabel;
    private Label customerPhoneLabel;
    private Label vehicleLabel;
    private Label mechanicNameLabel;

    private Label invoiceDateValue;
    private Label customerNameValue;
    private Label customerPhoneValue;
    private Label vehicleValue;
    private Label mechanicNameValue;

    private VBox invoiceDateBox;
    private VBox customerNameBox;
    private VBox customerPhoneBox;
    private VBox vehicleBox;
    private VBox mechanicBox;

    private final Invoice invoice;

    private final TableView<InvoiceLine> invoiceTableView;
    private final TableColumn<InvoiceLine, String> serviceColumn;
    private final TableColumn<InvoiceLine, String> priceColumn;
    private final TableColumn<InvoiceLine, String> discountColumn;
    private final TableColumn<InvoiceLine, String> totalAmount;

    private Label companyNameLabel;
    private Label companyAddressLabel1;
    private Label companyAddressLabel2;
    private Label organizationNumberLabel;

    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceDetailsView(Invoice invoice, Booking booking,
            Vehicle vehicle, Customer customer,
            Mechanic mechanic, List<InvoiceLine> invoiceLines) {

        this.invoice = invoice;

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        title = new Label(languageManager.getString("invoiceTitle") + " #" + invoice.getId());
        HBox titleBox = UIComponents.createPageTitle(title, "fa-file-text-o");
        subtitle = UIComponents.createSubtitle(languageManager.getString("invoiceSubtitle"));

        VBox invoiceInfoBox = UIComponents.createSectionBox();

        invoiceDate = UIComponents.createInfoLabel(languageManager.getString("invoiceDate"));
        invoiceDateValue = UIComponents.createValueLabel(invoice.getInvoiceDate());
        invoiceDateBox = UIComponents.createVBoxWithSpacing6();
        invoiceDateBox.getChildren().addAll(invoiceDate, invoiceDateValue);

        customerNameLabel = UIComponents.createInfoLabel(languageManager.getString("customerNameLabel"));
        customerNameValue = UIComponents.createValueLabel(customer.getName());
        customerNameBox = UIComponents.createVBoxWithSpacing6();
        customerNameBox.getChildren().addAll(customerNameLabel, customerNameValue);

        customerPhoneLabel = UIComponents.createInfoLabel(languageManager.getString("customerPhoneLabel"));
        customerPhoneValue = UIComponents.createValueLabel(customer.getPhone());
        customerPhoneBox = UIComponents.createVBoxWithSpacing6();
        customerPhoneBox.getChildren().addAll(customerPhoneLabel, customerPhoneValue);

        vehicleLabel = UIComponents.createInfoLabel(languageManager.getString("vehicleLabelInvoice"));
        vehicleValue = UIComponents.createValueLabel(vehicle.getRegistrationNumber());
        vehicleBox = UIComponents.createVBoxWithSpacing6();
        vehicleBox.getChildren().addAll(vehicleLabel, vehicleValue);

        mechanicNameLabel = UIComponents.createInfoLabel(languageManager.getString("mechanicNameLabel"));
        mechanicNameValue = UIComponents.createValueLabel(mechanic.getName());
        mechanicBox = UIComponents.createVBoxWithSpacing6();
        mechanicBox.getChildren().addAll(mechanicNameLabel, mechanicNameValue);

        HBox informationRow = new HBox(45);
        informationRow.getChildren().addAll( invoiceDateBox,
                customerNameBox, customerPhoneBox, vehicleBox, mechanicBox);

        invoiceInfoBox.getChildren().add(informationRow);

        VBox invoiceLinesBox = UIComponents.createSectionBox();

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
        VBox companyBox = new VBox(4);companyBox.getChildren().
                addAll(companyNameLabel, companyAddressLabel1,
                companyAddressLabel2, organizationNumberLabel);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(titleBox, subtitle, invoiceInfoBox,
                invoiceLinesBox, companyBox, backButton);

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    changeTextAllComponents();});

        this.root = box;
    }

    public Parent getView() {return root;}

    public void changeTextAllComponents() {

        title.setText(languageManager.getString("invoiceTitle") + " #" + invoice.getId());
        subtitle.setText(languageManager.getString("invoiceSubtitle"));
        invoiceDate.setText(languageManager.getString("invoiceDate"));
        customerNameLabel.setText(languageManager.getString("customerNameLabel"));
        customerPhoneLabel.setText(languageManager.getString("customerPhoneLabel"));
        vehicleLabel.setText(languageManager.getString("vehicleLabelInvoice"));
        mechanicNameLabel.setText(languageManager.getString("mechanicNameLabel"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        discountColumn.setText(languageManager.getString("discountColumn"));
        totalAmount.setText(languageManager.getString("totalPriceColumn"));
    }

    public void setOnBack(Runnable action) {
        backButton.setOnAction(e -> action.run());
    }
}

