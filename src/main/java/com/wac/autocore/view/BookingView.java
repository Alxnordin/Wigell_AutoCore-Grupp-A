package com.wac.autocore.view;

import com.wac.autocore.dao.CustomerDAO;
import com.wac.autocore.dao.VehicleDAO;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.dao.ServiceItemDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;


//UI vy klass för att skapa en ny bokning (fordons-ID, datum, beskrivning).
public class BookingView {

    private final Parent root;

    private ComboBox<Customer> customerComboBox;
    private ComboBox<Vehicle> vehicleComboBox;
    private ComboBox<ServiceItem> serviceComboBox;
    private DatePicker date;
    private TextField descriptionField;

    private final ServiceItemDAO serviceItemDAO = new ServiceItemDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private TableColumn<ServiceItem, String> serviceColumn;
    private TableColumn<ServiceItem, String> timeColumn;
    private TableColumn<ServiceItem, String> priceColumn;
    private TableColumn<ServiceItem, Void> deleteColumn;

    private Button createBookingButton;
    private Button backButton;
    private ListView<String> bookingListView;

    private Label title;
    private Label bookingInformationTitle;
    private Label customerLabel;
    private Label vehicleLabel;
    private Label dateLabel;
    private Label descriptionLabel;
    private Label servicesTitle;
    private Label noServicesLabel;
    private Label selectServiceLabel;
    private Label selectedServicesLabel;
    private Button addServiceButton;
    private Label summaryTitle;
    private Label totalTimeLabel;
    private Label totalPriceLabel;
    private Label totalTimeTitle;
    private Label totalPriceTitle;
    private Label bookingSubtitle;

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingView () {
        //huvudcontainer
        VBox box = new VBox(18);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.TOP_LEFT);

        //titel
        title = new Label(languageManager.getString("bookingTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-calendar-plus-o");

        //underrubrik
        bookingSubtitle = new Label(languageManager.getString("bookingSubtitle"));
        bookingSubtitle.getStyleClass().add("page-subtitle");


        //sektion 1 -> bokningsinformation
        bookingInformationTitle = new Label("1. " + languageManager.getString("bookingInformationTitle"));
        bookingInformationTitle.getStyleClass().add("section-title");

        customerComboBox = UIComponents.createComboBox();
        vehicleComboBox = UIComponents.createComboBox();

        List<Customer> customers = customerDAO.findAll();
        List<Vehicle> vehicles = vehicleDAO.findAll();

        customerComboBox.getItems().addAll(customers);
        vehicleComboBox.getItems().addAll(vehicles);

        //visa namn i dropdown
        customerComboBox.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer == null ? "" : customer.getName();
            }
            @Override
            public Customer fromString(String string) {
                return null;
            }
        });

        //visa registreringsnummer i dropdown
        vehicleComboBox.setConverter(new StringConverter<Vehicle>() {
            @Override
            public String toString(Vehicle vehicle) {
                return vehicle == null ? "" : vehicle.getRegistrationNumber();
            }
            @Override
            public Vehicle fromString(String string) {
                return null;
            }
        });

        date = UIComponents.createDatePicker();

        descriptionField = UIComponents.createTextField();
        descriptionField.setPromptText(languageManager.getString("descriptionField"));

        VBox customerBox = new VBox(5);
        customerLabel = new Label(languageManager.getString("customerLabel"));
        customerBox.getChildren().addAll(customerLabel, customerComboBox);

        VBox vehicleBox = new VBox(5);
        vehicleLabel = new Label(languageManager.getString("vehicleLabel"));
        vehicleBox.getChildren().addAll(vehicleLabel, vehicleComboBox);

        VBox dateBox = new VBox(5);
        dateLabel = new Label(languageManager.getString("dateLabel"));
        dateBox.getChildren().addAll(dateLabel, date);

        VBox descriptionBox = new VBox(5);
        descriptionLabel = new Label(languageManager.getString("descriptionLabel"));
        descriptionBox.getChildren().addAll(descriptionLabel, descriptionField);

        HBox bookingFields = new HBox(15);
        bookingFields.getChildren().addAll(customerBox, vehicleBox, dateBox);

        VBox bookingInformation = UIComponents.createSectionBox();
        bookingInformation.setSpacing(15);
        bookingInformation.getChildren().addAll(bookingInformationTitle, bookingFields, descriptionBox);

        //sektion 2 -> service
        servicesTitle = new Label("2. " + languageManager.getString("servicesTitle"));
        servicesTitle.getStyleClass().add("section-title");
        selectServiceLabel = new Label(languageManager.getString("selectService"));

        selectedServicesLabel = new Label(languageManager.getString("selectedServices"));
        noServicesLabel = new Label(languageManager.getString("noServices"));
        noServicesLabel.getStyleClass().add("placeholder-text");
        addServiceButton = new Button(languageManager.getString("addService"));
        addServiceButton.getStyleClass().add("add-service-button");

        // Hämta tjänster från MySQL
        List<ServiceItem> serviceItems = serviceItemDAO.findAll();
        serviceComboBox = UIComponents.createComboBox();
        serviceComboBox.getItems().addAll(serviceItems);

        // Visar endast tjänstens namn i ComboBoxen
        serviceComboBox.setConverter(new StringConverter<ServiceItem>() {

            @Override
            public String toString(ServiceItem serviceItem) {
                return serviceItem == null ? "" : serviceItem.getName();}

            @Override
            public ServiceItem fromString(String string) {
                return null;}
        });


        // Välj tjänst + Lägg till
        HBox serviceSelectionBox = new HBox(10);
        serviceSelectionBox.setAlignment(Pos.CENTER_LEFT);
        serviceSelectionBox.getChildren().addAll(serviceComboBox, addServiceButton);


        // Servicetabell
        TableView<ServiceItem> servicesTable = UIComponents.createTable();

        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
        timeColumn = new TableColumn<>(languageManager.getString("timeColumn"));
        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));

        deleteColumn = new TableColumn<>();
        deleteColumn.setPrefWidth(60);

        serviceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        timeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstimatedMinutes() + " min"));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getPrice())));

        //radera raden
        deleteColumn.setCellFactory(column -> new TableCell<ServiceItem, Void>() {
            private final Button deleteButton = new Button();

            {
                FontIcon deleteIcon = new FontIcon("fa-trash");
                deleteIcon.setIconSize(18);
                deleteIcon.getStyleClass().add("delete-icon");

                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("delete-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        servicesTable.setPlaceholder(noServicesLabel);

        // Kolumnbredder - flytta till UIComponents??
        serviceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.47));
        timeColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.23));
        priceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.23));
        deleteColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.07));

        servicesTable.getColumns().addAll(serviceColumn, timeColumn, priceColumn, deleteColumn);

        // Hela sektion 2
        VBox servicesSection = UIComponents.createSectionBox();
        servicesSection.setSpacing(15);

        servicesSection.getChildren().addAll(
                servicesTitle,
                selectServiceLabel,
                serviceSelectionBox,
                selectedServicesLabel,
                servicesTable);


        // Lägg till vald tjänst i tabellen - kommer från backenden??
//        addServiceButton.setOnAction(event -> {
//            ServiceItem selectedService = serviceComboBox.getValue();
//
//            if (selectedService != null &&
//                    !servicesTable.getItems().contains(selectedService)) {
//
//                servicesTable.getItems().add(selectedService);
//                serviceComboBox.setValue(null);
//            }
//        });


        //sektion 3 -> summering
        summaryTitle = new Label("3. " + languageManager.getString("summaryTitle"));
        summaryTitle.getStyleClass().add("section-title");

        //rubriker
        totalTimeTitle = new Label(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle = new Label(languageManager.getString("estimatedTotalPrice"));

        // Ikoner
        FontIcon timeIcon = new FontIcon("fa-clock-o");
        timeIcon.setIconSize(30);
        timeIcon.getStyleClass().add("summary-icon");

        FontIcon priceIcon = new FontIcon("fa-money");
        priceIcon.setIconSize(30);
        priceIcon.getStyleClass().add("summary-icon");

        // Ikon + rubrik
        HBox timeTitleBox = UIComponents.createIconLabel(timeIcon, totalTimeTitle);
        HBox priceTitleBox = UIComponents.createIconLabel(priceIcon, totalPriceTitle);

        // Värden
        totalTimeLabel = new Label("--");
        totalPriceLabel = new Label("--");

        // Styling
        totalTimeTitle.getStyleClass().add("summary-label");
        totalPriceTitle.getStyleClass().add("summary-label");

        totalTimeLabel.getStyleClass().add("summary-value");
        totalPriceLabel.getStyleClass().add("summary-value");

        // Kort för tid
        VBox timeCard = new VBox(6);
        timeCard.getStyleClass().add("summary-card");
        timeCard.getChildren().addAll(
                timeTitleBox,
                totalTimeLabel
        );

        // Kort för pris
        VBox priceCard = new VBox(6);
        priceCard.getStyleClass().add("summary-card");
        priceCard.getChildren().addAll(priceTitleBox, totalPriceLabel);

        // Lägg korten bredvid varandra
        HBox summaryCards = new HBox(15);
        summaryCards.getStyleClass().add("summary-cards");

        summaryCards.getChildren().addAll(timeCard, priceCard);

        // Gör korten lika breda
        HBox.setHgrow(timeCard, Priority.ALWAYS);
        HBox.setHgrow(priceCard, Priority.ALWAYS);

        timeCard.setMaxWidth(Double.MAX_VALUE);
        priceCard.setMaxWidth(Double.MAX_VALUE);

        VBox summarySection = UIComponents.createSectionBox();
        summarySection.setSpacing(10);
        summarySection.getChildren().addAll(summaryTitle, summaryCards);

        // knappar
        createBookingButton = new Button(languageManager.getString("createBookingButton"));
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        buttonBox.getChildren().addAll(backButton, createBookingButton);

        //listview
        bookingListView = new ListView<>();

        // Listan används fortfarande av controllern, men ska inte visas på "Create Booking"-sidan.
        bookingListView.setVisible(false);
        bookingListView.setManaged(false);

        box.getChildren().addAll(titleBox, bookingSubtitle, bookingInformation, servicesSection, summarySection, buttonBox, bookingListView);
        this.root = box;

        //ändra språk
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();

            java.time.LocalDate selectedDate = date.getValue();
            DatePicker newDatePicker = UIComponents.createDatePicker();
            newDatePicker.setValue(selectedDate);
            dateBox.getChildren().set(1, newDatePicker);
            date = newDatePicker;
        });
    }

    public Parent getView() {return root;}

    public ComboBox<Customer> getCustomerComboBox() {return customerComboBox;}
    public ComboBox<Vehicle> getVehicleComboBox() {return vehicleComboBox;}
    public DatePicker getDate() {return date;}
    public TextField getDescriptionField() {return descriptionField;}
    public Button getCreateBookingButton() {return createBookingButton;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("bookingTitle"));
        bookingSubtitle.setText(languageManager.getString("bookingSubtitle"));
        bookingInformationTitle.setText("1. " + languageManager.getString("bookingInformationTitle"));
        vehicleLabel.setText(languageManager.getString("vehicleLabel"));
        dateLabel.setText(languageManager.getString("dateLabel"));
        descriptionLabel.setText(languageManager.getString("descriptionLabel"));
        servicesTitle.setText("2. " + languageManager.getString("servicesTitle"));
        customerLabel.setText(languageManager.getString("customerLabel"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        selectServiceLabel.setText(languageManager.getString("selectService"));
        selectedServicesLabel.setText(languageManager.getString("selectedServices"));
        timeColumn.setText(languageManager.getString("timeColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        noServicesLabel.setText(languageManager.getString("noServices"));
        addServiceButton.setText(languageManager.getString("addService"));
        summaryTitle.setText("3. " + languageManager.getString("summaryTitle"));
        totalTimeTitle.setText(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle.setText(languageManager.getString("estimatedTotalPrice"));

        totalTimeLabel.setText("--");
        totalPriceLabel.setText("--");

        descriptionField.setPromptText(languageManager.getString("descriptionField"));
        createBookingButton.setText(languageManager.getString("createBookingButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}
