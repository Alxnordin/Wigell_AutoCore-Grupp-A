package com.wac.autocore.view.invoice;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InvoiceListView {

    private final Parent root;

    private Button createInvoiceButton;
    private Button paymentHistoryButton;

    private Label title;
    private Label subtitle;

    private TextField searchField;
    private ObservableList<Invoice> observableInvoiceList;
    private FilteredList<Invoice> filteredInvoiceList;

    private TableView<Invoice> invoiceTable;
    private TableColumn<Invoice, Integer> invoiceIdColumn;
    private TableColumn<Invoice, Integer> workOrderIdColumn;
    private TableColumn<Invoice, String> customerNameColumn;
    private TableColumn<Invoice, String> invoiceDateColumn;
    private TableColumn<Invoice, String> amountColumn;
    private TableColumn<Invoice, String> discountColumn;
    private TableColumn<Invoice, String> totalColumn;
    private TableColumn<Invoice, String> paidColumn;
    private TableColumn<Invoice, Void> actionColumn;

    private Map<Integer, String> customerNames = new HashMap<>();

    private Consumer<Invoice> onViewInvoice;
    private Consumer<Invoice> onRegisterPayment;

    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceListView() {
        VBox box = UIComponents.createVBoxForViews();
        title = new Label(languageManager.getString("invoicesTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-list-alt");
        subtitle = UIComponents.createSubtitle(languageManager.getString("invoiceListSubtitle"));

        createInvoiceButton = UIComponents.createCreateButton(languageManager.getString("createInvoiceButton"));

        paymentHistoryButton = UIComponents.createPaymentHistoryButton(languageManager.getString("paymentHistoryButton"));

        // Samlar rubriken och skapa-knappen på samma rad.
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Låter rubriken ta det tillgängliga utrymmet.
        titleBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        header.getChildren().addAll(
                titleBox,
                paymentHistoryButton,
                createInvoiceButton
        );

        TableView<Invoice> invoiceTable = buildTable();
        HBox searchBox = buildSearchSection();

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(header, subtitle, searchBox, invoiceTable, backButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    private HBox buildSearchSection() {
        searchField = new TextField();
        HBox searchBox = UIComponents.createSearchBox(searchField);
        searchField.setPromptText(languageManager.getString("searchTextField"));

        searchField.textProperty().addListener((
                observable, oldValue, newValue) -> {
            filteredInvoiceList.setPredicate(invoice -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

        String search = newValue.toLowerCase().trim();

                return String.valueOf(invoice.getId()).contains(search)
                        || String.valueOf(invoice.getWorkOrderId()).contains(search)
                        || customerNames.getOrDefault(invoice.getId(), "").
                        toLowerCase().contains(search)
                        || invoice.getInvoiceDate().toString().contains(search)
                        || (invoice.isPaid()
                        ? languageManager.getString("paid")
                        : languageManager.getString("unpaid"))
                        .toLowerCase().contains(search);
            });
        });

        observableInvoiceList = FXCollections.observableArrayList();
        filteredInvoiceList = new FilteredList<>(observableInvoiceList, invoice -> true);
        invoiceTable.setItems(filteredInvoiceList);

        return searchBox;
    }

    private TableView<Invoice> buildTable() {
        invoiceTable = UIComponents.createTable();

        invoiceIdColumn = new TableColumn<>(languageManager.getString("invoiceIdInTable"));
        workOrderIdColumn = new TableColumn<>(languageManager.getString("workOrderIdInTable"));
        customerNameColumn = new TableColumn<>(languageManager.getString("customer"));
        invoiceDateColumn = new TableColumn<>(languageManager.getString("dateInTable"));
        amountColumn = new TableColumn<>(languageManager.getString("amountInTable"));
        discountColumn = new TableColumn<>(languageManager.getString("discountInTable"));
        totalColumn = new TableColumn<>(languageManager.getString("totalInTable"));
        paidColumn = new TableColumn<>(languageManager.getString("status"));
        actionColumn = new TableColumn<>(languageManager.getString("actionInTable"));

        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        workOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("workOrderId"));

        customerNameColumn.setCellValueFactory(cellData -> {
            int invoiceId = cellData.getValue().getId();
            String customerName = customerNames.getOrDefault(invoiceId, "");
            return new SimpleStringProperty(customerName);
        });

        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));

        amountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UIComponents.formatPrice(cellData.getValue().getAmount())));

        discountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UIComponents.formatPrice(cellData.getValue().getDiscount())));

        totalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UIComponents.formatPrice(cellData.getValue().getTotalAmount())));

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

        // Fördelar tabellens utrymme jämnare mellan kolumnerna
        invoiceIdColumn.setPrefWidth(78);
        invoiceIdColumn.setMinWidth(70);

        workOrderIdColumn.setPrefWidth(65);
        workOrderIdColumn.setMinWidth(55);

        customerNameColumn.setPrefWidth(150);
        customerNameColumn.setMinWidth(130);

        invoiceDateColumn.setPrefWidth(100);
        invoiceDateColumn.setMinWidth(90);

        amountColumn.setPrefWidth(85);
        amountColumn.setMinWidth(75);

        discountColumn.setPrefWidth(75);
        discountColumn.setMinWidth(70);

        totalColumn.setPrefWidth(90);
        totalColumn.setMinWidth(80);

        paidColumn.setPrefWidth(125);
        paidColumn.setMinWidth(115);

        // Behåller den bredare kolumnen för de två knapparna
        actionColumn.setPrefWidth(290);
        actionColumn.setMinWidth(270);

        actionColumn.setCellFactory(column -> new TableCell<Invoice, Void>() {
            // Knappen som visar fakturans detaljer
            private final Button viewButton =
                    UIComponents.createViewButton(
                            languageManager.getString("viewInvoice"));

            // Knappen som öppnar betalningsdialogen
            private final Button paymentButton =
                    UIComponents.createRegisterPaymentButton(
                            languageManager.getString("registerPaymentButton"));

            // Knapparna placeras bredvid varandra
            private final HBox buttons = new HBox(8, viewButton, paymentButton);

            {
                buttons.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                viewButton.setOnAction(event -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());

                    if (onViewInvoice != null) {
                        onViewInvoice.accept(invoice);
                    }
                });

                paymentButton.setOnAction(event -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());

                    if (onRegisterPayment != null) {
                        onRegisterPayment.accept(invoice);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0
                        || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Invoice invoice = getTableView().getItems().get(getIndex());

                    // Uppdaterar knapptexterna när språket ändras
                    viewButton.setText(languageManager.getString("viewInvoice"));

                    if (invoice.isPaid()) {
                        // Visar en dov grön knapp med bock när fakturan är betald.
                        paymentButton.setText(languageManager.getString("paid"));
                        paymentButton.setGraphic(
                                new org.kordamp.ikonli.javafx.FontIcon("fa-check"));

                        // Säkerställer att knappen inte går att klicka på.
                        paymentButton.setDisable(true);
                        paymentButton.setOpacity(1.0);

                        // Byter från betalningsknappens stil till den dova gröna stilen.
                        paymentButton.getStyleClass().remove("action-button");
                        paymentButton.getStyleClass().remove("register-payment-button");

                        if (!paymentButton.getStyleClass().contains("paid-button")) {
                            paymentButton.getStyleClass().add("paid-button");
                        }

                    } else {
                        // Visar knappen för att registrera betalning.
                        paymentButton.setText(
                                languageManager.getString("registerPaymentButton"));

                        org.kordamp.ikonli.javafx.FontIcon paymentIcon =
                                new org.kordamp.ikonli.javafx.FontIcon("fa-credit-card");

                        // Säkerställer att kortikonen är vit.
                        paymentIcon.setIconColor(javafx.scene.paint.Color.WHITE);
                        paymentIcon.setIconSize(14);
                        paymentButton.setGraphic(paymentIcon);

                        paymentButton.setDisable(false);
                        paymentButton.setOpacity(1.0);

                        // Tar bort stilen för betalda fakturor.
                        paymentButton.getStyleClass().remove("paid-button");

                        // Återställer grundstilen för aktiva åtgärdsknappar.
                        if (!paymentButton.getStyleClass().contains("action-button")) {
                            paymentButton.getStyleClass().add("action-button");
                        }

                        // Återställer stilen för knappen som registrerar betalning.
                        if (!paymentButton.getStyleClass().contains("register-payment-button")) {
                            paymentButton.getStyleClass().add("register-payment-button");
                        }
                    }
                    setGraphic(buttons);
                }
            }
        });

        // Visar fakturans status som en färgad etikett, likt Orders-vyn.
        paidColumn.setCellFactory(column -> new TableCell<Invoice, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null
                        || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Invoice invoice = getTableRow().getItem();
                boolean paid = invoice.isPaid();

                // Hämtar statusens översättning på valt språk.
                String statusText = languageManager.getString(
                        paid ? "paid" : "unpaid"
                );

                // Återanvänder den befintliga statuskomponenten.
                Label statusBadge =
                        UIComponents.createInvoiceStatusValueLabel(statusText, paid);

                setText(null);
                setAlignment(Pos.CENTER);
                setGraphic(statusBadge);
            }
        });

        invoiceTable.getColumns().addAll(invoiceIdColumn, workOrderIdColumn, customerNameColumn,
                invoiceDateColumn, amountColumn, discountColumn, totalColumn,
                paidColumn, actionColumn);
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return  invoiceTable;
    }

    public Parent getView() { return root; }

    public TableView<Invoice> getInvoiceTable() {return invoiceTable;}

    public Button getBackButton() { return backButton; }

    public void setOnViewInvoice(Consumer<Invoice> onViewInvoice) {
        this.onViewInvoice = onViewInvoice;
    }

    public void setInvoices(List<Invoice> invoices) {
        observableInvoiceList.setAll(invoices);
    }

    public void setCustomerNames(Map<Integer, String> customerNames) {
        this.customerNames = customerNames;
        invoiceTable.refresh();
    }

    // Gör det möjligt för controllern att hantera betalningsknappen
    public void setOnRegisterPayment(Consumer<Invoice> onRegisterPayment) {
        this.onRegisterPayment = onRegisterPayment;
    }

    // Gör betalningshistorikens knapp tillgänglig för controllern.
    public Button getPaymentHistoryButton() {
        return paymentHistoryButton;
    }

    // Gör skapa-faktura-knappen tillgänglig för controllern.
    public Button getCreateInvoiceButton() {
        return createInvoiceButton;
    }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("invoicesTitle"));
        subtitle.setText(languageManager.getString("invoiceListSubtitle"));

        invoiceIdColumn.setText(languageManager.getString("invoiceIdInTable"));
        workOrderIdColumn.setText(languageManager.getString("workOrderIdInTable"));
        customerNameColumn.setText(languageManager.getString("customer"));
        invoiceDateColumn.setText(languageManager.getString("dateInTable"));
        amountColumn.setText(languageManager.getString("amountInTable"));
        discountColumn.setText(languageManager.getString("discountInTable"));
        totalColumn.setText(languageManager.getString("totalInTable"));
        paidColumn.setText(languageManager.getString("status"));
        actionColumn.setText(languageManager.getString("actionInTable"));
        createInvoiceButton.setText(languageManager.getString("createInvoiceButton"));
        paymentHistoryButton.setText(languageManager.getString("paymentHistoryButton"));

        backButton.setText(languageManager.getString("backButton"));

        invoiceTable.refresh();
    }
}
