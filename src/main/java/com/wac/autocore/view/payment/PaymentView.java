package com.wac.autocore.view.payment;

import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
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

    //Registrera betalning
    private Label paymentLabel;
    private TextField invoiceIdField;
    private ComboBox<String> paymentTypeComboBox;
    private Button processPaymentButton;

    private Label paymentListLabel;
    private ListView<String> paymentListView;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public PaymentView() {
      VBox box = UIComponents.createVBoxForViews();

        paymentLabel = new Label(languageManager.getString("paymentLabel"));
        invoiceIdField = new TextField();
        invoiceIdField.setPromptText(languageManager.getString("invoiceIdField"));
        paymentTypeComboBox = new ComboBox<>();
        paymentTypeComboBox.getItems().addAll(
                languageManager.getString("paymentTypeCard"),
                languageManager.getString("paymentTypeSwish"),
                languageManager.getString("paymentTypeCash")
        );

        paymentTypeComboBox.setPromptText(languageManager.getString("paymentTypeComboBox"));
        processPaymentButton = new Button(languageManager.getString("processPaymentButton"));

        paymentListLabel = new Label(languageManager.getString("paymentListLabel"));
        paymentListView = new ListView<>();

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

             box.getChildren().addAll(
                paymentLabel, invoiceIdField, paymentTypeComboBox, processPaymentButton,
                paymentListLabel, paymentListView,
                backButton
        );

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }
    public TextField getInvoiceIdField() { return invoiceIdField; }
    public ComboBox<String> getPaymentTypeComboBox() { return paymentTypeComboBox; }
    public Button getProcessPaymentButton() { return processPaymentButton; }
    public ListView<String> getPaymentListView() { return paymentListView; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        paymentLabel.setText(languageManager.getString("paymentLabel"));

        invoiceIdField.setText(languageManager.getString("invoiceIdField"));

        paymentTypeComboBox.getItems().setAll(
                languageManager.getString("paymentTypeCard"),
                languageManager.getString("paymentTypeSwish"),
                languageManager.getString("paymentTypeCash")
        );
        paymentTypeComboBox.setPromptText(languageManager.getString("paymentTypeComboBox"));

        processPaymentButton.setText(languageManager.getString("processPaymentButton"));
        paymentListLabel.setText(languageManager.getString("paymentListLabel"));
        backButton.setText(languageManager.getString("backButton"));
    }
}
