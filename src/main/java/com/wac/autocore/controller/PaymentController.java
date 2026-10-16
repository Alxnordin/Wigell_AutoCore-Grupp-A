package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.interf.PaymentService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Payment;
import com.wac.autocore.validation.Validator;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.InfoDialog;
import com.wac.autocore.view.payment.PaymentView;
import javafx.scene.Parent;


public class PaymentController {

    //Alexander
    //koderna som servicen förstår, i samma ordning som i rullistan.
    //rullistan visar översatt text (t.ex. KORT/KONTANTER), så valet översätts till kod via positionen
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

                //betalningstypen som den visas i rullistan (översatt), används i bekräftelsen
                String paymentTypeText = paymentView.getPaymentTypeComboBox().getValue();

                Payment payment = paymentService.processPayment(invoiceId, selectedPaymentTypeCode());

                refreshLists();
                paymentView.getInvoiceIdField().clear();
                paymentView.getPaymentTypeComboBox().setValue(null);
                showPaymentConfirmation(payment, paymentTypeText);
            } catch (Exception e) {
                ErrorHandler.show(e);
            }
        });

        paymentView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    //Alexander
    //bekräftelse till användaren: betalningen är registrerad och vem bekräftelsen skickades till
    private void showPaymentConfirmation(Payment payment, String paymentTypeText) {
        String amount = String.format("%.2f", payment.getAmount());
        Customer customer = paymentService.findCustomerForPayment(payment);

        if (customer != null) {
            InfoDialog.show("paymentConfirmation", String.valueOf(payment.getInvoiceId()), amount,
                    paymentTypeText, customer.getName(), customer.getEmail());
        } else {
            InfoDialog.show("paymentConfirmationNoCustomer", String.valueOf(payment.getInvoiceId()), amount,
                    paymentTypeText);
        }
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