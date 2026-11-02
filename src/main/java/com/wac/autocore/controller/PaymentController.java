
package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.interf.PaymentService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.payment.PaymentView;
import javafx.scene.Parent;

public class PaymentController {

    private final PaymentService paymentService;
    private final AutoCoreApplication app;
    private final PaymentView paymentView;
    private final LanguageManager languageManager =
            LanguageManager.getInstance();

    public PaymentController(PaymentService paymentService,
                             AutoCoreApplication app,
                             PaymentView paymentView) {
        this.paymentService = paymentService;
        this.app = app;
        this.paymentView = paymentView;

        wireEvents();
        refreshLists();

        // Uppdaterar historiken när användaren byter språk.
        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> refreshLists()
        );
    }

    private void wireEvents() {
        // Kopplar tillbaka-knappen till huvudmenyn.
        paymentView.getBackButton().setOnAction(
                event -> app.showInvoiceListView()
        );
    }

    private void refreshLists() {
        // Hämtar betalningarna som redan har sparats.
        paymentView.getPaymentTable()
                .getItems()
                .setAll(paymentService.findAll());
    }

    // Returnerar betalningsvyn så att den kan visas i huvudfönstret.
    public Parent getView() {
        return paymentView.getView();
    }
}
