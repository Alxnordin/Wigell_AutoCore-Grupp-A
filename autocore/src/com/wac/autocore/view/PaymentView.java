package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


//UI för att visa fakturor och registrera betalningar
public class PaymentView {

    private final Parent root;

    //Skapa faktura
    private TextField workOrderIdField;
    private TextField discountCodeField;
    private Button createInvoiceButton;

    //Registrera betalning
    private TextField invoiceIdField;
    private ComboBox<String> paymentTypeComboBox;
    private Button processPaymentButton;

    private ListView<String> invoiceListView;
    private ListView<String> paymentListView;
    private Button backButton;

    public PaymentView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label invoiceLabel = new Label("Skapa faktura");
        workOrderIdField = new TextField();
        workOrderIdField.setPromptText("Arbetsorder-ID");
        discountCodeField = new TextField();
        discountCodeField.setPromptText("Rabattkod (valfritt)");
        createInvoiceButton = new Button("Skapa faktura");

        Label paymentLabel = new Label("Registrera betalning");
        invoiceIdField = new TextField();
        invoiceIdField.setPromptText("Faktura-ID");
        paymentTypeComboBox = new ComboBox<>();
        paymentTypeComboBox.getItems().addAll("CARD", "SWISH", "CASH");
        paymentTypeComboBox.setPromptText("Betalningstyp");
        processPaymentButton = new Button("Betala");

        Label invoiceListLabel = new Label("Fakturor");
        invoiceListView = new ListView<>();

        Label paymentListLabel = new Label("Betalningar");
        paymentListView = new ListView<>();

        backButton = new Button("Tillbaka");

        box.getChildren().addAll(
                invoiceLabel, workOrderIdField, discountCodeField, createInvoiceButton,
                paymentLabel, invoiceIdField, paymentTypeComboBox, processPaymentButton,
                invoiceListLabel, invoiceListView,
                paymentListLabel, paymentListView,
                backButton
        );

        this.root = box;
    }

    public Parent getView() { return root; }

    public TextField getWorkOrderIdField() { return workOrderIdField; }
    public TextField getDiscountCodeField() { return discountCodeField; }
    public Button getCreateInvoiceButton() { return createInvoiceButton; }

    public TextField getInvoiceIdField() { return invoiceIdField; }
    public ComboBox<String> getPaymentTypeComboBox() { return paymentTypeComboBox; }
    public Button getProcessPaymentButton() { return processPaymentButton; }

    public ListView<String> getInvoiceListView() { return invoiceListView; }
    public ListView<String> getPaymentListView() { return paymentListView; }
    public Button getBackButton() { return backButton; }
}
