package com.wac.autocore;

import com.wac.autocore.controller.*;
import com.wac.autocore.model.Booking;
import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.service.*;
import com.wac.autocore.view.vehicle.VehicleListView;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.*;
import com.wac.autocore.view.booking.BookingListView;
import com.wac.autocore.view.booking.BookingView;
import com.wac.autocore.view.customer.CustomerListView;
import com.wac.autocore.view.customer.CustomerView;
import com.wac.autocore.view.invoice.InvoiceListView;
import com.wac.autocore.view.invoice.InvoiceView;
import com.wac.autocore.view.mechanic.MechanicView;
import com.wac.autocore.view.order.OrderFormView;
import com.wac.autocore.view.order.OrderListView;
import com.wac.autocore.view.payment.PaymentView;
import com.wac.autocore.view.service.ServiceItemView;
import com.wac.autocore.view.vehicle.VehicleView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

//JavaFX startpunkt, bygger upp scenen/fönstret, skapar vyer och kontroller, och styr växling mellan de olika vyerna
public class AutoCoreApplication extends Application {

    private final InvoiceLineService invoiceLineService = new InvoiceLineService();
    private final MechanicService mechanicService = new MechanicService();
    private final CustomerService customerService = new CustomerService();
    private final VehicleService vehicleService = new VehicleService(customerService);
    private final ServiceItemService serviceItemService = new ServiceItemService();

    private final BookingService bookingService =
            new BookingService(vehicleService, serviceItemService);

    private final WorkOrderService workOrderService = new WorkOrderService(
            bookingService, mechanicService, serviceItemService);

    private final InvoiceService invoiceService = new InvoiceService(
            workOrderService, bookingService, vehicleService,
            customerService, serviceItemService);

    private final PaymentService paymentService = new PaymentService(invoiceService);

    private BorderPane borderPane;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {

        DatabaseConnection.initializeDatabase();

        this.stage = primaryStage;
        stage.setTitle("Wigell AutoCore");

        borderPane = new BorderPane();

        Label headerLabel = new Label("WAC AutoCore");
        Button languageButton = new Button(LanguageManager.getInstance().getString("language"));

        Image logoImage = new Image(getClass().getResourceAsStream("/WAC_1.png"));
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(40);
        logoView.setPreserveRatio(true);

        StackPane header = new StackPane();
        // WAC AutoCore i mitten
        StackPane.setAlignment(headerLabel, Pos.CENTER);
        // Logotyp till vänster
        StackPane.setAlignment(logoView, Pos.CENTER_LEFT);
        StackPane.setMargin(logoView, new Insets(0, 0, 0, 15));
        // Språkknapp till höger
        StackPane.setAlignment(languageButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(languageButton, new Insets(0, 15, 0, 0));

        header.getChildren().addAll(logoView, headerLabel, languageButton);

        LanguageManager languageManager = LanguageManager.getInstance();
        languageButton.setOnAction(e -> {
            languageManager.changeLanguage();
        });
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            languageButton.setText(languageManager.getString("language"));
        });

        header.getStyleClass().add("header");
        borderPane.setTop(header);

        Label footerLabel = new Label("Wigell AutoCore  |  All rights reserved");
        HBox footer = new HBox(footerLabel);
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("footer");
        borderPane.setBottom(footer);

        new MainMenuController(this, borderPane);
        showMainMenu();

        showMainMenu();

        Scene scene = new Scene(borderPane, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void showMainMenu() {
        borderPane.setCenter(new StartView().getView());
    }

    public void showCustomerListView() {
        CustomerController customerController = new CustomerController(customerService,
                this, new CustomerView(), new CustomerListView());
        borderPane.setCenter(customerController.getCustomerListView());
    }

    public void showCustomerView() {
         CustomerController customerController = new CustomerController(customerService,
                this, new CustomerView(), new CustomerListView());
        borderPane.setCenter(customerController.getCustomerView());
    }

    public void showVehicleView() {
        VehicleController controller = new VehicleController(vehicleService, customerService,
                this, new VehicleView(), new VehicleListView());
        borderPane.setCenter(controller.getVehicleFormView());
    }

    public void showVehicleListView() {
        VehicleController controller = new VehicleController(vehicleService, customerService,
                this, new VehicleView(), new VehicleListView());
        borderPane.setCenter(controller.getVehicleListPane());
    }

    public void showServiceItemView() {
        ServiceController controller = new ServiceController(serviceItemService,
                this, new ServiceItemView(), new MechanicView());
        borderPane.setCenter(controller.getServiceItemView());
    }

    public void showMechanicView() {
        ServiceController controller = new ServiceController(serviceItemService,
                this, new ServiceItemView(), new MechanicView());
        borderPane.setCenter(controller.getMechanicView());
    }
    public void showBookingView() {
        BookingController controller = new BookingController(serviceItemService,
                bookingService, workOrderService,
                this, new BookingView(), new BookingListView());
        borderPane.setCenter(controller.getBookingFormView());
    }

    public void showBookingListView() {
        BookingController controller = new BookingController(serviceItemService,
                bookingService, workOrderService,
                this, new BookingView(), new BookingListView());
        borderPane.setCenter(controller.getBookingListPane());
    }

    public void showOrderFormView() {
        OrderController controller = new OrderController(bookingService,
                workOrderService, mechanicService, serviceItemService, vehicleService,
                customerService,
                this, new OrderFormView(), new OrderListView());
        borderPane.setCenter(controller.getOrderFormView());
    }

    //öppnar "Skapa order" med en bokning redan ifylld (från knappen i bokningslistan)
    public void showOrderFormView(Booking booking) {
        OrderController controller = new OrderController(bookingService,
                workOrderService, mechanicService, serviceItemService, vehicleService,
                customerService,
                this, new OrderFormView(), new OrderListView());
        controller.prefillFromBooking(booking);
        borderPane.setCenter(controller.getOrderFormView());
    }

    public void showOrderListView() {
        OrderController controller = new OrderController(bookingService,
                workOrderService, mechanicService, serviceItemService, vehicleService,
                customerService,
                this, new OrderFormView(), new OrderListView());
        borderPane.setCenter(controller.getOrderListPane());
    }

    public void showView(Parent view) {
        borderPane.setCenter(view);
    }

    public void showPaymentView() {
        borderPane.setCenter(new PaymentController(paymentService,
                this, new PaymentView()).getView());
    }

    public void showInvoiceListView() {
        InvoiceController invoiceController = new InvoiceController(invoiceService,
            workOrderService, bookingService, vehicleService, customerService,
                mechanicService, invoiceLineService,
                this, new InvoiceListView(), new InvoiceView());
        borderPane.setCenter(invoiceController.getInvoiceListView());
    }

    public void showInvoiceView() {
        InvoiceController invoiceController = new InvoiceController(invoiceService,
                workOrderService, bookingService, vehicleService, customerService,
                mechanicService, invoiceLineService,
                this, new InvoiceListView(), new InvoiceView());
        borderPane.setCenter(invoiceController.getInvoiceView());
    }

    public void exitApplication() {
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}