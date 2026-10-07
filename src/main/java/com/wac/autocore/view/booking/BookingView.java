package com.wac.autocore.view.booking;

import com.wac.autocore.dao.CustomerDAO;
import com.wac.autocore.dao.VehicleDAO;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.dao.ServiceItemDAO;

import com.wac.autocore.view.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    //Alexander
    //tabellen är ett fält så att controllern kan läsa de valda tjänsterna
    private TableView<ServiceItem> servicesTable;

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
        bookingSubtitle = UIComponents.createSubtitle(languageManager.getString("bookingSubtitle"));

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
        customerLabel.getStyleClass().add("form-label");
        customerBox.getChildren().addAll(customerLabel, customerComboBox);

        VBox vehicleBox = new VBox(5);
        vehicleLabel = new Label(languageManager.getString("vehicleLabel"));
        vehicleLabel.getStyleClass().add("form-label");
        vehicleBox.getChildren().addAll(vehicleLabel, vehicleComboBox);

        VBox dateBox = new VBox(5);
        dateLabel = new Label(languageManager.getString("dateLabel"));
        dateLabel.getStyleClass().add("form-label");
        dateBox.getChildren().addAll(dateLabel, date);

        VBox descriptionBox = new VBox(5);
        descriptionLabel = new Label(languageManager.getString("descriptionLabel"));
        descriptionLabel.getStyleClass().add("form-label");
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
        selectServiceLabel.getStyleClass().add("form-label");

        selectedServicesLabel = new Label(languageManager.getString("selectedServices"));
        selectedServicesLabel.getStyleClass().add("form-label");
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
        servicesTable = UIComponents.createTable();

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
                //Alexander
                // soptunnan centreras och får mindre luft så att raden inte blir högre än övriga
                setAlignment(Pos.CENTER);
                setStyle("-fx-padding: 2 4 2 4;");

                //Alexander - soptunnan tar bort tjänsten på den här raden ur listan med valda tjänster
                deleteButton.setOnAction(event -> getTableView().getItems().remove(getIndex()));
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
        //Alexander
        //bredderna summerar till under 100 % så att soptunnan inte hamnar bakom en rullningslist
        serviceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.44));
        timeColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.22));
        priceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.22));
        deleteColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.08));
        deleteColumn.setMinWidth(56);
        deleteColumn.setSortable(false);

        //Alexander
        //tabellen får alltid plats med minst tre valda tjänster
        servicesTable.setMinHeight(165);

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


        //Alexander
        //knappen "Lägg till tjänst" kopplas i BookingController (wireEvents)


        //sektion 3 -> summering
        summaryTitle = new Label("3. " + languageManager.getString("summaryTitle"));
        summaryTitle.getStyleClass().add("section-title");

        //rubriker
        totalTimeTitle = new Label(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle = new Label(languageManager.getString("estimatedTotalPrice"));

        // Värden
        totalTimeLabel = new Label("--");
        totalPriceLabel = new Label("--");

        //korten byggs av UIComponents
        VBox timeCard = UIComponents.createSummaryCard("fa-clock-o", totalTimeTitle, totalTimeLabel);
        VBox priceCard = UIComponents.createSummaryCard("fa-money", totalPriceTitle, totalPriceLabel);
        HBox summaryCards = UIComponents.createSummaryCards(timeCard, priceCard);

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

        // Listan används av controllern, men ska inte visas på "Create Booking"-sidan.
        bookingListView.setVisible(false);
        bookingListView.setManaged(false);

        box.getChildren().addAll(titleBox, bookingSubtitle, bookingInformation, servicesSection, summarySection, buttonBox, bookingListView);

        //sidan kan rullas när fönstret är för litet, så att summeringen och knapparna alltid går att nå
        ScrollPane scrollPane = new ScrollPane(box);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        this.root = scrollPane;

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

    //Alexander
    //används av BookingController för att koppla tjänsterna till GarageSystem
    public ComboBox<ServiceItem> getServiceComboBox() {return serviceComboBox;}
    public Button getAddServiceButton() {return addServiceButton;}
    public TableView<ServiceItem> getServicesTable() {return servicesTable;}

    //Alexander
    //visar summering, värdena räknas ut i GarageSystem, vyn visar dem bara.
    public void showSummary(String totalTime, String totalPrice) {
        totalTimeLabel.setText(totalTime);
        totalPriceLabel.setText(totalPrice);
    }

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

        descriptionField.setPromptText(languageManager.getString("descriptionField"));
        createBookingButton.setText(languageManager.getString("createBookingButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}