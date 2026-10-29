package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.*;
import com.wac.autocore.service.impl.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.InfoDialog;
import com.wac.autocore.view.UIComponents;
import com.wac.autocore.view.invoice.InvoiceDetailsView;
import com.wac.autocore.view.invoice.InvoiceListView;
import com.wac.autocore.view.invoice.InvoiceView;
import com.wac.autocore.view.payment.RegisterPaymentDialog;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceController {

    private final AutoCoreApplication app;

    private final InvoiceService invoiceService;
    private final WorkOrderService workOrderService;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final MechanicService mechanicService;
    private final InvoiceLineService invoiceLineService;
    private final PaymentService paymentService;

    private final InvoiceListView invoiceListView;
    private final InvoiceView invoiceView;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceController(InvoiceService invoiceService, WorkOrderService workOrderService,
                             BookingService bookingService, VehicleService vehicleService,
                             CustomerService customerService, MechanicService mechanicService,
                             InvoiceLineService invoiceLineService, AutoCoreApplication app,
                             InvoiceListView invoiceListView, InvoiceView invoiceView,  PaymentService paymentService) {
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
        this.paymentService = paymentService;
        wireEvents();
        refreshLists();
    }

    private void wireEvents() {
        //Alexander
        //ID-fältet tolkas med den gemensamma Validator-regeln, och servicen kontrollerar resten.
        //alla fel visas via ErrorHandler (tidigare skrevs de bara ut i konsolen)
        invoiceView.getCreateInvoiceButton().setOnAction(actionEvent -> {
            try {
                // Hämtar arbetsordern som användaren valt i rullistan.
                WorkOrder selectedWorkOrder =
                        invoiceView.getWorkOrderComboBox().getValue();

                if (selectedWorkOrder == null) {
                    ErrorHandler.show(
                            new IllegalArgumentException(
                                    "Välj en arbetsorder först."));
                    return;
                }

                int workOrderId = selectedWorkOrder.getId();
                String discountCode = invoiceView.getDiscountCodeComboBox().getValue();

                Invoice invoice = invoiceService.createInvoice(workOrderId, discountCode);

                refreshLists();
                invoiceView.getWorkOrderComboBox().setValue(null);
                invoiceView.getDiscountCodeComboBox().setValue(null);
                showInvoiceConfirmation(invoice);
            } catch (Exception e) {
                ErrorHandler.show(e);
            }
        });

        // Öppnar formuläret för att skapa en faktura.
        invoiceListView.getCreateInvoiceButton().setOnAction(event ->
                app.showInvoiceView());

        invoiceView.getBackButton().setOnAction(actionEvent -> app.showInvoiceListView());
        invoiceListView.getPaymentHistoryButton().setOnAction(event -> app.showPaymentView());
        invoiceListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());

        invoiceListView.setOnViewInvoice(invoice -> {

            //Alexander: arbetsordern hämtas med fakturans arbetsorder-ID (tidigare användes fakturans eget ID)
            WorkOrder workOrder = workOrderService.findWorkOrder(invoice.getWorkOrderId());
            if (workOrder == null) {
                return;
            }

            Booking booking = null;
            Vehicle vehicle;
            Customer customer;

            if (workOrder.getBookingId() != null) {
                booking = bookingService.findBooking(workOrder.getBookingId());
                if (booking == null) {
                    return;
                }

                vehicle = vehicleService.findVehicle(booking.getVehicleId());
                if (vehicle == null) {
                    return;
                }

                customer = customerService.findCustomer(vehicle.getCustomerId());
                if (customer == null) {
                    return;
                }

            } else {
                vehicle = vehicleService.findVehicle(workOrder.getVehicleId());
                if (vehicle == null) {
                    return;
                }

                customer = customerService.findCustomer(workOrder.getCustomerId());
                if (customer == null) {
                    return;
                }
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

        // Öppnar dialogrutan och registrerar betalningen via den befintliga tjänsten.
        invoiceListView.setOnRegisterPayment(invoice -> {
            // Betalda fakturor ska inte kunna betalas igen.
            if (invoice.isPaid()) {
                return;
            }

            RegisterPaymentDialog dialog = new RegisterPaymentDialog(
                    invoice.getId(),
                    invoice.getTotalAmount()
            );

            dialog.showAndWait().ifPresent(details -> {
                try {
                    // Använder dialogens valda betalningstyp.
                    Payment payment = paymentService.processPayment(
                            details.getInvoiceId(),
                            details.getPaymentType()
                    );

                    // Hämtar fakturorna på nytt så att statusen uppdateras.
                    refreshLists();

                    // Visar bekräftelse när betalningen har registrerats.
                    showPaymentConfirmation(payment, details.getPaymentType());

                } catch (Exception e) {
                    ErrorHandler.show(e);
                }
            });
        });
    }

    // Hämtar färdigställda arbetsordrar som saknar faktura.
    private List<WorkOrder> getAvailableWorkOrdersForInvoice() {
        List<WorkOrder> availableWorkOrders = new java.util.ArrayList<>();

        for (WorkOrder workOrder : workOrderService.findAllWorkOrders()) {
            // Endast slutförda arbetsordrar får faktureras.
            if (!"COMPLETED".equals(workOrder.getStatus())) {
                continue;
            }

            // Hoppar över arbetsordrar som redan har en faktura.
            boolean alreadyInvoiced = invoiceService.findAll().stream()
                    .anyMatch(invoice ->
                            invoice.getWorkOrderId() == workOrder.getId());

            if (!alreadyInvoiced) {
                availableWorkOrders.add(workOrder);
            }
        }

        return availableWorkOrders;
    }

    // Visar en bekräftelse efter att betalningen har registrerats.
//    private void showPaymentConfirmation(Payment payment, String paymentTypeCode) {
//        String amount = String.format("%.2f", payment.getAmount());
//
//        String paymentTypeText;
//
//        switch (paymentTypeCode) {
//            case "CARD":
//                paymentTypeText = com.wac.autocore.util.LanguageManager
//                        .getInstance().getString("paymentTypeCard");
//                break;
//            case "SWISH":
//                paymentTypeText = com.wac.autocore.util.LanguageManager
//                        .getInstance().getString("paymentTypeSwish");
//                break;
//            case "CASH":
//                paymentTypeText = com.wac.autocore.util.LanguageManager
//                        .getInstance().getString("paymentTypeCash");
//                break;
//            default:
//                paymentTypeText = paymentTypeCode;
//        }
//
//        Customer customer = paymentService.findCustomerForPayment(payment);
//
//        if (customer != null) {
//            InfoDialog.show(
//                    "paymentConfirmation",
//                    String.valueOf(payment.getInvoiceId()),
//                    amount,
//                    paymentTypeText,
//                    customer.getName(),
//                    customer.getEmail()
//            );
//        } else {
//            InfoDialog.show(
//                    "paymentConfirmationNoCustomer",
//                    String.valueOf(payment.getInvoiceId()),
//                    amount,
//                    paymentTypeText
//            );
//        }
//    }

    private void showPaymentConfirmation(Payment payment, String paymentTypeCode) {
        String heading = languageManager.getString("confirmationPaymentHeading");
        String subHeading = languageManager.getString("confirmationPaymentSubHeading");

        String amount = String.format("%.2f", payment.getAmount());
        String paymentTypeText;

        switch (paymentTypeCode) {
            case "CARD":
                paymentTypeText = languageManager.getString("paymentTypeCard");
                break;
            case "SWISH":
                paymentTypeText = languageManager.getString("paymentTypeSwish");
                break;
            case "CASH":
                paymentTypeText = languageManager.getString("paymentTypeCash");
                break;

            default:
                paymentTypeText = paymentTypeCode;
        }

        Map<String, String> confirmationDetails = new LinkedHashMap<>();

        confirmationDetails.put(languageManager.getString("invoiceIdField"),
                String.valueOf(payment.getInvoiceId()));

        Customer customer = paymentService.findCustomerForPayment(payment);

        if (customer != null) {
            confirmationDetails.put(languageManager.getString("name"), customer.getName());
            confirmationDetails.put(languageManager.getString("emailLabel"), customer.getEmail());
        }

        confirmationDetails.put(languageManager.getString("paymentType"), paymentTypeText);
        confirmationDetails.put(languageManager.getString("amount"), amount);

        Alert alert = UIComponents.createConfirmationAlert(heading, subHeading,
                confirmationDetails);
        alert.showAndWait();
    }

    //Alexander
    //bekräftelse till användaren: fakturan är skapad och vem aviseringen skickades till
//    private void showInvoiceConfirmation(Invoice invoice) {
//        String amount = String.format("%.2f", invoice.getTotalAmount());
//        Customer customer = invoiceService.findCustomerForInvoice(invoice);
//
//        if (customer != null) {
//            InfoDialog.show("invoiceCreatedConfirmation", String.valueOf(invoice.getId()), amount,
//                    customer.getName(), customer.getEmail());
//        } else {
//            InfoDialog.show("invoiceCreatedConfirmationNoCustomer", String.valueOf(invoice.getId()), amount);
//        }
//    }

    private void showInvoiceConfirmation(Invoice invoice) {
        String heading = languageManager.getString("invoiceConfirmationHeading");
        String subHeading = languageManager.getString("invoiceConfirmationSubHeading");

        String amount = String.format("%.2f", invoice.getTotalAmount());
        Customer customer = invoiceService.findCustomerForInvoice(invoice);

        Map<String, String> confirmationDetails = new LinkedHashMap<>();
        confirmationDetails.put(languageManager.getString("invoiceNumber"), String.valueOf(invoice.getId()));
        confirmationDetails.put(languageManager.getString("customer"), customer.getName());
        confirmationDetails.put(languageManager.getString("amount"), amount);

        Alert alert = UIComponents.createConfirmationAlert(heading, subHeading, confirmationDetails);
        alert.showAndWait();
    }


    private void refreshLists() {
        // Uppdaterar översikten med befintliga fakturor.
        invoiceListView.getInvoiceTable().getItems()
                .setAll(invoiceService.findAll());

        // Visar bara arbetsordrar som är färdigställda och ännu inte fakturerade.
        invoiceView.getWorkOrderComboBox().getItems()
                .setAll(getAvailableWorkOrdersForInvoice());
    }

    public Parent getInvoiceListView() {
        return invoiceListView.getView();
    }

    public Parent getInvoiceView() {
        return invoiceView.getView();
    }
}