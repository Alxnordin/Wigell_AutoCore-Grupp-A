package com.wac.autocore;

import com.wac.autocore.security.LoginController;
import com.wac.autocore.view.service.ServiceOverviewView;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import com.wac.autocore.controller.*;
import com.wac.autocore.dao.*;
import com.wac.autocore.model.Booking;
import com.wac.autocore.repository.*;
import com.wac.autocore.service.impl.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.view.service.ServicePackageView;
import com.wac.autocore.data.DatabaseConnection;
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
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

//JavaFX startpunkt, bygger upp scenen/fönstret, skapar vyer och kontroller, och styr växling mellan de olika vyerna
public class AutoCoreApplication extends Application {

    private BorderPane borderPane;
    private Stage stage;

    //***** SPRING *******
    private CustomerService customerService;
    private MechanicService mechanicService;
    private ServiceItemService serviceItemService;
    private VehicleService vehicleService;
    private BookingService bookingService;
    private InvoiceLineService invoiceLineService;
    private InvoiceService invoiceService;
    private PaymentService paymentService;
    private ServicePackageService servicePackageService;
    private WorkOrderService workOrderService;

    private ConfigurableApplicationContext springContext;

    //Startar Spring via klassen SpringBootStart och sparar referens
    //till spring i variabeln springContext.
    //Hämtar sedan alla service som spring skapat för att kunna använda dem
    //vid skapandet av alla views. Spring skickar automatiskt in repository-klasserna
    //till service-klasserna.
    @Override
    public void init()  {
        springContext = SpringApplication.run(SpringBootStart.class);

        customerService = springContext.getBean(CustomerService.class);
        mechanicService = springContext.getBean(MechanicService.class);
        serviceItemService = springContext.getBean(ServiceItemService.class);
        vehicleService = springContext.getBean(VehicleService.class);
        bookingService = springContext.getBean(BookingService.class);
        invoiceLineService = springContext.getBean(InvoiceLineService.class);
        invoiceService = springContext.getBean(InvoiceService.class);
        paymentService = springContext.getBean(PaymentService.class);
        servicePackageService = springContext.getBean(ServicePackageService.class);
        workOrderService = springContext.getBean(WorkOrderService.class);
    }

    @Override
    public void start(Stage primaryStage) {

        //Alexander
        //körläget märks på alla loggrader, så att det syns att händelsen kom från JavaFX-appen
        MDC.put("mode", "JavaFX-app");

        //Alexander
        //fel som ingen har fångat på JavaFX-tråden loggas och visas via ErrorHandler
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) ->
                ErrorHandler.show(throwable instanceof Exception
                        ? (Exception) throwable
                        : new RuntimeException(throwable)));


        DatabaseConnection.initializeDatabase();

        this.stage = primaryStage;
        stage.setTitle("Wigell AutoCore");

        borderPane = new BorderPane();

        LanguageManager languageManager = LanguageManager.getInstance();

        FontIcon globeIcon = new FontIcon("fa-globe");
        globeIcon.setIconSize(18);

        Label languageLabel = new Label(
                languageManager.getLocale().getLanguage().equals("sv")
                        ? "SV"
                        : "EN"
        );

        HBox languageContent = new HBox(8, globeIcon, languageLabel);
        languageContent.setAlignment(Pos.CENTER);

        Button languageButton = new Button();
        languageButton.setGraphic(languageContent);
        languageButton.getStyleClass().add("language-button");

        languageButton.setOnAction(e -> {
            languageManager.changeLanguage();
        });

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            languageLabel.setText(
                    newValue.getLanguage().equals("sv")
                            ? "SV"
                            : "EN"
            );
        });

        //logotyp
        Image logoImage = new Image(getClass().getResourceAsStream("/WAC_1.png"));
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(60);
        logoView.setPreserveRatio(true);

        StackPane header = new StackPane();

        // Logotyp till vänster
        StackPane.setAlignment(logoView, Pos.CENTER_LEFT);
        StackPane.setMargin(logoView, new Insets(0, 0, 0, 15));
        // Språkknapp till höger
        StackPane.setAlignment(languageButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(languageButton, new Insets(0, 15, 0, 0));

        header.getChildren().addAll(logoView, languageButton);

        languageButton.setOnAction(e -> {
            languageManager.changeLanguage();
        });

        header.getStyleClass().add("header");
        borderPane.setTop(header);

        Label footerLabel = new Label("Wigell AutoCore  |  All rights reserved");
        HBox footer = new HBox(footerLabel);
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("footer");
        borderPane.setBottom(footer);

        //new MainMenuController(this, borderPane);
        //showMainMenu();

        //showMainMenu();

        showLoginView();

        Scene scene = new Scene(borderPane, 1300, 780);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    //Den första viewn när man startar programmet. Om man loggar in så visas sen showMainMenu
    public void showLoginView(){
        borderPane.setLeft(null);
        LoginController loginController = springContext.getBean(LoginController.class);

        LoginView loginView = new LoginView();

        loginView.getLoginButton().setOnAction(event -> {
            loginView.clearError();

        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (loginController.login(username, password)){
            new MainMenuController(this, borderPane);
            showMainMenu();
        }else{
            loginView.showError("Felaktigt användarnamn eller lösenord.");
            }
        });
        borderPane.setCenter(loginView.getView());
    }

    public void logout(){
        LoginController loginController = springContext.getBean(LoginController.class);

        loginController.logout();
        showLoginView();
    }

    //kontrollerar att användaren är inloggad
    private Boolean isUserLoggedIn(){
        return SecurityContextHolder.getContext()
                .getAuthentication() != null && SecurityContextHolder.getContext()
                .getAuthentication().isAuthenticated() && !"anonymousUser".equals(
                        SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal()
        );
    }


    public void showMainMenu() {
        if (!isUserLoggedIn()) {
            showLoginView();
            return;
        }

        // Skapar startsidan med de services som Spring redan har hämtat.
        StartView startView = new StartView(
                bookingService,
                workOrderService,
                vehicleService,
                invoiceService,
                customerService,
                serviceItemService
        );

        borderPane.setCenter(startView.getView());
    }


    //Hjälpmetod som kontrollerar användarens inloggningsstatus. används i varje navigering
    private boolean requireLogin(){
        if(!isUserLoggedIn()) {
            showLoginView();
            return false;
        }
        return true;
    }

    public void showCustomerListView() {

        if(!requireLogin()) {
            return;
        }

        CustomerController customerController = new CustomerController(customerService,
                this, new CustomerView(), new CustomerListView());
        borderPane.setCenter(customerController.getCustomerListView());
    }

    public void showVehicleView() {

        if(!requireLogin()) {
            return;
        }
        VehicleController controller = new VehicleController(vehicleService, customerService,
                this, new VehicleView(), new VehicleListView());
        borderPane.setCenter(controller.getVehicleFormView());
    }

    public void showVehicleListView() {

        if(!requireLogin()) {
            return;
        }
        VehicleController controller = new VehicleController(vehicleService, customerService,
                this, new VehicleView(), new VehicleListView());
        borderPane.setCenter(controller.getVehicleListPane());
    }

    public void showServiceItemView() {

        if(!requireLogin()) {
            return;
        }
        ServiceItemView serviceItemView = new ServiceItemView();

        ServiceController controller = new ServiceController(serviceItemService,
                mechanicService, workOrderService,
                this, serviceItemView, new MechanicView());

        //Alexander
        //servicepaketen visas i en egen sektion på samma sida
        ServicePackageController packageController = new ServicePackageController(
                servicePackageService, serviceItemService, this, new ServicePackageView());

        serviceItemView.addSection(packageController.getView());
        borderPane.setCenter(controller.getServiceItemView());
    }

    public void showMechanicView() {

        if(!requireLogin()) {
            return;
        }
        ServiceController controller = new ServiceController(serviceItemService,  mechanicService,
                workOrderService,this, new ServiceItemView(), new MechanicView());
        borderPane.setCenter(controller.getMechanicView());
    }

    public void showBookingView() {
        if(!requireLogin()) {
            return;
        }
        BookingController controller = new BookingController(serviceItemService, servicePackageService,
                bookingService, workOrderService, vehicleService, customerService,
                this, new BookingView(), new BookingListView());
        borderPane.setCenter(controller.getBookingFormView());
    }

    public void showBookingListView() {
        if(!requireLogin()) {
            return;
        }
        BookingController controller = new BookingController(serviceItemService, servicePackageService,
                bookingService, workOrderService, vehicleService, customerService,
                this, new BookingView(), new BookingListView());
        borderPane.setCenter(controller.getBookingListPane());
    }

    public void showCreateWorkOrderView(Booking booking) {
        if(!requireLogin()) {
            return;
        }
        OrderController controller = new OrderController(
                bookingService,
                workOrderService,
                mechanicService,
                serviceItemService,
                servicePackageService,
                vehicleService,
                customerService,
                invoiceService,
                this,
                new OrderListView()
        );
        controller.openCreateWorkOrderView(booking);
    }

    public void showOrderListView() {

        if(!requireLogin()) {
            return;
        }
        OrderController controller = new OrderController(bookingService,
                workOrderService, mechanicService, serviceItemService,servicePackageService, vehicleService,
                customerService,
                invoiceService,
                this,
                new OrderListView()
        );

        borderPane.setCenter(controller.getOrderListPane());
    }

    public void showView(Parent view) {

        if(!requireLogin()) {
            return;
        }
        borderPane.setCenter(view);
    }

    public void showPaymentView() {
        if(!requireLogin()) {
            return;
        }
        borderPane.setCenter(new PaymentController(paymentService,
                this, new PaymentView()).getView());
    }

    public void showInvoiceListView() {
        if(!requireLogin()) {
            return;
        }
        InvoiceController invoiceController = new InvoiceController(invoiceService,
            workOrderService, bookingService, vehicleService, customerService,
                mechanicService, invoiceLineService,
                this, new InvoiceListView(), new InvoiceView(), paymentService);
        borderPane.setCenter(invoiceController.getInvoiceListView());
    }

    public void showInvoiceView() {
        if(!requireLogin()) {
            return;
        }
        InvoiceController invoiceController = new InvoiceController(invoiceService,
                workOrderService, bookingService, vehicleService, customerService,
                mechanicService, invoiceLineService,
                this, new InvoiceListView(), new InvoiceView(), paymentService);
        borderPane.setCenter(invoiceController.getInvoiceView());
    }


    // Visar översikten och kopplar kortens knappar till rätt vyer.
    public void showServiceOverviewView() {
        if (!requireLogin()) return;

        ServiceOverviewView view = new ServiceOverviewView();

        view.getServicesButton().setOnAction(
                e -> showServiceItemView()
        );

        view.getPackagesButton().setOnAction(
                e -> showServicePackageView()
        );

        view.getMechanicsButton().setOnAction(
                e -> showMechanicView()
        );

        borderPane.setCenter(view.getView());
    }

    // Visar servicepaketen i en egen vy.
    public void showServicePackageView() {
        if (!requireLogin()) return;

        ServicePackageController controller = new ServicePackageController(
                servicePackageService,
                serviceItemService,
                this,
                new ServicePackageView()
        );

        borderPane.setCenter(controller.getView());
    }

    public void exitApplication() {
        Platform.exit();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}