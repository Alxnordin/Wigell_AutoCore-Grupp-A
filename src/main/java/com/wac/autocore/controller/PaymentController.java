package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.PaymentService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.model.Payment;
import com.wac.autocore.view.payment.PaymentView;
import javafx.scene.Parent;


public class PaymentController {

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

        paymentView.getProcessPaymentButton().setOnAction(actionEvent -> {
            String paymentType = paymentView.getPaymentTypeComboBox().getValue();

            if (paymentType == null) {
                System.out.println("Du måste välja en betalningstyp.");
                return;
            }

            try {
                int invoiceId = Integer.parseInt(paymentView.getInvoiceIdField().getText());

                Payment payment = paymentService.processPayment(invoiceId, paymentType);

                if (payment != null) {
                    refreshLists();
                    paymentView.getInvoiceIdField().clear();
                    paymentView.getPaymentTypeComboBox().setValue(null);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt faktura-ID — måste vara ett heltal.");
            }
        });

        paymentView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
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
