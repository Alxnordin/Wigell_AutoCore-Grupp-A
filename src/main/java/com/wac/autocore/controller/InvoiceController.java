package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.*;
import com.wac.autocore.service.impl.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.validation.Validator;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.InfoDialog;
import com.wac.autocore.view.invoice.InvoiceDetailsView;
import com.wac.autocore.view.invoice.InvoiceListView;
import com.wac.autocore.view.invoice.InvoiceView;
import javafx.scene.Parent;

import java.util.List;

public class InvoiceController {

    private final InvoiceService invoiceService;
    private final WorkOrderService workOrderService;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final MechanicService mechanicService;
    private final InvoiceLineService invoiceLineService;

    private final AutoCoreApplication app;
    private final InvoiceListView invoiceListView;
    private final InvoiceView invoiceView;

    public InvoiceController(InvoiceService invoiceService, WorkOrderService workOrderService,
                             BookingService bookingService, VehicleService vehicleService,
                             CustomerService customerService, MechanicService mechanicService,
                             InvoiceLineService invoiceLineService, AutoCoreApplication app,
                             InvoiceListView invoiceListView, InvoiceView invoiceView) {
        this.invoiceService = invoiceService;
        this.workOrderService = workOrderService;
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.mechanicService = mechanicService;
        this.invoiceLineService = invoiceLineService;
        this.app = app;
        this.invoiceListView = invoiceListView;
        this.invoiceView = invoiceView;
        wireEvents();
        refreshLists();
    }

    private void wireEvents() {
        //Alexander
        //ID-fältet tolkas med den gemensamma Validator-regeln, och servicen kontrollerar resten.
        //alla fel visas via ErrorHandler (tidigare skrevs de bara ut i konsolen)
        invoiceView.getCreateInvoiceButton().setOnAction(actionEvent -> {
            try {
                int workOrderId = Validator.requireInteger(
                        invoiceView.getWorkOrderIdField().getText(), "invalidWorkOrderId");
                String discountCode = invoiceView.getDiscountCodeComboBox().getValue();

                Invoice invoice = invoiceService.createInvoice(workOrderId, discountCode);

                refreshLists();
                invoiceView.getWorkOrderIdField().clear();
                invoiceView.getDiscountCodeComboBox().setValue(null);
                showInvoiceConfirmation(invoice);
            } catch (Exception e) {
                ErrorHandler.show(e);
            }
        });

        invoiceView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        invoiceListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        invoiceListView.setOnViewInvoice(invoice -> {

            //Alexander: arbetsordern hämtas med fakturans arbetsorder-ID (tidigare användes fakturans eget ID)
            WorkOrder workOrder = workOrderService.findWorkOrder(invoice.getWorkOrderId());
            if (workOrder == null) {
                return;
            }

            Booking booking = bookingService.findBooking(workOrder.getBookingId());
            if (booking == null) {
                return;
            }

            Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());
            if (vehicle == null) {
                return;
            }

            Customer customer = customerService.findCustomer(vehicle.getCustomerId());
            if (customer == null) {
                return;
            }

            Mechanic mechanic = mechanicService.findMechanic(workOrder.getMechanicId());
            if (mechanic == null) {
                return;
            }

            List<InvoiceLine> invoiceLines = invoiceLineService.findInvoiceLinesByInvoiceId(invoice.getId());
            InvoiceDetailsView detailsView = new InvoiceDetailsView(invoice, booking,
                    vehicle, customer, mechanic, invoiceLines);

            detailsView.setOnBack(() -> {
                app.showView(invoiceListView.getView());
            });

            app.showView(detailsView.getView());

        });
    }

    //Alexander
    //bekräftelse till användaren: fakturan är skapad och vem aviseringen skickades till
    private void showInvoiceConfirmation(Invoice invoice) {
        String amount = String.format("%.2f", invoice.getTotalAmount());
        Customer customer = invoiceService.findCustomerForInvoice(invoice);

        if (customer != null) {
            InfoDialog.show("invoiceCreatedConfirmation", String.valueOf(invoice.getId()), amount,
                    customer.getName(), customer.getEmail());
        } else {
            InfoDialog.show("invoiceCreatedConfirmationNoCustomer", String.valueOf(invoice.getId()), amount);
        }
    }

    private void refreshLists() {
        invoiceListView.getInvoiceTable().getItems().setAll(invoiceService.findAll());
    }

    public Parent getInvoiceListView() {
        return invoiceListView.getView();
    }

    public Parent getInvoiceView() {
        return invoiceView.getView();
    }
}