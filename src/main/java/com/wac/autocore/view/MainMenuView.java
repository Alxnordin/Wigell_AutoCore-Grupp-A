package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


//UI klass för huvudmenyn med knappar till systemets olika delar
public class MainMenuView {

    private final Parent root;
    private final List<VBox> allSubMenus = new ArrayList<>();

    private Button showCustomersButton, addCustomerButton;
    private Button showVehiclesButton, addVehicleButton;
    private Button showBookingsButton, addBookingButton;
    private Button showServicesButton, showMechanicsButton;
    private Button showOrdersButton, addOrderButton;

    private Button showInvoicesButton, addInvoiceButton, showPaymentsButton, addPaymentButton;

    private Button exitButton;

    private Button customersButton;
    private Button vehiclesButton;
    private Button bookingsButton;
    private Button servicesButton;
    private Button ordersButton;
    private Button paymentsButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public MainMenuView() {

        VBox menu = new VBox();
        menu.getStyleClass().add("main-menu");

//        showCustomersButton = new Button(languageManager.getString("showCustomers"));
//        addCustomerButton = new Button(languageManager.getString("addCustomer"));
        customersButton = UIComponents.createMenuButton(languageManager.getString("customersButton"), "fa-user");
        menu.getChildren().add(buildSection(customersButton));

        showVehiclesButton = new Button(languageManager.getString("showVehicles"));
        addVehicleButton = new Button(languageManager.getString("addVehicle"));
        vehiclesButton = UIComponents.createMenuButton(languageManager.getString("vehiclesButton"), "fa-car");
        menu.getChildren().add(buildSection(vehiclesButton, showVehiclesButton, addVehicleButton));

        showBookingsButton = new Button(languageManager.getString("showBookings"));
        addBookingButton = new Button(languageManager.getString("addBooking"));
        bookingsButton = UIComponents.createMenuButton(languageManager.getString("bookingsButton"), "fa-calendar");
        menu.getChildren().add(buildSection(bookingsButton, showBookingsButton, addBookingButton));

        showServicesButton = new Button(languageManager.getString("showServices"));
        showMechanicsButton = new Button(languageManager.getString("showMechanicsButton"));
        servicesButton = UIComponents.createMenuButton(languageManager.getString("servicesButton"), "fa-wrench");
        menu.getChildren().add(buildSection(servicesButton, showServicesButton, showMechanicsButton));

        showOrdersButton = new Button(languageManager.getString("showOrders"));
        addOrderButton = new Button(languageManager.getString("addOrder"));

        ordersButton = new Button(languageManager.getString("ordersButton"));
        ordersButton = UIComponents.createMenuButton(languageManager.getString("ordersButton"), "fa-clipboard");

        menu.getChildren().add(buildSection(ordersButton, showOrdersButton, addOrderButton));

        showInvoicesButton = new Button(languageManager.getString("showInvoices"));
        addInvoiceButton = new Button(languageManager.getString("addInvoice"));
        showPaymentsButton = new Button(languageManager.getString("showPayments"));
        addPaymentButton = new Button(languageManager.getString("addPayment"));
        paymentsButton = UIComponents.createMenuButton(languageManager.getString("paymentsButton"), "fa-credit-card");
        menu.getChildren().add(buildSection(paymentsButton, showInvoicesButton, addInvoiceButton, showPaymentsButton, addPaymentButton));

        exitButton = new Button(languageManager.getString("quit"));
        exitButton.getStyleClass().add("exit-button");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        menu.getChildren().add(exitButton);

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });

        this.root = menu;
    }

    public void changeTextAllComponents() {
        customersButton.setText(languageManager.getString("customersButton"));
//        showCustomersButton.setText(languageManager.getString("showCustomers"));
//        addCustomerButton.setText(languageManager.getString("addCustomer"));

        vehiclesButton.setText(languageManager.getString("vehiclesButton"));
        showVehiclesButton.setText(languageManager.getString("showVehicles"));
        addVehicleButton.setText(languageManager.getString("addVehicle"));

        bookingsButton.setText(languageManager.getString("bookingsButton"));
        showBookingsButton.setText(languageManager.getString("showBookings"));
        addBookingButton.setText(languageManager.getString("addBooking"));

        servicesButton.setText(languageManager.getString("servicesButton"));
        showServicesButton.setText(languageManager.getString("showServices"));
        showMechanicsButton.setText(languageManager.getString("showMechanicsButton"));

        ordersButton.setText(languageManager.getString("ordersButton"));
        showOrdersButton.setText(languageManager.getString("showOrders"));
        addOrderButton.setText(languageManager.getString("addOrder"));

        paymentsButton.setText(languageManager.getString("paymentsButton"));
        showInvoicesButton.setText(languageManager.getString("showInvoices"));
        addInvoiceButton.setText(languageManager.getString("addInvoice"));
        showPaymentsButton.setText(languageManager.getString("showPayments"));
        addPaymentButton.setText(languageManager.getString("addPayment"));

        exitButton.setText(languageManager.getString("quit"));
    }

    private VBox buildSection(Button mainButton, Button... subButtons) {
        mainButton.getStyleClass().add("category-button");
        mainButton.setMaxWidth(Double.MAX_VALUE);
        mainButton.setAlignment(Pos.CENTER_LEFT);

        VBox subMenu = new VBox(subButtons);
        subMenu.getStyleClass().add("submenu");
        subMenu.setVisible(false);
        subMenu.setManaged(false);
        allSubMenus.add(subMenu);

        for (Button sub : subButtons) {
            sub.setMaxWidth(Double.MAX_VALUE);
        }

        mainButton.setOnAction(e -> toggleSubMenu(subMenu));

        VBox section = new VBox(mainButton, subMenu);
        return section;
    }

    private void toggleSubMenu(VBox subMenu) {
        boolean wasOpen = subMenu.isVisible();
        for (VBox s : allSubMenus) {
            s.setVisible(false);
            s.setManaged(false);
        }
        if (!wasOpen) {
            subMenu.setVisible(true);
            subMenu.setManaged(true);
        }
    }

    public Parent getView() { return root; }

//    public Button getShowCustomersButton() { return showCustomersButton; }
//    public Button getAddCustomerButton() { return addCustomerButton; }
    public Button getCustomersButton() { return customersButton; }
    public Button getShowVehiclesButton() { return showVehiclesButton; }
    public Button getAddVehicleButton() { return addVehicleButton; }
    public Button getShowBookingsButton() { return showBookingsButton; }
    public Button getAddBookingButton() { return addBookingButton; }
    public Button getShowServicesButton() { return showServicesButton; }
    public Button getShowMechanicsButton() { return showMechanicsButton; }
    public Button getShowOrdersButton() { return showOrdersButton; }
    public Button getAddOrderButton() { return addOrderButton; }
    //public Button getStartCompleteOrderButton() { return startCompleteOrderButton; }
    public Button getShowInvoicesButton() { return showInvoicesButton; }
    public Button getAddInvoiceButton() { return addInvoiceButton; }
    public Button getShowPaymentsButton() { return showPaymentsButton; }
    public Button getAddPaymentButton() { return addPaymentButton; }

    public Button getExitButton() {return exitButton;}
}