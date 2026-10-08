package com.wac.autocore.view.booking;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class BookingDetailsView {

    private final Parent root;
    private final Booking booking;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    private Label title;
    private Label subtitle;

    private Button isEditingButton;
    private Button createNewChangeBookingButton;
    private Button saveEditingButton;
    private Button cancelEditingButton;

    private ContextMenu bookingActionsMenu;
    private Label editBookingTitle;
    private Label editBookingDescription;
    private Label createFromBookingTitle;
    private Label createFromBookingDescription;
    private Consumer<String> onBookingAction;

    private Label bookingInformationTitle;
    private Label bookingIdTitle;
    private Label customerTitle;
    private Label vehicleRegistrationNumberTitle;
    private Label dateTitle;
    private Label statusTitle;
    private Label descriptionTitle;

    private Label servicesTitle;
    private Label noServicesLabel;

    private TableView<ServiceItem> servicesTable;
    private TableColumn<ServiceItem, String> serviceColumn;
    private TableColumn<ServiceItem, String> timeColumn;
    private TableColumn<ServiceItem, String> priceColumn;

    private VBox editServicesBox;
    private ComboBox<ServiceItem> serviceComboBox;
    private Button addServiceButton;
    private Button removeServiceButton;

    private Label summaryTitle;
    private Label totalTimeTitle;
    private Label totalPriceTitle;
    private Label totalTimeValue;
    private Label totalPriceValue;

    private final Button backButton;

    public BookingDetailsView(Booking booking, Vehicle vehicle, Customer customer, List<ServiceItem> services) {

        this.booking = booking;

        VBox box = new VBox(18);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.TOP_LEFT);

        HBox header = buildHeaderSection();
        subtitle = UIComponents.createSubtitle(languageManager.getString("bookingDetailsSubtitle"));
        VBox bookingInformation = buildBookingInformationSection(customer, vehicle);
        VBox servicesSection = buildServicesSection(services);
        VBox summarySection = buildSummarySection(services);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(backButton);

        box.getChildren().addAll(header, subtitle, bookingInformation, servicesSection, summarySection, buttonBox);

        ScrollPane scrollPane = new ScrollPane(box);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        this.root = scrollPane;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}

    public ComboBox<ServiceItem> getServiceComboBox() {return serviceComboBox;}
    public Button getAddServiceButton() {return addServiceButton;}
    public Button getRemoveServiceButton() {return removeServiceButton;}
    public TableView<ServiceItem> getServicesTable() {return servicesTable;}

    public void setOnBack(Runnable action) {
        backButton.setOnAction(e -> action.run());
    }

    public void setOnBookingAction(Consumer<String> action) {
        this.onBookingAction = action;
    }

    public void showEditServices() {
        editServicesBox.setVisible(true);
        editServicesBox.setManaged(true);

        isEditingButton.setVisible(true);
        isEditingButton.setManaged(true);

        createNewChangeBookingButton.setVisible(false);
        createNewChangeBookingButton.setManaged(false);

        cancelEditingButton.setVisible(true);
        cancelEditingButton.setManaged(true);
        saveEditingButton.setVisible(true);
        saveEditingButton.setManaged(true);
    }

    public void showSummary(String totalTime, String totalPrice) {
        totalTimeValue.setText(totalTime);
        totalPriceValue.setText(totalPrice);
    }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("bookingDetailsTitle"));
        subtitle.setText(languageManager.getString("bookingDetailsSubtitle"));
        isEditingButton.setText(languageManager.getString("isEditingButton"));
        createNewChangeBookingButton.setText(languageManager.getString("createNewBookingButton"));
        editBookingTitle.setText(languageManager.getString("editBooking"));
        editBookingDescription.setText(languageManager.getString("editBookingDescription"));
        createFromBookingTitle.setText(languageManager.getString("createFromBooking"));
        createFromBookingDescription.setText(languageManager.getString("createFromBookingDescription"));

        bookingInformationTitle.setText("1. " + languageManager.getString("bookingInformationTitle"));
        bookingIdTitle.setText(languageManager.getString("bookingIdInTable"));
        customerTitle.setText(languageManager.getString("customerLabel"));
        vehicleRegistrationNumberTitle.setText(languageManager.getString("vehicleRegistrationNumber"));
        dateTitle.setText(languageManager.getString("dateLabel"));
        statusTitle.setText(languageManager.getString("statusInTable"));
        descriptionTitle.setText(languageManager.getString("descriptionLabel"));

        servicesTitle.setText("2. " + languageManager.getString("servicesTitle"));
        noServicesLabel.setText(languageManager.getString("noServices"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        timeColumn.setText(languageManager.getString("timeColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));

        summaryTitle.setText("3. " + languageManager.getString("summaryTitle"));
        totalTimeTitle.setText(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle.setText(languageManager.getString("estimatedTotalPrice"));

        serviceComboBox.setPromptText(languageManager.getString("selectService"));
        addServiceButton.setText(languageManager.getString("addService"));
        removeServiceButton.setText(languageManager.getString("removeService"));

        backButton.setText(languageManager.getString("backButton"));
    }

    private HBox buildHeaderSection() {

        title = new Label(languageManager.getString("bookingDetailsTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-calendar");

        isEditingButton = UIComponents.createIsEditingButton(languageManager.getString("isEditingButton"));
        isEditingButton.setVisible(false);
        isEditingButton.setManaged(false);

        titleBox.getChildren().add(isEditingButton);

        createNewChangeBookingButton = UIComponents.createCreateButton(languageManager.getString("createNewBookingButton"));

        saveEditingButton = UIComponents.createSaveEditingButton(languageManager.getString("saveEditingButton"));
        cancelEditingButton = UIComponents.createCancelEditingButton(languageManager.getString("cancelEditingButton"));
        saveEditingButton.setVisible(false);
        saveEditingButton.setManaged(false);
        cancelEditingButton.setVisible(false);
        cancelEditingButton.setManaged(false);

        bookingActionsMenu = new ContextMenu();

        editBookingTitle = new Label(languageManager.getString("editBooking"));
        editBookingDescription = new Label(languageManager.getString("editBookingDescription"));

        createFromBookingTitle = new Label(languageManager.getString("createFromBooking"));
        createFromBookingDescription = new Label(languageManager.getString("createFromBookingDescription"));

        MenuItem editBookingItem = UIComponents.createOrderMenuItem(editBookingTitle,
                editBookingDescription, "fa-pencil");

        MenuItem createFromBookingItem = UIComponents.createOrderMenuItem(createFromBookingTitle,
                createFromBookingDescription, "fa-copy");

        editBookingItem.setOnAction(event -> {
            if (onBookingAction != null) {
                onBookingAction.accept("edit");
            }
        });

        createFromBookingItem.setOnAction(event -> {
            if (onBookingAction != null) {
                onBookingAction.accept("createFromExisting");
            }
        });

        bookingActionsMenu.getItems().addAll(editBookingItem, createFromBookingItem);

        createNewChangeBookingButton.setOnAction(event -> {
            bookingActionsMenu.show(
                    createNewChangeBookingButton,
                    javafx.geometry.Side.BOTTOM,
                    0,
                    0
            );
        });
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, saveEditingButton,
                cancelEditingButton, createNewChangeBookingButton);

        return header;
    }

    private VBox buildBookingInformationSection(Customer customer, Vehicle vehicle) {
        bookingInformationTitle = UIComponents.createSectionTitle("1. " + languageManager.getString("bookingInformationTitle"));

        bookingIdTitle = UIComponents.createInfoLabel(languageManager.getString("bookingIdInTable"));
        Label bookingIdValue = UIComponents.createValueLabel(String.valueOf(booking.getId()));
        VBox bookingIdBox = UIComponents.createVBoxWithSpacing6();
        bookingIdBox.getChildren().addAll(bookingIdTitle, bookingIdValue);

        customerTitle = UIComponents.createInfoLabel(languageManager.getString("customerLabel"));
        Label customerValue = UIComponents.createValueLabel(customer.getName());
        VBox customerBox = UIComponents.createVBoxWithSpacing6();
        customerBox.getChildren().addAll(customerTitle, customerValue);

        vehicleRegistrationNumberTitle = UIComponents.createInfoLabel(languageManager.getString("vehicleRegistrationNumber"));
        Label vehicleRegistrationNumberValue = UIComponents.createValueLabel(vehicle.getRegistrationNumber());
        VBox vehicleBox = UIComponents.createVBoxWithSpacing6();
        vehicleBox.getChildren().addAll(vehicleRegistrationNumberTitle, vehicleRegistrationNumberValue);

        dateTitle = UIComponents.createInfoLabel(languageManager.getString("dateLabel"));
        Label dateValue = UIComponents.createValueLabel(String.valueOf(booking.getDate()));
        VBox dateBox = UIComponents.createVBoxWithSpacing6();
        dateBox.getChildren().addAll(dateTitle, dateValue);

        statusTitle = UIComponents.createInfoLabel(languageManager.getString("statusInTable"));
        Label statusValue = UIComponents.createValueLabel(booking.getStatus());
        VBox statusBox = UIComponents.createVBoxWithSpacing6();
        statusBox.getChildren().addAll(statusTitle, statusValue);

        HBox bookingFields = new HBox(45);
        bookingFields.getChildren().addAll(bookingIdBox, customerBox, vehicleBox, dateBox, statusBox);

        descriptionTitle = UIComponents.createInfoLabel(languageManager.getString("descriptionLabel"));

        String description = booking.getDescription();
        if (description == null || description.trim().isEmpty()) {
            description = "--";
        }

        Label descriptionValue = UIComponents.createValueLabel(description);
        descriptionValue.setWrapText(true);

        VBox descriptionBox = UIComponents.createVBoxWithSpacing6();
        descriptionBox.getChildren().addAll(descriptionTitle, descriptionValue);

        VBox bookingInformation = UIComponents.createSectionBox();
        bookingInformation.setSpacing(15);
        bookingInformation.getChildren().addAll(bookingInformationTitle, bookingFields, descriptionBox);
        return bookingInformation;
    }

    private VBox buildServicesSection(List<ServiceItem> services) {
     servicesTitle = UIComponents.createSectionTitle("2. " + languageManager.getString("servicesTitle"));

        servicesTable = UIComponents.createTable();

        noServicesLabel = UIComponents.createPlaceholderLabel(languageManager.getString("noServices"));
        servicesTable.setPlaceholder(noServicesLabel);

        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
        timeColumn = new TableColumn<>(languageManager.getString("timeColumn"));
        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));

        serviceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        timeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstimatedMinutes() + " min"));

        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getPrice())));

        serviceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.48));
        timeColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.24));
        priceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.24));

        servicesTable.setMinHeight(165);
        servicesTable.getColumns().addAll(serviceColumn, timeColumn, priceColumn);
        servicesTable.getItems().addAll(services);

        serviceComboBox = UIComponents.createComboBox();
        serviceComboBox.setPromptText(languageManager.getString("selectService"));

        addServiceButton = UIComponents.createAddRemoveServiceButton(languageManager.getString("addService"));
        removeServiceButton = UIComponents.createAddRemoveServiceButton(languageManager.getString("removeService"));

        HBox editServicesRow = new HBox(10);
        editServicesRow.setAlignment(Pos.CENTER_LEFT);
        editServicesRow.getChildren().addAll(serviceComboBox, addServiceButton, removeServiceButton);

        editServicesBox = new VBox(10);
        editServicesBox.getChildren().add(editServicesRow);
        editServicesBox.setVisible(false);
        editServicesBox.setManaged(false);

        VBox servicesSection = UIComponents.createSectionBox();
        servicesSection.setSpacing(15);
        servicesSection.getChildren().addAll(servicesTitle, servicesTable, editServicesBox);
        return servicesSection;
    }

    private VBox buildSummarySection(List<ServiceItem> services) {
        summaryTitle = UIComponents.createSectionTitle("3. " + languageManager.getString("summaryTitle"));

        totalTimeTitle = new Label(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle = new Label(languageManager.getString("estimatedTotalPrice"));

        int totalMinutes = 0;
        double totalPrice = 0;

        for (ServiceItem service : services) {
            totalMinutes += service.getEstimatedMinutes();
            totalPrice += service.getPrice();
        }

        totalTimeValue = new Label(totalMinutes + " min");
        totalPriceValue = new Label(String.format("%,.0f kr", totalPrice));

        VBox timeCard = UIComponents.createSummaryCard("fa-clock-o", totalTimeTitle, totalTimeValue);
        VBox priceCard = UIComponents.createSummaryCard("fa-money", totalPriceTitle, totalPriceValue);
        HBox summaryCards = UIComponents.createSummaryCards(timeCard, priceCard);

        VBox summarySection = UIComponents.createSectionBox();
        summarySection.setSpacing(10);
        summarySection.getChildren().addAll(summaryTitle, summaryCards);
        return summarySection;
    }

}