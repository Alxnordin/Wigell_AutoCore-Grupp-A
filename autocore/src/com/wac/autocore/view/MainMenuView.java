package com.wac.autocore.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MainMenuView {

    private final Parent root;
    private final List<VBox> allSubMenus = new ArrayList<>();

    private Button showCustomersButton, addCustomerButton;
    private Button showVehiclesButton, addVehicleButton;
    private Button showBookingsButton, addBookingButton;
    private Button showServicesButton, showMechanicsButton;
    private Button showOrdersButton, addOrderButton, startCompleteOrderButton;
    private Button showInvoicesButton, addInvoiceButton, showPaymentsButton, addPaymentButton;
    private Button exitButton;

    public MainMenuView() {
        VBox menu = new VBox();
        menu.getStyleClass().add("main-menu");

        showCustomersButton = new Button("Visa kunder");
        addCustomerButton = new Button("Lägg till kund");
        menu.getChildren().add(buildSection("👤 Kunder", showCustomersButton, addCustomerButton));

        showVehiclesButton = new Button("Visa fordon");
        addVehicleButton = new Button("Lägg till fordon");
        menu.getChildren().add(buildSection("🚗 Fordon", showVehiclesButton, addVehicleButton));

        showBookingsButton = new Button("Visa bokningar");
        addBookingButton = new Button("Lägg till bokning");
        menu.getChildren().add(buildSection("📅 Bokning", showBookingsButton, addBookingButton));

        showServicesButton = new Button("Visa tjänster");
        showMechanicsButton = new Button("Visa mekaniker");
        menu.getChildren().add(buildSection("🛠 Service", showServicesButton, showMechanicsButton));

        showOrdersButton = new Button("Visa ordrar");
        addOrderButton = new Button("Skapa order");
        startCompleteOrderButton = new Button("Starta/slutför");
        menu.getChildren().add(buildSection("📋 Ordrar", showOrdersButton, addOrderButton, startCompleteOrderButton));

        showInvoicesButton = new Button("Visa fakturor");
        addInvoiceButton = new Button("Skapa faktura");
        showPaymentsButton = new Button("Visa betalningar");
        addPaymentButton = new Button("Utför betalning");
        menu.getChildren().add(buildSection("💳 Betalning", showInvoicesButton, addInvoiceButton, showPaymentsButton, addPaymentButton));

        exitButton = new Button("Avsluta");
        exitButton.getStyleClass().add("exit-button");
        exitButton.setMaxWidth(Double.MAX_VALUE);

        menu.getChildren().add(exitButton);

        this.root = menu;
    }

    private VBox buildSection(String title, Button... subButtons) {
        Button mainButton = new Button(title);
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

    public Button getShowCustomersButton() { return showCustomersButton; }
    public Button getAddCustomerButton() { return addCustomerButton; }
    public Button getShowVehiclesButton() { return showVehiclesButton; }
    public Button getAddVehicleButton() { return addVehicleButton; }
    public Button getShowBookingsButton() { return showBookingsButton; }
    public Button getAddBookingButton() { return addBookingButton; }
    public Button getShowServicesButton() { return showServicesButton; }
    public Button getShowMechanicsButton() { return showMechanicsButton; }
    public Button getShowOrdersButton() { return showOrdersButton; }
    public Button getAddOrderButton() { return addOrderButton; }
    public Button getStartCompleteOrderButton() { return startCompleteOrderButton; }
    public Button getShowInvoicesButton() { return showInvoicesButton; }
    public Button getAddInvoiceButton() { return addInvoiceButton; }
    public Button getShowPaymentsButton() { return showPaymentsButton; }
    public Button getAddPaymentButton() { return addPaymentButton; }

    public Button getExitButton() {return exitButton;}
}