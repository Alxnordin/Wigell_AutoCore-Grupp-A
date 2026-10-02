package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class InvoiceView {

    private final Parent root;

    private Label invoiceLabel;
    private TextField workOrderIdField;
    private TextField discountCodeField;
    private Button createInvoiceButton;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceView() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        invoiceLabel = new Label(languageManager.getString("invoiceLabel"));

        workOrderIdField = new TextField();
        workOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));

        discountCodeField = new TextField();
        discountCodeField.setPromptText(languageManager.getString("discountCodeField"));

        createInvoiceButton = new Button(languageManager.getString("createInvoiceButton"));
        backButton = new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(invoiceLabel, workOrderIdField, discountCodeField,
                createInvoiceButton, backButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }

    public TextField getWorkOrderIdField() { return workOrderIdField; }
    public TextField getDiscountCodeField() { return discountCodeField; }
    public Button getCreateInvoiceButton() { return createInvoiceButton; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        invoiceLabel.setText(languageManager.getString("invoiceLabel"));
        workOrderIdField.setText(languageManager.getString("workOrderIdField"));
        discountCodeField.setText(languageManager.getString("discountCodeField"));
        createInvoiceButton.setText(languageManager.getString("createInvoiceButton"));
        backButton.setText(languageManager.getString("backButton"));
    }


}
