package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.PaymentView;
import javafx.scene.Parent;


//Kopplar PaymentView till GarageSystem — hanterar fakturor och betalningar.
public class PaymentController {

    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final PaymentView paymentView;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public PaymentController(GarageSystem garageSystem, AutoCoreApplication app, PaymentView paymentView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.paymentView = paymentView;
        wireEvents();
        refreshLists();

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshLists();
        });
    }

    private void wireEvents() {
        paymentView.getCreateInvoiceButton().setOnAction(actionEvent -> {
            try {
                int workOrderId = Integer.parseInt(paymentView.getWorkOrderIdField().getText());
                String discountCode = paymentView.getDiscountCodeField().getText();

                Invoice invoice = garageSystem.createInvoice(workOrderId, discountCode);

                if (invoice != null) {
                    refreshLists();
                    paymentView.getWorkOrderIdField().clear();
                    paymentView.getDiscountCodeField().clear();
                }
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt arbetsorder-ID — måste vara ett heltal.");
            }
        });

        paymentView.getProcessPaymentButton().setOnAction(actionEvent -> {
            String paymentType = paymentView.getPaymentTypeComboBox().getValue();

            if (paymentType == null) {
                System.out.println("Du måste välja en betalningstyp.");
                return;
            }

            try {
                int invoiceId = Integer.parseInt(paymentView.getInvoiceIdField().getText());

                Payment payment = garageSystem.processPayment(invoiceId, paymentType);

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
        paymentView.getInvoiceListView().getItems().clear();

        for (Invoice invoice : Database.getInvoices()) {
            String invoiceInfo = invoice.getId() + " | "
                    + languageManager.getString("workOrderIdInTable")
                    + ": " + invoice.getWorkOrderId() + " | "
                    + languageManager.getString("dateInTable")
                    + ": " + invoice.getInvoiceDate() + " | "
                    + languageManager.getString("amountInTable")
                    + ": " + invoice.getAmount() + " SEK | "
                    + languageManager.getString("discountInTable")
                    + ": " + invoice.getDiscount() + " SEK | "
                    + languageManager.getString("totalInTable")
                    + ": " + invoice.getTotalAmount() + " SEK | "
                    + languageManager.getString("paidInTable")
                    + ": " + (invoice.isPaid()
                    ? languageManager.getString("yes")
                    : languageManager.getString("no"));

            paymentView.getInvoiceListView().getItems().add(invoiceInfo);

        }

        paymentView.getPaymentListView().getItems().clear();
        for (Payment payment : Database.getPayments()) {
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
