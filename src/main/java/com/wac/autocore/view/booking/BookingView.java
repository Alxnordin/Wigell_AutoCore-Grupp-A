package com.wac.autocore.view.booking;

import com.wac.autocore.dao.CustomerDAO;
import com.wac.autocore.dao.VehicleDAO;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.ServiceComponent;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePackage;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.LanguageManager;

import com.wac.autocore.view.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;


//UI vy klass för att skapa en ny bokning (fordons-ID, datum, beskrivning).
public class BookingView {

    private final Parent root;

    private ComboBox<Customer> customerComboBox;
    private ComboBox<Vehicle> vehicleComboBox;
    //Alexander
    //rullistan innehåller både enskilda tjänster och servicepaket (Composite)
    private ComboBox<ServiceComponent> serviceComboBox;
    private DatePicker date;
    private TextField descriptionField;

    //tabellen är ett fält så att controllern kan läsa de valda tjänsterna
    private TableView<ServiceItem> servicesTable;

    //ÄNDRA. Ska inte gå direkt till DAO
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private TableColumn<ServiceItem, String> serviceColumn;
    private TableColumn<ServiceItem, String> timeColumn;
    private TableColumn<ServiceItem, String> priceColumn;
    private TableColumn<ServiceItem, Void> deleteColumn;

    private Button createBookingButton;
    private Button cancelBookingButton;
    private Button backButton;

    private ListView<String> bookingListView;

    private Label title;
    private Button newBookingFromPrevoiusBookingButton;
    private Label bookingInformationTitle;
    private Label customerLabel;
    private Label vehicleRegistrationNumber;
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

    private Button newCustomerButton;
    private Button newVehicleButton;

    private FontIcon customerLockIcon;

    private VBox dateBox;

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingView () {
        VBox box = UIComponents.createVBoxForViews();
        box.setAlignment(Pos.TOP_LEFT);

        title = new Label(languageManager.getString("bookingTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-calendar-plus-o");

        newBookingFromPrevoiusBookingButton = UIComponents.createIsEditingButton(
                languageManager.getString("newBookingFromPrevoiusBookingButton"));
        newBookingFromPrevoiusBookingButton.setVisible(false);
        newBookingFromPrevoiusBookingButton.setManaged(false);
        titleBox.getChildren().add(newBookingFromPrevoiusBookingButton);

        bookingSubtitle = UIComponents.createSubtitle(languageManager.getString("bookingSubtitle"));

        VBox bookingInformation = buildSection1BookingInformationSection();
        VBox servicesSection = buildSection2ServiceInformation();
        VBox summarySection = buildSection3SummarySection();
        HBox buttonBottomBox = buildButtonBottomBox();

        bookingListView = new ListView<>();

        // Listan används av controllern, men ska inte visas på "Create Booking"-sidan.
        bookingListView.setVisible(false);
        bookingListView.setManaged(false);

        box.getChildren().addAll(titleBox, bookingSubtitle, bookingInformation,
                servicesSection, summarySection, buttonBottomBox, bookingListView);

        //sidan kan rullas när fönstret är för litet, så att summeringen och knapparna alltid går att nå
        ScrollPane scrollPane = new ScrollPane(box);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        this.root = scrollPane;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();

            java.time.LocalDate selectedDate = date.getValue();
            DatePicker newDatePicker = UIComponents.createDatePicker();
            newDatePicker.setValue(selectedDate);
            dateBox.getChildren().set(1, newDatePicker);
            date = newDatePicker;
        });
    }

    private VBox buildSection1BookingInformationSection() {
        bookingInformationTitle = new Label("1. " + languageManager.getString("bookingInformationTitle"));
        bookingInformationTitle.getStyleClass().add("section-title");

        customerComboBox = UIComponents.createComboBox();
        vehicleComboBox = UIComponents.createComboBox();


        //ÄNDRA. Ska inte gå direkt till DAO
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
        customerLabel = UIComponents.createFormLabel(languageManager.getString("customerLabel"));

        //Obs! Kolla hur lösa detta i UIComponent ist.
        StackPane customerComboBoxContainer = new StackPane(customerComboBox);
        customerLockIcon = new FontIcon("fa-lock");
        customerLockIcon.setIconSize(14);
        customerLockIcon.getStyleClass().add("locked-field-icon");
        customerLockIcon.setMouseTransparent(true);
        customerLockIcon.setVisible(false);
        customerComboBoxContainer.getChildren().add(customerLockIcon);
        StackPane.setAlignment(customerLockIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(customerLockIcon, new Insets(0, 14, 0, 0));

        //************** OBS EJ KLAR med knappen för ny kund, testar bara ***********
        newCustomerButton = new Button("+ Ny kund");
        newCustomerButton.getStyleClass().add("create-new-if-missing-button");
        newCustomerButton.setVisible(true);
        newCustomerButton.setManaged(true);
        customerBox.getChildren().addAll(customerLabel, customerComboBoxContainer, newCustomerButton);

        VBox vehicleBox = new VBox(5);
        vehicleRegistrationNumber = UIComponents.createFormLabel(languageManager.getString("vehicleRegistrationNumber"));

        //************** OBS EJ KLAR med knappen för nytt fordon, testar bara ***********
        newVehicleButton = new Button("+ Nytt fordon");
        newVehicleButton.getStyleClass().add("create-new-if-missing-button");
        newVehicleButton.setVisible(true);
        newVehicleButton.setManaged(true);
        vehicleBox.getChildren().addAll(vehicleRegistrationNumber, vehicleComboBox, newVehicleButton);

        dateBox = UIComponents.createVBoxWithSpacing5();
        dateLabel = UIComponents.createFormLabel(languageManager.getString("dateLabel"));
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

        return bookingInformation;
    }

    private VBox buildSection2ServiceInformation() {
        servicesTitle = UIComponents.createSectionTitle(languageManager.getString("servicesTitle"));
        selectServiceLabel = UIComponents.createFormLabel(languageManager.getString("selectService"));
        selectedServicesLabel = UIComponents.createFormLabel(languageManager.getString("selectedServices"));
        noServicesLabel = UIComponents.createPlaceholderLabel(languageManager.getString("noServices"));
        addServiceButton = UIComponents.createAddRemoveServiceButton(languageManager.getString("addService"));

        //Alexander
        //rullistan fylls av BookingController med tjänster och servicepaket
        serviceComboBox = UIComponents.createComboBox();

        //en tjänst visas med sitt namn, ett paket visas som "Paket: namn (antal tjänster)"
        serviceComboBox.setConverter(new StringConverter<ServiceComponent>() {

            @Override
            public String toString(ServiceComponent serviceComponent) {
                if (serviceComponent == null) {
                    return "";
                }
                if (serviceComponent instanceof ServicePackage) {
                    return languageManager.getString("packageInListLabel") + ": " + serviceComponent.getName()
                            + " (" + serviceComponent.getServiceItems().size() + " "
                            + languageManager.getString("packageInListServices") + ")";
                }
                return serviceComponent.getName();}

            @Override
            public ServiceComponent fromString(String string) {
                return null;}
        });

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
            private final Button deleteButton = UIComponents.createDeleteButton();
            {
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
        //bredderna summerar till under 100 % så att soptunnan inte hamnar bakom en rullningslist
        serviceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.44));
        timeColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.22));
        priceColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.22));
        deleteColumn.prefWidthProperty().bind(servicesTable.widthProperty().multiply(0.08));
        deleteColumn.setMinWidth(56);
        deleteColumn.setSortable(false);

        //tabellen får alltid plats med minst tre valda tjänster
        servicesTable.setMinHeight(165);
        servicesTable.getColumns().addAll(serviceColumn, timeColumn, priceColumn, deleteColumn);

        // Hela sektion 2
        VBox servicesSection = UIComponents.createSectionBox();
        servicesSection.setSpacing(15);

        servicesSection.getChildren().addAll(servicesTitle, selectServiceLabel,
                serviceSelectionBox, selectedServicesLabel, servicesTable);

        return servicesSection;
    }

    private VBox buildSection3SummarySection() {
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

        return summarySection;
    }

    private HBox buildButtonBottomBox() {
        HBox buttonBottomBox = new HBox();
        buttonBottomBox.setSpacing(10);

        createBookingButton = UIComponents.createCreateButton(languageManager.getString("createBookingButton"));

        cancelBookingButton = UIComponents.createCancelButton(languageManager.getString("cancelBookingButton"));
        cancelBookingButton.setVisible(false);
        cancelBookingButton.setManaged(false);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));
        backButton.setVisible(true);
        backButton.setManaged(true);

        buttonBottomBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBottomBox.getChildren().addAll(backButton, cancelBookingButton, createBookingButton);
        return buttonBottomBox;
    }

    public Parent getView() {return root;}

    public ComboBox<Customer> getCustomerComboBox() {return customerComboBox;}
    public ComboBox<Vehicle> getVehicleComboBox() {return vehicleComboBox;}
    public DatePicker getDate() {return date;}
    public TextField getDescriptionField() {return descriptionField;}
    public Button getCreateBookingButton() {return createBookingButton;}
    public Button getBackButton() {return backButton;}

    public void setOnCancelButton(Runnable action) {
        cancelBookingButton.setOnAction(e -> action.run());
    }

    //Alexander
    //används av BookingController för att koppla tjänsterna till GarageSystem
    public ComboBox<ServiceComponent> getServiceComboBox() {return serviceComboBox;}
    public Button getAddServiceButton() {return addServiceButton;}
    public TableView<ServiceItem> getServicesTable() {return servicesTable;}

    //Alexander
    //visar summering, värdena räknas ut i GarageSystem, vyn visar dem bara.
    public void showSummary(String totalTime, String totalPrice) {
        totalTimeLabel.setText(totalTime);
        totalPriceLabel.setText(totalPrice);
    }

    public void showWhenCreateBookingFromExistingBooking() {
        newBookingFromPrevoiusBookingButton.setVisible(true);
        newBookingFromPrevoiusBookingButton.setManaged(true);

        newCustomerButton.setVisible(false);
        newCustomerButton.setManaged(false);
        newVehicleButton.setVisible(false);
        newVehicleButton.setManaged(false);

        customerComboBox.setMouseTransparent(true);
        customerComboBox.setFocusTraversable(false);
        customerComboBox.getStyleClass().add("locked-combo-box");
        customerLockIcon.setVisible(true);

        cancelBookingButton.setVisible(true);
        cancelBookingButton.setManaged(true);

        backButton.setVisible(false);
        backButton.setManaged(false);
    }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("bookingTitle"));
        bookingSubtitle.setText(languageManager.getString("bookingSubtitle"));
        newBookingFromPrevoiusBookingButton.setText(languageManager.getString("newBookingFromPrevoiusBookingButton"));
        bookingInformationTitle.setText("1. " + languageManager.getString("bookingInformationTitle"));
        vehicleRegistrationNumber.setText(languageManager.getString("vehicleRegistrationNumber"));
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
        cancelBookingButton.setText(languageManager.getString("cancelBookingButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}