package com.wac.autocore.view;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class InvoiceListView {

    private final Parent root;

    private Label title;
    private Label subtitle;

    private final TableView<Invoice> invoiceTable;
    private final TableColumn<Invoice, Integer> invoiceIdColumn;
    private final TableColumn<Invoice, Integer> workOrderIdColumn;
    private final TableColumn<Invoice, String> invoiceDateColumn;
    private final TableColumn<Invoice, Double> amountColumn;
    private final TableColumn<Invoice, Double> discountColumn;
    private final TableColumn<Invoice, Double> totalColumn;
    private final TableColumn<Invoice, Boolean> paidColumn;
    private final TableColumn<Invoice, Void> actionColumn;

    private Consumer<Invoice> onViewInvoice;
    private Button backButton;

    //DET HÄR SKA FLYTTAS
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
    //private TableColumn<String, String> discountColumn;
    private TableColumn<String, String> totalAmount;
    Label companyNameLabel;
    Label companyAddressLabel1;
    Label companyAddressLabel2;
    Label organizationNumberLabel;
    //FRAM TILL HIT

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceListView() {
        VBox box = UIComponents.createVBoxForViews();
        title = new Label(languageManager.getString("invoicesTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-list-alt");
        subtitle = UIComponents.createSubtitle(languageManager.getString("invoiceListSubtitle"));

        invoiceTable = UIComponents.createTable();

        invoiceIdColumn = new TableColumn<>(languageManager.getString("invoiceIdInTable"));
        workOrderIdColumn = new TableColumn<>(languageManager.getString("workOrderIdInTable"));
        invoiceDateColumn = new TableColumn<>(languageManager.getString("dateInTable"));
        amountColumn = new TableColumn<>(languageManager.getString("amountInTable"));
        discountColumn = new TableColumn<>(languageManager.getString("discountInTable"));
        totalColumn = new TableColumn<>(languageManager.getString("totalInTable"));
        paidColumn = new TableColumn<>(languageManager.getString("paidInTable"));
        actionColumn = new TableColumn<>(languageManager.getString("actionInTable"));
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        workOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("workOrderId"));
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        paidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));
        actionColumn.setCellFactory(column -> new TableCell<Invoice, Void>() {

            private final Button viewButton =
                    UIComponents.createViewButton(languageManager.getString("viewInvoice"));

            {
                viewButton.setOnAction(event -> {

                    Invoice invoice = getTableView().getItems().get(getIndex());
                    if (onViewInvoice != null) {
                        onViewInvoice.accept(invoice);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        invoiceTable.getColumns().addAll(invoiceIdColumn, workOrderIdColumn,
                invoiceDateColumn, amountColumn,
                discountColumn, totalColumn,
                paidColumn, actionColumn);
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        //DET HÄR SKA FLYTTAS
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
        //discountColumn = new TableColumn<>(languageManager.getString("discountColumn"));
        totalAmount = new TableColumn<>(languageManager.getString("totalPriceColumn"));
        serviceColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.45));
        priceColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.20));
        discountColumn.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.15));
        totalAmount.prefWidthProperty().bind(invoiceTableView.widthProperty().multiply(0.20));

//        invoiceTableView.getColumns().addAll(serviceColumn,
//                 priceColumn, discountColumn, totalAmount);

        companyNameLabel = new Label("Wigell AutoCore");
        companyAddressLabel1 = new Label("Låtsasgatan 15");
        companyAddressLabel2 = new Label("855 92 Ankeborg");
        organizationNumberLabel = new Label("Org.nr. 12464-4544");
        //FRAM TILL HIT

        box.getChildren().addAll(titleBox, subtitle, invoiceTable, backButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }
    public TableView<Invoice> getInvoiceTable() {return invoiceTable;}
    public Button getBackButton() { return backButton; }
    public void setOnViewInvoice(Consumer<Invoice> onViewInvoice) {
        this.onViewInvoice = onViewInvoice;
    }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("invoicesTitle"));
        subtitle.setText(languageManager.getString("invoiceListSubtitle"));

        invoiceIdColumn.setText(languageManager.getString("invoiceIdInTable"));
        workOrderIdColumn.setText(languageManager.getString("workOrderIdInTable"));
        invoiceDateColumn.setText(languageManager.getString("dateInTable"));
        amountColumn.setText(languageManager.getString("amountInTable"));
        discountColumn.setText(languageManager.getString("discountInTable"));
        totalColumn.setText(languageManager.getString("totalInTable"));
        paidColumn.setText(languageManager.getString("paidInTable"));
        actionColumn.setText(languageManager.getString("actionInTable"));

        backButton.setText(languageManager.getString("backButton"));

         //DET HÄR SKA FLYTTAS
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
        //FRAM TILL HIT
    }
}
