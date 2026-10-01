package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class InvoiceView {

    private final Parent root;

    //Skapa faktura
    private Label invoiceLabel;
    private TextField workOrderIdField;
    private TextField discountCodeField;
    private Button createInvoiceButton;

    private Label invoiceListLabel;
    private ListView<String> invoiceListView;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    private TableView<String> invoiceTableView;
    Label invoiceID;
    Label invoiceDate;
    //Label paidOrNot;
    Label customerNameLabel;
    Label customerPhoneLabel;
    Label vehicleLabel;
    Label mechanicNameLabel;

    private TableColumn<String, String> serviceColumn;
    private TableColumn<String, String> priceColumn;
    private TableColumn<String, String> discountColumn;
    private TableColumn<String, String> totalAmount;

    Label companyNameLabel;
    Label companyAddressLabel1;
    Label companyAddressLabel2;
    Label organizationNumberLabel;

    public InvoiceView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        //JAG ANVÄNDER INTE DETTA JUST NU
        invoiceLabel = new Label(languageManager.getString("invoiceLabel"));
        workOrderIdField = new TextField();
        workOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));
        discountCodeField = new TextField();
        discountCodeField.setPromptText(languageManager.getString("discountCodeField"));
        createInvoiceButton = new Button(languageManager.getString("createInvoiceButton"));
        invoiceListLabel = new Label(languageManager.getString("invoiceListLabel"));
        invoiceListView = new ListView<>();
        //FRAM TILL HIT

        backButton = new Button(languageManager.getString("backButton"));

        invoiceID = new Label(languageManager.getString("invoiceIDNumber"));
        invoiceDate = new Label(languageManager.getString("invoiceDate"));
        //paidOrNot = new Label("PaidOrNot:");
        customerNameLabel = new Label(languageManager.getString("customerNameLabel"));
        customerPhoneLabel = new Label(languageManager.getString("customerPhoneLabel"));
        vehicleLabel = new Label(languageManager.getString("vehicleLabel"));
        mechanicNameLabel = new Label(languageManager.getString("mechanicNameLabel"));

        invoiceTableView = UIComponents.createTable();

        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));
        discountColumn = new TableColumn<>(languageManager.getString("discountColumn"));
        totalAmount = new TableColumn<>(languageManager.getString("totalPriceColumn"));
        serviceColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.45));
        priceColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.20));
        discountColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.15));
        totalAmount.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.20));

        invoiceTableView.getColumns().addAll(serviceColumn,
                 priceColumn, discountColumn, totalAmount);

        companyNameLabel = new Label("Wigell AutoCore");
        companyAddressLabel1 = new Label("Låtsasgatan 15");
        companyAddressLabel2 = new Label("855 92 Ankeborg");
        organizationNumberLabel = new Label("Org.nr. 12464-4544");

        box.getChildren().addAll(invoiceID, invoiceDate,
                customerNameLabel, customerPhoneLabel, vehicleLabel,
                mechanicNameLabel, invoiceTableView, companyNameLabel,
                companyAddressLabel1, companyAddressLabel2,
                organizationNumberLabel, backButton, workOrderIdField, discountCodeField,
                createInvoiceButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }

    public TextField getWorkOrderIdField() { return workOrderIdField; }
    public TextField getDiscountCodeField() { return discountCodeField; }
    public Button getCreateInvoiceButton() { return createInvoiceButton; }
    public ListView<String> getInvoiceListView() { return invoiceListView; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
       //JAG ANVÄNDER INTE DETTA JUST NU
        invoiceLabel.setText(languageManager.getString("invoiceLabel"));
        workOrderIdField.setText(languageManager.getString("workOrderIdField"));
        discountCodeField.setText(languageManager.getString("discountCodeField"));
        createInvoiceButton.setText(languageManager.getString("createInvoiceButton"));
        invoiceListLabel.setText(languageManager.getString("invoiceListLabel"));
        //FRAM TILL HIT

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

        backButton.setText(languageManager.getString("backButton"));
    }
}
