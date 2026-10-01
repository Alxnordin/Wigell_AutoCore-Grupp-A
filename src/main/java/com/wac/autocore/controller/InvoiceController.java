package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.InvoiceDAO;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.InvoiceView;
import javafx.scene.Parent;

public class InvoiceController {

    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final InvoiceView invoiceView;

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceController(GarageSystem garageSystem, AutoCoreApplication app, InvoiceView invoiceView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.invoiceView = invoiceView;
        wireEvents();
        refreshLists();

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshLists();
        });
    }

    private void wireEvents() {
        invoiceView.getCreateInvoiceButton().setOnAction(actionEvent -> {
            try {
                int workOrderId = Integer.parseInt(invoiceView.getWorkOrderIdField().getText());
                String discountCode = invoiceView.getDiscountCodeField().getText();

                Invoice invoice = garageSystem.createInvoice(workOrderId, discountCode);

                if (invoice != null) {
                    refreshLists();
                    invoiceView.getWorkOrderIdField().clear();
                    invoiceView.getDiscountCodeField().clear();
                }
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt arbetsorder-ID — måste vara ett heltal.");
            }
        });

        invoiceView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private void refreshLists() {
        invoiceView.getInvoiceListView().getItems().clear();

        for (Invoice invoice : invoiceDAO.findAll()) {
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

            invoiceView.getInvoiceListView().getItems().add(invoiceInfo);
        }
    }

    public Parent getView() {
        return invoiceView.getView();
    }
}
