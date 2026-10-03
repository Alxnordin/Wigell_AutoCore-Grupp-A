package com.wac.autocore.view.invoice;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
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
    private final TableColumn<Invoice, String> paidColumn;
    private final TableColumn<Invoice, Void> actionColumn;

    private Consumer<Invoice> onViewInvoice;

    private Button backButton;

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
        paidColumn = new TableColumn<>(languageManager.getString("statusLabel"));
        actionColumn = new TableColumn<>(languageManager.getString("actionInTable"));

        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        workOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("workOrderId"));
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        paidColumn.setCellValueFactory(cellData -> {
            Invoice invoice = cellData.getValue();
            String text;

            if (invoice.isPaid()) {
                text = languageManager.getString("paid");
            } else {
                text = languageManager.getString("unpaid");
            }

            return new SimpleStringProperty(text);
        });

        actionColumn.setCellFactory(column -> new TableCell<Invoice, Void>() {

            Button viewButton =
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
                    viewButton.setText(languageManager.getString("viewInvoice"));
                    setGraphic(viewButton);
                }
            }
        });


        invoiceTable.getColumns().addAll(invoiceIdColumn, workOrderIdColumn,
                invoiceDateColumn, amountColumn, discountColumn, totalColumn,
                paidColumn, actionColumn);
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

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
        paidColumn.setText(languageManager.getString("statusLabel"));
        actionColumn.setText(languageManager.getString("actionInTable"));

        backButton.setText(languageManager.getString("backButton"));

        invoiceTable.refresh();
    }
}
