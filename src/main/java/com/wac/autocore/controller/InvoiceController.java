package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.*;
import com.wac.autocore.model.*;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.invoice.InvoiceDetailsView;
import com.wac.autocore.view.invoice.InvoiceListView;
import com.wac.autocore.view.invoice.InvoiceView;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

public class InvoiceController {

    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final InvoiceListView invoiceListView;
    private final InvoiceView invoiceView;

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final MechanicDAO mechanicDAO = new MechanicDAO();
    private final InvoiceLineDAO invoiceLineDAO = new InvoiceLineDAO();

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceController(GarageSystem garageSystem, AutoCoreApplication app,
                             InvoiceListView invoiceListView, InvoiceView invoiceView) {
        this.garageSystem = garageSystem;
        this.app = app;
        this.invoiceListView = invoiceListView;
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
                String discountCode = invoiceView.getDiscountCodeComboBox().getValue();

                Invoice invoice = garageSystem.createInvoice(workOrderId, discountCode);

                if (invoice != null) {
                    refreshLists();
                    invoiceView.getWorkOrderIdField().clear();
                    invoiceView.getDiscountCodeComboBox().setValue(null);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ogiltigt arbetsorder-ID — måste vara ett heltal.");
            }
        });

        invoiceView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        invoiceListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        invoiceListView.setOnViewInvoice(invoice -> {

            WorkOrder workOrder = findWorkOrderById(invoice.getWorkOrderId());
            if (workOrder == null) {
                return;
            }

            Booking booking = findBookingById(workOrder.getBookingId());
            if (booking == null) {
                return;
            }

            Vehicle vehicle = findVehicleById(booking.getVehicleId());
            if (vehicle == null) {
                return;
            }

            Customer customer = findCustomerById(vehicle.getCustomerId());
            if (customer == null) {
                return;
            }

            Mechanic mechanic = findMechanicById(workOrder.getMechanicId());
            if (mechanic == null) {
                return;
            }

            List<InvoiceLine> invoiceLines = findInvoiceLinesByInvoiceId(invoice.getId());

            InvoiceDetailsView detailsView = new InvoiceDetailsView(invoice, booking,
                    vehicle, customer, mechanic, invoiceLines);

            detailsView.setOnBack(() -> {
                app.showView(invoiceListView.getView());
            });

            app.showView(detailsView.getView());

        });
    }

    private void refreshLists() {
        invoiceListView.getInvoiceTable().getItems().setAll(invoiceDAO.findAll());
    }

    private WorkOrder findWorkOrderById(int workOrderId) {

        for (WorkOrder workOrder : workOrderDAO.findAll()) {
            if (workOrder.getId() == workOrderId) {
                return workOrder;
            }
        }
        return null;
    }

    private Booking findBookingById(int bookingId) {

        for (Booking booking : bookingDAO.findAll()) {
            if (booking.getId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    private Vehicle findVehicleById(int vehicleId) {

        for (Vehicle vehicle : vehicleDAO.findAll()) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }

        return null;
    }

    private Customer findCustomerById(int customerId) {

        for (Customer customer : customerDAO.findAll()) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    private Mechanic findMechanicById(int mechanicId) {
        for (Mechanic mechanic : mechanicDAO.findAll()) {
            if (mechanic.getId() == mechanicId) {
                return mechanic;
            }
        }
        return null;
    }

    private List<InvoiceLine> findInvoiceLinesByInvoiceId(int invoiceId) {

        List<InvoiceLine> invoiceLines = new ArrayList<>();

        for (InvoiceLine invoiceLine : invoiceLineDAO.findAll()) {
            if (invoiceLine.getInvoiceId() == invoiceId) {
                invoiceLines.add(invoiceLine);
            }
        }

        return invoiceLines;
    }

    public Parent getInvoiceListView() {
        return invoiceListView.getView();
    }

    public Parent getInvoiceView() {
        return invoiceView.getView();
    }
}
