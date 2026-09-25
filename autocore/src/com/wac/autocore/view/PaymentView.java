package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
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
    private Label invoiceLabel;
    private TextField workOrderIdField;
    private TextField discountCodeField;
    private Button createInvoiceButton;

    //Registrera betalning
    private Label paymentLabel;
    private TextField invoiceIdField;
    private ComboBox<String> paymentTypeComboBox;
    private Button processPaymentButton;

    private Label invoiceListLabel;
    private Label paymentListLabel;
    private ListView<String> invoiceListView;
    private ListView<String> paymentListView;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public PaymentView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        invoiceLabel = new Label(languageManager.getString("invoiceLabel"));
        workOrderIdField = new TextField();
        workOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));
        discountCodeField = new TextField();
        discountCodeField.setPromptText(languageManager.getString("discountCodeField"));
        createInvoiceButton = new Button(languageManager.getString("createInvoiceButton"));

        paymentLabel = new Label(languageManager.getString("paymentLabel"));
        invoiceIdField = new TextField();
        invoiceIdField.setPromptText(languageManager.getString("invoiceIdField"));
        paymentTypeComboBox = new ComboBox<>();
        paymentTypeComboBox.getItems().addAll(
                languageManager.getString("paymentTypeCard"),
                languageManager.getString("paymentTypeSwish"),
                languageManager.getString("paymentTypeCash")
        );

        //paymentTypeComboBox.getItems().addAll("CARD", "SWISH", "CASH");
        paymentTypeComboBox.setPromptText(languageManager.getString("paymentTypeComboBox"));
        processPaymentButton = new Button(languageManager.getString("processPaymentButton"));

        invoiceListLabel = new Label(languageManager.getString("invoiceListLabel"));
        invoiceListView = new ListView<>();

        paymentListLabel = new Label(languageManager.getString("paymentListLabel"));
        paymentListView = new ListView<>();

        backButton = new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(
                invoiceLabel, workOrderIdField, discountCodeField, createInvoiceButton,
                paymentLabel, invoiceIdField, paymentTypeComboBox, processPaymentButton,
                invoiceListLabel, invoiceListView,
                paymentListLabel, paymentListView,
                backButton
        );

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
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

    public void changeTextAllComponents() {
        invoiceLabel.setText(languageManager.getString("invoiceLabel"));
        workOrderIdField.setText(languageManager.getString("workOrderIdField"));
        discountCodeField.setText(languageManager.getString("discountCodeField"));
        createInvoiceButton.setText(languageManager.getString("createInvoiceButton"));
        paymentLabel.setText(languageManager.getString("paymentLabel"));

        invoiceIdField.setText(languageManager.getString("invoiceIdField"));

        paymentTypeComboBox.getItems().setAll(
                languageManager.getString("paymentTypeCard"),
                languageManager.getString("paymentTypeSwish"),
                languageManager.getString("paymentTypeCash")
        );
        paymentTypeComboBox.setPromptText(languageManager.getString("paymentTypeComboBox"));

        processPaymentButton.setText(languageManager.getString("processPaymentButton"));

        invoiceListLabel.setText(languageManager.getString("invoiceListLabel"));
        paymentListLabel.setText(languageManager.getString("paymentListLabel"));
        backButton.setText(languageManager.getString("backButton"));
    }
}
