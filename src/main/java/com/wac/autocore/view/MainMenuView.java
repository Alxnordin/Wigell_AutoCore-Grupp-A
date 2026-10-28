package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


//UI klass för huvudmenyn med knappar till systemets olika delar
public class MainMenuView {

    private final Parent root;
    private final List<VBox> allSubMenus = new ArrayList<>();


    private Button customersButton;
    private Button vehiclesButton;
    private Button bookingsButton;
    private Button servicesButton;
    private Button ordersButton;
    private Button paymentsButton;

    private Button logoutButton;
    private Button exitButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public MainMenuView() {

        VBox menu = new VBox();
        menu.getStyleClass().add("main-menu");

        customersButton = UIComponents.createMenuButton(languageManager.getString("customersButton"), "fa-user");
        menu.getChildren().add(buildSection(customersButton));

        vehiclesButton = UIComponents.createMenuButton(languageManager.getString("vehiclesButton"), "fa-car");
        menu.getChildren().add(buildSection(vehiclesButton));

        // Skapar Booking-knappen utan undermeny.
        bookingsButton = UIComponents.createMenuButton(
                languageManager.getString("bookingsButton"),
                "fa-calendar");
        menu.getChildren().add(buildSection(bookingsButton));

        // Skapar Service-knappen utan undermeny.
        servicesButton = UIComponents.createMenuButton(
                languageManager.getString("servicesButton"),
                "fa-wrench");
        menu.getChildren().add(buildSection(servicesButton));

        ordersButton = new Button(languageManager.getString("ordersButton"));
        ordersButton = UIComponents.createMenuButton(languageManager.getString("ordersButton"), "fa-clipboard");
        menu.getChildren().add(buildSection(ordersButton));

        //skapar payment-knappen utan en undermeny
        paymentsButton = UIComponents.createMenuButton(languageManager.getString("paymentsButton"), "fa-credit-card");
        menu.getChildren().add(buildSection(paymentsButton));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Skapar en utloggningsknapp med ikon framför texten.
        logoutButton = UIComponents.createMenuButton(
                languageManager.getString("logoutButton"),
                "fa-sign-out"
        );

        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.getStyleClass().add("logout-button");

        exitButton = new Button(languageManager.getString("quit"));
        exitButton.getStyleClass().add("exit-button");
        exitButton.setMaxWidth(Double.MAX_VALUE);

        menu.getChildren().add(spacer);
        menu.getChildren().add(logoutButton);
        menu.getChildren().add(exitButton);

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });

        this.root = menu;
    }

    public void changeTextAllComponents() {
        customersButton.setText(languageManager.getString("customersButton"));

        vehiclesButton.setText(languageManager.getString("vehiclesButton"));
//        showVehiclesButton.setText(languageManager.getString("showVehicles"));
//        addVehicleButton.setText(languageManager.getString("addVehicle"));

        bookingsButton.setText(languageManager.getString("bookingsButton"));
//        showBookingsButton.setText(languageManager.getString("showBookings"));
//        addBookingButton.setText(languageManager.getString("addBooking"));

        servicesButton.setText(languageManager.getString("servicesButton"));

        ordersButton.setText(languageManager.getString("ordersButton"));

        paymentsButton.setText(languageManager.getString("paymentsButton"));

        logoutButton.setText(languageManager.getString("logoutButton"));
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

        // Öppnar undermenyn endast om sektionen faktiskt har undermenyknappar.
        if (subButtons.length > 0) {
            mainButton.setOnAction(e -> toggleSubMenu(subMenu));
        }

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

    public Button getServicesButton() {return servicesButton;}

    public Button getCustomersButton() { return customersButton; }
    public Button getVehiclesButton() {return vehiclesButton;}

    public Button getBookingsButton() {return bookingsButton;}

    public Button getOrdersButton() { return ordersButton; }
    public Button getPaymentsButton() {return paymentsButton;}

    public Button getLogoutButton(){return logoutButton;}
    public Button getExitButton() {return exitButton;}
}