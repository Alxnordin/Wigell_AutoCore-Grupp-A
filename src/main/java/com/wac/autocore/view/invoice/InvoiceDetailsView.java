package com.wac.autocore.view.invoice;

import com.wac.autocore.model.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    private final Label title;
    private final Label subtitle;

    private final Label invoiceDate;
    private final Label customerNameLabel;
    private final Label customerPhoneLabel;
    private final Label vehicleLabel;
    private final Label mechanicNameLabel;

    private final Label statusLabel;
    private Label statusValue;
    String statusText;

    private final Invoice invoice;

    private final TableView<InvoiceLine> invoiceTableView;
    private final TableColumn<InvoiceLine, String> serviceColumn;
    private final TableColumn<InvoiceLine, String> priceColumn;
    private final TableColumn<InvoiceLine, String> discountColumn;
    private final TableColumn<InvoiceLine, String> totalAmount;

    Label totalTitle;

    private final Button backButton;

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
        Label invoiceDateValue = UIComponents.createValueLabel(invoice.getInvoiceDate());
        VBox invoiceDateBox = UIComponents.createVBoxWithSpacing6();
        invoiceDateBox.getChildren().addAll(invoiceDate, invoiceDateValue);

        customerNameLabel = UIComponents.createInfoLabel(languageManager.getString("customerNameLabel"));
        Label customerNameValue = UIComponents.createValueLabel(customer.getName());
        VBox customerNameBox = UIComponents.createVBoxWithSpacing6();
        customerNameBox.getChildren().addAll(customerNameLabel, customerNameValue);

        customerPhoneLabel = UIComponents.createInfoLabel(languageManager.getString("customerPhoneLabel"));
        Label customerPhoneValue = UIComponents.createValueLabel(customer.getPhone());
        VBox customerPhoneBox = UIComponents.createVBoxWithSpacing6();
        customerPhoneBox.getChildren().addAll(customerPhoneLabel, customerPhoneValue);

        vehicleLabel = UIComponents.createInfoLabel(languageManager.getString("vehicleLabelInvoice"));
        Label vehicleValue = UIComponents.createValueLabel(vehicle.getRegistrationNumber());
        VBox vehicleBox = UIComponents.createVBoxWithSpacing6();
        vehicleBox.getChildren().addAll(vehicleLabel, vehicleValue);

        mechanicNameLabel = UIComponents.createInfoLabel(languageManager.getString("mechanicNameLabel"));
        Label mechanicNameValue = UIComponents.createValueLabel(mechanic.getName());
        VBox mechanicBox = UIComponents.createVBoxWithSpacing6();
        mechanicBox.getChildren().addAll(mechanicNameLabel, mechanicNameValue);

        statusLabel = UIComponents.createInfoLabel(languageManager.getString("statusLabel"));

        if (invoice.isPaid()) {
            statusText = languageManager.getString("paid");
        } else {
            statusText = languageManager.getString("unpaid");
        }

        statusValue = UIComponents.createInvoiceStatusValueLabel(statusText, invoice.isPaid());

        VBox statusBox = UIComponents.createVBoxWithSpacing6();
        VBox.setMargin(statusBox, new Insets(15, 0, 0, 0));
        statusBox.getChildren().addAll(statusLabel, statusValue);

        HBox informationRow = new HBox(45);
        informationRow.getChildren().addAll(invoiceDateBox, customerNameBox, customerPhoneBox,
                vehicleBox, mechanicBox);

        invoiceInfoBox.getChildren().addAll(informationRow, statusBox);

        VBox invoiceLinesBox = UIComponents.createSectionBox();

        invoiceTableView = UIComponents.createTable();
        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));
        discountColumn = new TableColumn<>(languageManager.getString("discountColumn"));
        totalAmount = new TableColumn<>(languageManager.getString("totalPriceColumn"));

        totalTitle = new Label(languageManager.getString("totalPriceColumn"));
        Label totalValue = new Label(String.format("%.2f kr", invoice.getTotalAmount()));

        VBox totalCard = UIComponents.createSummaryCard("fa-money", totalTitle, totalValue);
        HBox totalBox = new HBox();
        totalBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        totalBox.getChildren().add(totalCard);
        totalCard.setPrefWidth(330);

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
        invoiceLinesBox.getChildren().addAll(invoiceTableView, totalBox);

        Label companyNameLabel = UIComponents.createCompanyNameLabel("Wigell AutoCore");
        Label companyAddressLabel1 = new Label("Låtsasgatan 15");
        Label companyAddressLabel2 = new Label("855 92 Ankeborg");
        Label organizationNumberLabel = new Label("Org.nr. 12464-4544");

        VBox companyBox = UIComponents.createCompanyInfoBox();
        companyBox.getChildren().addAll(companyNameLabel, companyAddressLabel1,
                companyAddressLabel2, organizationNumberLabel);

        HBox companyRow = new HBox(companyBox);
        companyRow.setAlignment(Pos.TOP_RIGHT);

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

        if (invoice.isPaid()) {
            statusValue.setText(languageManager.getString("paid"));
        } else {
            statusValue.setText(languageManager.getString("unpaid"));
        }

        serviceColumn.setText(languageManager.getString("serviceColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        discountColumn.setText(languageManager.getString("discountColumn"));
        totalAmount.setText(languageManager.getString("totalPriceColumn"));
        totalTitle.setText(languageManager.getString("totalPriceColumn"));
    }

    public void setOnBack(Runnable action) {
        backButton.setOnAction(e -> action.run());
    }
}

