
package com.wac.autocore.view;

import com.wac.autocore.service.interf.ServiceItemService;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.model.WorkOrder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.control.cell.PropertyValueFactory;

public class StartView {

    private final Parent root;

    private final Label title;
    private final Label subtitle;
    private final Label instruction;

    // Etiketter som uppdateras när vi kopplar på riktig data.
    private final Label bookingsValue;
    private final Label workOrdersValue;
    private final Label vehiclesValue;
    private final Label invoicesValue;

    private final Label bookingsTitle;
    private final Label workOrdersTitle;
    private final Label vehiclesTitle;
    private final Label invoicesTitle;

    private Label summaryHeading;

    private TableView<BookingRow> bookingsTable;
    private TableView<WorkOrderRow> workOrdersTable;

    private TableColumn<BookingRow, String> bookingRegistrationColumn;
    private TableColumn<BookingRow, String> bookingCustomerColumn;
    private TableColumn<BookingRow, String> bookingDescriptionColumn;
    private TableColumn<BookingRow, String> bookingStatusColumn;

    private TableColumn<WorkOrderRow, String> workOrderIdColumn;
    private TableColumn<WorkOrderRow, String> workOrderRegistrationColumn;
    private TableColumn<WorkOrderRow, String> workOrderServiceColumn;
    private TableColumn<WorkOrderRow, String> workOrderStatusColumn;

    private final BookingService bookingService;
    private final WorkOrderService workOrderService;
    private final VehicleService vehicleService;
    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;

    private Label bookingsSectionTitle;
    private Label workOrdersSectionTitle;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public StartView(BookingService bookingService,
                     WorkOrderService workOrderService,
                     VehicleService vehicleService,
                     InvoiceService invoiceService,
                     CustomerService customerService,
                     ServiceItemService serviceItemService) {

        // Sparar services som skickas in från applikationen.
        this.bookingService = bookingService;
        this.workOrderService = workOrderService;
        this.vehicleService = vehicleService;
        this.invoiceService = invoiceService;
        this.customerService = customerService;
        this.serviceItemService = serviceItemService;

        // Samlar hela startsidan i en vertikal layout.
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(55, 30, 30, 30));
        mainContent.getStyleClass().add("start-view");

        // Välkomstsektion med text till vänster och bil till höger.
        HBox welcomeSection = new HBox(35);
        welcomeSection.setAlignment(Pos.CENTER_LEFT);
        welcomeSection.setMinHeight(170);

        VBox textSection = new VBox(18);
        textSection.setAlignment(Pos.CENTER_LEFT);
        textSection.setMaxWidth(550);
        HBox.setHgrow(textSection, Priority.ALWAYS);

        title = new Label(languageManager.getString("startTitle"));
        title.getStyleClass().add("start-title");
        title.setWrapText(true);

        subtitle = new Label(languageManager.getString("startSubtitle"));
        subtitle.getStyleClass().add("start-subtitle");
        subtitle.setWrapText(true);

        instruction = new Label(languageManager.getString("startInstruction"));
        instruction.getStyleClass().add("start-instruction");
        instruction.setWrapText(true);

        textSection.getChildren().addAll(
                title, subtitle, instruction);

        // Hämtar bilsilhuetten från resources-mappen.
        var imageUrl = getClass().getResource("/WAC_3.png");

        if (imageUrl == null) {
            throw new IllegalStateException(
                    "Kunde inte hitta bilden /WAC_3.png i resources.");
        }

        Image carImage = new Image(imageUrl.toExternalForm());
        ImageView carView = new ImageView(carImage);
        carView.setPreserveRatio(true);
        carView.setFitWidth(420);
        carView.setOpacity(0.35);
        carView.setMouseTransparent(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        welcomeSection.getChildren().addAll(
                textSection, spacer, carView);

        // Rubrik för sammanfattningen under välkomstsektionen.
        summaryHeading = new Label(languageManager.getString("startSummaryTitle"));
        summaryHeading.getStyleClass().add("section-title");

        // Skapar kortens rubriker och värden.
        bookingsTitle = new Label(languageManager.getString("startBookings"));
        bookingsValue = new Label("0");

        workOrdersTitle = new Label(languageManager.getString("startWorkOrders"));
        workOrdersValue = new Label("0");

        vehiclesTitle = new Label(languageManager.getString("startVehicles"));
        vehiclesValue = new Label("0");

        invoicesTitle = new Label(languageManager.getString("startUnpaidInvoices"));
        invoicesValue = new Label("0");

        // Återanvänder projektets gemensamma kortkomponent.
        VBox bookingsCard = UIComponents.createSummaryCard(
                "fa-calendar", bookingsTitle, bookingsValue);

        VBox workOrdersCard = UIComponents.createSummaryCard(
                "fa-wrench", workOrdersTitle, workOrdersValue);

        VBox vehiclesCard = UIComponents.createSummaryCard(
                "fa-car", vehiclesTitle, vehiclesValue);

        VBox invoicesCard = UIComponents.createSummaryCard(
                "fa-file-text", invoicesTitle, invoicesValue);

        // Ger startsidans kort en egen stil utan att påverka andra vyer.
        bookingsCard.getStyleClass().add("start-summary-card");
        workOrdersCard.getStyleClass().add("start-summary-card");
        vehiclesCard.getStyleClass().add("start-summary-card");
        invoicesCard.getStyleClass().add("start-summary-card");

        bookingsTitle.getStyleClass().add("start-summary-label");
        workOrdersTitle.getStyleClass().add("start-summary-label");
        vehiclesTitle.getStyleClass().add("start-summary-label");
        invoicesTitle.getStyleClass().add("start-summary-label");

        bookingsValue.getStyleClass().add("start-summary-value");
        workOrdersValue.getStyleClass().add("start-summary-value");
        vehiclesValue.getStyleClass().add("start-summary-value");
        invoicesValue.getStyleClass().add("start-summary-value");

        // Placerar samtliga fyra kort på en gemensam rad.
        HBox summaryCards = UIComponents.createSummaryCards(
                bookingsCard,
                workOrdersCard,
                vehiclesCard,
                invoicesCard);

        // Skapar rubriker och tabeller för dagens aktivitet.
        bookingsSectionTitle = new Label("Today's bookings");
        workOrdersSectionTitle = new Label("Ongoing work");

        bookingsSectionTitle.getStyleClass().add("section-title");
        workOrdersSectionTitle.getStyleClass().add("section-title");

        bookingsTable = createBookingsTable();
        workOrdersTable = createWorkOrdersTable();

        // Håller rubrik och tabell tillsammans.
        VBox bookingsSection = new VBox(12,
                bookingsSectionTitle, bookingsTable);

        VBox workOrdersSection = new VBox(12,
                workOrdersSectionTitle, workOrdersTable);

        // Låter sektionerna dela på tillgänglig bredd.
        HBox activityTables = new HBox(20,
                bookingsSection, workOrdersSection);

        HBox.setHgrow(bookingsSection, Priority.ALWAYS);
        HBox.setHgrow(workOrdersSection, Priority.ALWAYS);

        bookingsSection.setMaxWidth(Double.MAX_VALUE);
        workOrdersSection.setMaxWidth(Double.MAX_VALUE);

        // Visar välkomstsektionen, korten och dagens aktivitet.
        mainContent.getChildren().addAll(
                welcomeSection,
                summaryHeading,
                summaryCards,
                activityTables
        );

        // Gör startsidans innehåll scrollbar när det inte får plats på skärmen.
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Låter scrollområdet fylla hela den tillgängliga ytan.
        scrollPane.getStyleClass().add("start-scroll-pane");

        root = scrollPane;

        refreshDashboardData();

        // Uppdaterar alla synliga texter när användaren byter språk.
        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) ->
                        changeTextAllComponents());
    }


    //Skapar en tabell med exempel på dagens bokningar.
    //Tabellen är endast visuell tills vi kopplar på riktig data.

    private TableView<BookingRow> createBookingsTable() {
        TableView<BookingRow> table = UIComponents.createTable();

        // Skapar kolumner som kan byta språk utan att tabellen byggs om.
        bookingRegistrationColumn = new TableColumn<>();
        bookingCustomerColumn = new TableColumn<>();
        bookingDescriptionColumn = new TableColumn<>();
        bookingStatusColumn = new TableColumn<>();

        updateBookingColumnHeaders();

        bookingRegistrationColumn.setCellValueFactory(
                new PropertyValueFactory<>("registrationNumber"));
        bookingCustomerColumn.setCellValueFactory(
                new PropertyValueFactory<>("customer"));
        bookingDescriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        bookingStatusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        // Begränsar höjden så fyra rader visas åt gången.
        configureDashboardTable(table);

        // Visar status med projektets gemensamma statusbadge.
        bookingStatusColumn.setCellFactory(column ->
                new TableCell<BookingRow, String>() {
                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);

                        if (empty || status == null || status.isBlank()) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setText(null);
                            setGraphic(UIComponents.createStatusBadge(status));
                        }
                    }
                });

        table.getColumns().addAll(
                bookingRegistrationColumn,
                bookingCustomerColumn,
                bookingDescriptionColumn,
                bookingStatusColumn);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }


    // Uppdaterar bokningstabellens rubriker efter valt språk.
    private void updateBookingColumnHeaders() {
        bookingRegistrationColumn.setText(languageManager.getString("startTableRegistration"));
        bookingCustomerColumn.setText(languageManager.getString("startTableCustomer"));
        bookingDescriptionColumn.setText(languageManager.getString("startTableDescription"));
        bookingStatusColumn.setText(languageManager.getString("startTableStatus"));
    }


    //Skapar en tabell med exempel på pågående arbetsordrar.
    private TableView<WorkOrderRow> createWorkOrdersTable() {
        TableView<WorkOrderRow> table = UIComponents.createTable();

        // Skapar kolumner som kan uppdateras vid språkbyte.
        workOrderIdColumn = new TableColumn<>();
        workOrderRegistrationColumn = new TableColumn<>();
        workOrderServiceColumn = new TableColumn<>();
        workOrderStatusColumn = new TableColumn<>();

        updateWorkOrderColumnHeaders();

        workOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        workOrderRegistrationColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        workOrderServiceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        workOrderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Begränsar höjden så fyra rader visas åt gången.
        configureDashboardTable(table);

        // Återanvänder samma statusbadge som i bokningstabellen.
        workOrderStatusColumn.setCellFactory(column ->
                new TableCell<WorkOrderRow, String>() {
                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);

                        if (empty || status == null || status.isBlank()) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            String displayStatus = "IN_PROGRESS".equals(status)
                                    ? "In progress"
                                    : status;

                            setText(null);
                            setGraphic(UIComponents.createStatusBadge(displayStatus));
                        }
                    }
                });

        table.getColumns().addAll(
                workOrderIdColumn,
                workOrderRegistrationColumn,
                workOrderServiceColumn,
                workOrderStatusColumn);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    // Uppdaterar arbetsordertabellens rubriker efter valt språk.
    private void updateWorkOrderColumnHeaders() {
        workOrderIdColumn.setText(languageManager.getString("startWorkOrderTableId"));
        workOrderRegistrationColumn.setText(languageManager.getString("startTableRegistration"));
        workOrderServiceColumn.setText(languageManager.getString("startTableService"));
        workOrderStatusColumn.setText(languageManager.getString("startTableStatus"));
    }


    // Gemensam höjd för tabellerna på startsidan.
    private void configureDashboardTable(TableView<?> table) {
        double rowHeight = 42;
        double headerHeight = 35;
        int visibleRows = 4;

        // Begränsar höjden så fyra rader visas åt gången.
        double tableHeight = (rowHeight * visibleRows) + headerHeight;

        table.setFixedCellSize(rowHeight);
        table.setPrefHeight(tableHeight);
        table.setMinHeight(tableHeight);
        table.setMaxHeight(tableHeight);
    }

    //Uppdaterar startsidans sammanfattning och tabeller med riktig data.
    private void refreshDashboardData() {
        LocalDate today = LocalDate.now();
        // Hämtar dagens bokningar.
        var todaysBookings = bookingService.getBookings()
                .stream()
                .filter(booking -> today.equals(booking.getDate()))
                .collect(Collectors.toList());

        // Hämtar endast arbetsordrar där arbetet faktiskt pågår.
        var ongoingWorkOrders = workOrderService.findAllWorkOrders()
                .stream()
                .filter(order -> "IN_PROGRESS".equals(order.getStatus()))
                .collect(Collectors.toList());

        // Hämtar alla fordon och obetalda fakturor.
        var vehicles = vehicleService.getVehicles();

        var unpaidInvoices = invoiceService.findAll()
                .stream()
                .filter(invoice -> !invoice.isPaid())
                .collect(Collectors.toList());

        // Uppdaterar sammanfattningskorten.
        bookingsValue.setText(String.valueOf(todaysBookings.size()));
        workOrdersValue.setText(String.valueOf(ongoingWorkOrders.size()));
        vehiclesValue.setText(String.valueOf(vehicles.size()));
        invoicesValue.setText(String.valueOf(unpaidInvoices.size()));

        // Skapar en uppslagning av bokningar via deras ID.
        Map<Integer, Booking> bookingById = new HashMap<>();

        for (Booking booking : bookingService.getBookings()) {
            bookingById.put(booking.getId(), booking);

            // Skriver ut bokningens koppling till fordonet för felsökning.
            if (booking.getId() == 3 || booking.getId() == 12) {
                System.out.println(
                        "Bokning ID: " + booking.getId()
                                + " | Vehicle ID: " + booking.getVehicleId()
                );
            }
        }

        // Bygger upp uppslag för att slippa leta efter samma fordon flera gånger.
        Map<Integer, Vehicle> vehicleById = new HashMap<>();
        for (Vehicle vehicle : vehicles) {
            vehicleById.put(vehicle.getId(), vehicle);
        }

        Map<Integer, Customer> customerById = new HashMap<>();
        for (Customer customer : customerService.getAllCustomers()) {
            customerById.put(customer.getId(), customer);
        }

        // Fyller bokningstabellen med dagens bokningar.
        bookingsTable.getItems().setAll(
                todaysBookings.stream()
                        .map(booking -> {
                            // Hämtar fordonet som hör till bokningen.
                            Vehicle vehicle = vehicleById.get(booking.getVehicleId());

                            String registration = vehicle != null
                                    ? vehicle.getRegistrationNumber()
                                    : "-";

                            // Hämtar kunden via fordonets kund-ID.
                            String customerName = "-";
                            if (vehicle != null) {
                                Customer customer = customerById.get(vehicle.getCustomerId());

                                if (customer != null) {
                                    customerName = customer.getName();
                                }
                            }

                            return new BookingRow(
                                    registration,
                                    customerName,
                                    booking.getDescription(),
                                    booking.getStatus()
                            );
                        })
                        .collect(Collectors.toList())
        );

        // Fyller arbetsordertabellen med pågående arbetsordrar.
        workOrdersTable.getItems().setAll(
                ongoingWorkOrders.stream()
                        .map(order -> {
                            // Försöker först hitta fordonet via arbetsorderns fordons-ID.
                            Vehicle vehicle = vehicleById.get(order.getVehicleId());
                            // Skriver ut arbetsorderns kopplingar för felsökning.

                            // Om fordonet saknas försöker vi hitta det via bokningen.
                            if (vehicle == null && order.getBookingId() != null) {
                                Booking linkedBooking = bookingById.get(order.getBookingId());

                                if (linkedBooking != null) {
                                    vehicle = vehicleById.get(linkedBooking.getVehicleId());
                                }
                            }

                            // Visar registreringsnumret eller ett streck om fordonet saknas.
                            String registration = vehicle != null
                                    ? vehicle.getRegistrationNumber()
                                    : "-";

                            // Hämtar tjänstenamnen via arbetsorderns tjänste-ID:n.
                            String serviceNames = order.getServiceItemIds()
                                    .stream()
                                    .map(serviceItemService::findServiceItem)
                                    .filter(service -> service != null)
                                    .map(service -> service.getName())
                                    .collect(Collectors.joining(", "));

                            return new WorkOrderRow(
                                    String.valueOf(order.getId()),
                                    registration,
                                    serviceNames,
                                    order.getStatus()
                            );
                        })
                        .collect(Collectors.toList())
        );
    }

    //Enkel presentationsmodell för bokningstabellen.
    //Ersätts senare med data från projektets riktiga modeller.
    public static class BookingRow {
        private final String registrationNumber;
        private final String customer;
        private final String description;
        private final String status;

        public BookingRow(String registrationNumber, String customer,
                          String description, String status) {
            this.registrationNumber = registrationNumber;
            this.customer = customer;
            this.description = description;
            this.status = status;
        }


        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public String getCustomer() {
            return customer;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return status;
        }
    }

    /**
     * Enkel presentationsmodell för arbetsordertabellen.
     */
    public static class WorkOrderRow {
        private final String id;
        private final String registrationNumber;
        private final String service;
        private final String status;

        public WorkOrderRow(String id, String registrationNumber,
                            String service, String status) {
            this.id = id;
            this.registrationNumber = registrationNumber;
            this.service = service;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public String getService() {
            return service;
        }

        public String getStatus() {
            return status;
        }
    }



    public Parent getView() {
        return root;
    }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("startTitle"));
        subtitle.setText(languageManager.getString("startSubtitle"));
        instruction.setText(languageManager.getString("startInstruction"));
        summaryHeading.setText(languageManager.getString("startSummaryTitle"));
        bookingsSectionTitle.setText(languageManager.getString("startBookingsTableTitle"));
        workOrdersSectionTitle.setText(languageManager.getString("startWorkOrdersTableTitle"));
        bookingsTitle.setText(languageManager.getString("startBookings"));
        workOrdersTitle.setText(languageManager.getString("startWorkOrders"));
        vehiclesTitle.setText(languageManager.getString("startVehicles"));
        invoicesTitle.setText(languageManager.getString("startUnpaidInvoices"));

        // Uppdaterar tabellernas rubriker vid språkbyte.
        updateBookingColumnHeaders();
        updateWorkOrderColumnHeaders();
    }
}
