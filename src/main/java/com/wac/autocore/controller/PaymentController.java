package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.interf.PaymentService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.model.Payment;
import com.wac.autocore.validation.Validator;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.payment.PaymentView;
import javafx.scene.Parent;


public class PaymentController {

    private static final String[] PAYMENT_TYPE_CODES = {"CARD", "SWISH", "CASH"};

    private final PaymentService paymentService;

    private final AutoCoreApplication app;
    private final PaymentView paymentView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public PaymentController(PaymentService paymentService, AutoCoreApplication app, PaymentView paymentView) {
        this.paymentService = paymentService;
        this.app = app;
        this.paymentView = paymentView;
        wireEvents();
        refreshLists();

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshLists();
        });
    }

    private void wireEvents() {

        //Alexander
        //servicen kontrollerar betalningstyp och faktura, fel visas via ErrorHandler
        paymentView.getProcessPaymentButton().setOnAction(actionEvent -> {
            try {
                int invoiceId = Validator.requireInteger(
                        paymentView.getInvoiceIdField().getText(), "invalidInvoiceIdError");

                paymentService.processPayment(invoiceId, selectedPaymentTypeCode());

                refreshLists();
                paymentView.getInvoiceIdField().clear();
                paymentView.getPaymentTypeComboBox().setValue(null);
            } catch (Exception e) {
                ErrorHandler.show(e);
            }
        });

        paymentView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    //null om ingen betalningstyp är vald, annars koden (CARD, SWISH eller CASH)
    private String selectedPaymentTypeCode() {
        int index = paymentView.getPaymentTypeComboBox().getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= PAYMENT_TYPE_CODES.length) {
            return null;
        }
        return PAYMENT_TYPE_CODES[index];
    }

    private void refreshLists() {

        paymentView.getPaymentListView().getItems().clear();
        for (Payment payment : paymentService.findAll()) {

            String paymentInfo = payment.getId() + " | "
                    + languageManager.getString("invoiceIdInTable")
                    + ": " + payment.getInvoiceId() + " | "
                    + languageManager.getString("amountInTable")
                    + ": " + payment.getAmount() + " SEK | "
                    + languageManager.getString("paymentTypeInTable")
                    + ": " + payment.getPaymentType() + " | "
                    + languageManager.getString("dateInTable")
                    + ": " + payment.getPaymentDate() + " | "
                    + languageManager.getString("successfulInTable")
                    + ": " + (payment.isSuccessful()
                    ? languageManager.getString("yes")
                    : languageManager.getString("no"));

            paymentView.getPaymentListView().getItems().add(paymentInfo);
        }
    }

    public Parent getView() {
        return paymentView.getView();
    }
}