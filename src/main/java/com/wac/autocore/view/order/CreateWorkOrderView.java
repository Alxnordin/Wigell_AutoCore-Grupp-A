package com.wac.autocore.view.order;

import com.wac.autocore.dao.ServiceItemDAO;
import com.wac.autocore.model.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class CreateWorkOrderView {

    private final Parent root;
    private final String orderType;
    private final Booking booking;
    private final Label title;
    private final Label subtitle;
    private final Button backButton;
    private final Button nextButton;
    private final Button saveDraftButton;

    private final Label bookingDescription;
    private final Label customerLabel;
    private final Label vehicleLabel;
    private final Label mechanicLabel;

    private final Label originalWorkOrderTitle;
    private final Label originalWorkOrderLabel;
    private final ComboBox<WorkOrder> originalWorkOrderComboBox;
    private final Label originalWorkOrderDescription;

    private final VBox originalWorkOrderOverview;

    private final Label originalOrderIdValue;
    private final Label originalOrderStatusValue;
    private final Label originalOrderCustomerValue;
    private final Label originalOrderVehicleValue;
    private final Label originalOrderMechanicValue;

    private final Label bookingTitle;
    private final Label bookingLabel;
    private final ComboBox<Booking> bookingComboBox;

    private final Label dateLabel;
    private final DatePicker datePicker;

    private final Label servicesTitle;

    private final TableColumn<ServiceItem, String> serviceColumn;
    private final TableColumn<ServiceItem, String> timeColumn;
    private final TableColumn<ServiceItem, String> priceColumn;
    private final TableColumn<ServiceItem, Void> actionColumn;
    private final TableView<ServiceItem> servicesTable;
    private final TableColumn<ServiceItem, String> costResponsibilityColumn;

    private final Label summaryTitle;
    private final Label totalTimeTitle;
    private final Label totalPriceTitle;
    private final Label totalTimeLabel;
    private final Label totalPriceLabel;

    private final Label customerVehicleTitle;

    private final Label orderInformationTitle;
    private final Label descriptionLabel;
    private final TextArea descriptionField;

    private final Label warrantyDescriptionLabel;
    private final TextArea warrantyDescriptionField;

    private final ServiceItemDAO serviceItemDAO = new ServiceItemDAO();
    private final List<Integer> bookingServiceItemIds = new ArrayList<>();

    private final ComboBox<ServiceItem> serviceComboBox;
    private final Button addServiceButton;

    private final Label selectServiceLabel;
    private final Label selectedServicesLabel;
    private final Label noServicesLabel;

    //för test utan koppling till backend ännu:
    private final ComboBox<Customer> customerComboBox;
    private final ComboBox<Vehicle> vehicleComboBox;
    private final ComboBox<Mechanic> mechanicComboBox;


    private final LanguageManager languageManager = LanguageManager.getInstance();

    public CreateWorkOrderView(String orderType, Booking booking) {
        this.orderType = orderType;
        this.booking = booking;
        VBox box = new VBox(18);
        box.setPadding(new Insets(25));
        box.setFillWidth(true);

        // Rubrik
        title = new Label(getTitleForOrderType(orderType));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-plus");

        // Booking
        VBox bookingSection = UIComponents.createFormSection();
        bookingTitle = UIComponents.createSectionTitle(languageManager.getString("bookingSection"));
        bookingDescription = UIComponents.createSubtitle(languageManager.getString("bookingDescription"));
        bookingLabel = UIComponents.createFormLabel(languageManager.getString("bookingLabel"));
        bookingComboBox = UIComponents.createComboBox();

        originalWorkOrderTitle = UIComponents.createSectionTitle(languageManager.getString("originalWorkOrderSection"));
        originalWorkOrderDescription = new Label(languageManager.getString("originalWorkOrderDescription"));
        originalWorkOrderLabel = UIComponents.createFormLabel(languageManager.getString("originalWorkOrderLabel"));
        originalWorkOrderComboBox = UIComponents.createComboBox();

        // Skapar värdena som senare fylls med information från den valda arbetsordern
        originalOrderIdValue = new Label("-");
        originalOrderStatusValue = new Label("-");
        originalOrderCustomerValue = new Label("-");
        originalOrderVehicleValue = new Label("-");
        originalOrderMechanicValue = new Label("-");

        originalWorkOrderOverview = createOriginalWorkOrderOverview();

        originalWorkOrderOverview.setVisible(false);
        originalWorkOrderOverview.setManaged(false);

        originalWorkOrderComboBox.setConverter(new StringConverter<WorkOrder>() {
            @Override
            public String toString(WorkOrder workOrder) {
                if (workOrder == null) {
                    return "";}

                return "WO-" + workOrder.getId();}

            @Override
            public WorkOrder fromString(String string) {
                return null;
            }
        });

        VBox originalWorkOrderSection = UIComponents.createFormSection();

        originalWorkOrderSection.getChildren().addAll(
                originalWorkOrderTitle,
                originalWorkOrderDescription,
                originalWorkOrderLabel,
                originalWorkOrderComboBox,
                originalWorkOrderOverview
        );

        bookingComboBox.setConverter(new StringConverter<Booking>() {
            @Override
            public String toString(Booking booking) {
                if (booking == null) {
                    return "";
                }

                return languageManager.getString("bookingNumber")
                        + " #" + booking.getId()
                        + " - " + booking.getDate();
            }
            @Override
            public Booking fromString(String string) {
                return null;
            }
        });

        bookingSection.getChildren().addAll(
                bookingTitle,
                bookingDescription,
                bookingLabel,
                bookingComboBox
        );


        // Underrubrik
        subtitle = UIComponents.createSubtitle(getSubtitleForOrderType(orderType));

        // Customer & Vehicle
        VBox customerVehicleSection = UIComponents.createFormSection();
        customerVehicleTitle = UIComponents.createSectionTitle(languageManager.getString("customerVehicleSection"));

        customerLabel = UIComponents.createFormLabel(languageManager.getString("customerLabel"));
        customerComboBox = UIComponents.createComboBox();

        vehicleLabel = UIComponents.createFormLabel(languageManager.getString("vehicleLabel"));
        vehicleComboBox = UIComponents.createComboBox();

        // Customer
        VBox customerBox = new VBox(5);

        if ("planned".equals(orderType) || "warranty".equals(orderType)) {
            StackPane lockedCustomer =
                    UIComponents.createLockedComboBox(customerComboBox);

            customerBox.getChildren().addAll(
                    customerLabel,
                    lockedCustomer
            );
        } else {
            customerBox.getChildren().addAll(
                    customerLabel,
                    customerComboBox
            );
        }


        // Vehicle
        VBox vehicleBox = new VBox(5);

        if ("planned".equals(orderType) || "warranty".equals(orderType)) {
            StackPane lockedVehicle =
                    UIComponents.createLockedComboBox(vehicleComboBox);

            vehicleBox.getChildren().addAll(
                    vehicleLabel,
                    lockedVehicle
            );
        } else {
            vehicleBox.getChildren().addAll(
                    vehicleLabel,
                    vehicleComboBox
            );
        }


        // Lägg Customer och Vehicle bredvid varandra
        HBox customerVehicleRow = new HBox(20);
        customerVehicleRow.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(customerBox, Priority.ALWAYS);
        HBox.setHgrow(vehicleBox, Priority.ALWAYS);

        customerComboBox.setMaxWidth(Double.MAX_VALUE);
        vehicleComboBox.setMaxWidth(Double.MAX_VALUE);

        customerVehicleRow.getChildren().addAll(
                customerBox,
                vehicleBox
        );

        // Lägg ihop sektionen
        customerVehicleSection.getChildren().addAll(
                customerVehicleTitle,
                customerVehicleRow
        );


        // Order Information
        VBox orderInformationSection = UIComponents.createFormSection();
        orderInformationTitle = UIComponents.createSectionTitle(languageManager.getString("orderInformationSection"));

        //mechanic
        mechanicLabel = UIComponents.createFormLabel(languageManager.getString("mechanicLabel") + " *");
        mechanicComboBox = UIComponents.createComboBox();

        mechanicComboBox.setConverter(new StringConverter<Mechanic>() {

            @Override
            public String toString(Mechanic mechanic) {
                if (mechanic == null) {
                    return "";
                }

                return mechanic.getName();
            }

            @Override
            public Mechanic fromString(String string) {
                return null;
            }
        });

        Label requiredLabel = new Label("* " + languageManager.getString("requiredField"));
        requiredLabel.getStyleClass().add("required-label");

        VBox mechanicBox = new VBox(5);
        mechanicBox.getChildren().addAll(
                mechanicLabel,
                mechanicComboBox
        );

        //date
        dateLabel = UIComponents.createFormLabel(languageManager.getString("dateLabel"));
        datePicker = UIComponents.createDatePicker();

        VBox dateBox = new VBox(5);
        if ("planned".equals(orderType)) {
            StackPane lockedDate = UIComponents.createLockedDatePicker(datePicker);

            dateBox.getChildren().addAll(
                    dateLabel,
                    lockedDate
            );
        } else {
            dateBox.getChildren().addAll(
                    dateLabel,
                    datePicker
            );
        }


        //lägger date och mechanik brevid varandra
        HBox mechanicDateRow = new HBox(20);
        mechanicDateRow.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(mechanicBox, Priority.ALWAYS);
        HBox.setHgrow(dateBox, Priority.ALWAYS);

        mechanicComboBox.setMaxWidth(Double.MAX_VALUE);
        datePicker.setMaxWidth(Double.MAX_VALUE);

        mechanicDateRow.getChildren().addAll(
                mechanicBox,
                dateBox
        );

        // Original work description
        descriptionLabel = UIComponents.createFormLabel(
                languageManager.getString("descriptionLabel")
        );

        descriptionField = new TextArea();
        descriptionField.setMaxWidth(Double.MAX_VALUE);
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        descriptionField.getStyleClass().add("standard-text-area");

        // Warranty description
        warrantyDescriptionLabel = UIComponents.createFormLabel(
                languageManager.getString("warrantyDescriptionLabel")
        );

        warrantyDescriptionField = new TextArea();
        warrantyDescriptionField.setMaxWidth(Double.MAX_VALUE);
        warrantyDescriptionField.setPrefRowCount(3);
        warrantyDescriptionField.setWrapText(true);
        warrantyDescriptionField.getStyleClass().add("standard-text-area");

        // Samlar båda beskrivningarna i Order Information
        VBox descriptionBox = new VBox(10);

        if ("planned".equals(orderType)) {

            StackPane lockedDescription =
                    UIComponents.createLockedTextArea(descriptionField);

            descriptionBox.getChildren().addAll(
                    descriptionLabel,
                    lockedDescription
            );

        } else {

            descriptionBox.getChildren().addAll(
                    descriptionLabel,
                    descriptionField
            );

            // Warranty description visas bara för Warranty-order
            if ("warranty".equals(orderType)) {
                descriptionBox.getChildren().addAll(
                        warrantyDescriptionLabel,
                        warrantyDescriptionField
                );
            }
        }


        //lägger ihop hela order information
        orderInformationSection.getChildren().addAll(
                orderInformationTitle,
                mechanicDateRow,
                descriptionBox
        );


        // Services
        VBox servicesSection = UIComponents.createFormSection();

        servicesTitle = UIComponents.createSectionTitle(languageManager.getString("servicesSection"));
        servicesTable = UIComponents.createTable();
        servicesTable.setMaxWidth(Double.MAX_VALUE);

        //dropdown och knapp för att lägga till service i tabellen
        selectServiceLabel = UIComponents.createFormLabel(languageManager.getString("selectService"));
        selectedServicesLabel = UIComponents.createFormLabel(languageManager.getString("selectedServices"));
        noServicesLabel = new Label(languageManager.getString("noServices"));
        noServicesLabel.getStyleClass().add("placeholder-text");

        addServiceButton = new Button(languageManager.getString("addService"));
        addServiceButton.getStyleClass().add("add-service-button");

        List<ServiceItem> serviceItems = serviceItemDAO.findAll();

        serviceComboBox = UIComponents.createComboBox();
        serviceComboBox.getItems().addAll(serviceItems);

        serviceComboBox.setConverter(new StringConverter<ServiceItem>() {

            @Override
            public String toString(ServiceItem serviceItem) {
                return serviceItem == null ? "" : serviceItem.getName();
            }

            @Override
            public ServiceItem fromString(String string) {
                return null;
            }
        });

        HBox serviceSelectionBox = new HBox(10);
        serviceSelectionBox.setAlignment(Pos.CENTER_LEFT);

        serviceSelectionBox.getChildren().addAll(
                serviceComboBox,
                addServiceButton
        );

        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
        timeColumn = new TableColumn<>(languageManager.getString("timeColumn"));
        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));
        costResponsibilityColumn = new TableColumn<>(languageManager.getString("costResponsibility"));
        actionColumn = new TableColumn<>(languageManager.getString("actionColumn"));


        servicesTable.setPlaceholder(noServicesLabel);

        //kopllar tabellkolumnerna till ServiceItem
        serviceColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getName()
                )
        );

        timeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getEstimatedMinutes() + " min"
                )
        );

        priceColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f kr", cellData.getValue().getPrice())
                )
        );

        costResponsibilityColumn.setCellFactory(column ->
                new TableCell<ServiceItem, String>() {

                    private final ComboBox<String> responsibilityComboBox =
                            UIComponents.createComboBox();

                    {
                        // Alternativ för vem som står för kostnaden
                        responsibilityComboBox.getItems().addAll(
                                languageManager.getString("costWarranty"),
                                languageManager.getString("costCompany"),
                                languageManager.getString("costCustomer")
                        );

                        responsibilityComboBox.setMaxWidth(Double.MAX_VALUE);
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                            return;
                        }

                        // Visa dropdownen i tabellraden
                        setGraphic(responsibilityComboBox);
                    }
                }
        );

        actionColumn.setCellFactory(column -> new TableCell<ServiceItem, Void>() {

            private final FontIcon lockIcon =
                    new FontIcon("fa-lock");

            private final Button deleteButton =
                    UIComponents.createDeleteButton();

            {
                lockIcon.setIconSize(14);
                lockIcon.getStyleClass().add("locked-field-icon");

                setAlignment(Pos.CENTER);

                deleteButton.setOnAction(event -> {

                    ServiceItem serviceItem =
                            getTableView().getItems().get(getIndex());

                    getTableView().getItems().remove(serviceItem);

                    updateSummary();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                ServiceItem serviceItem =
                        getTableView()
                                .getItems()
                                .get(getIndex());

                if (bookingServiceItemIds.contains(serviceItem.getId())) {

                    // Kommer från bokningen
                    setGraphic(lockIcon);

                } else {

                    // Tillagd manuellt
                    setGraphic(deleteButton);
                }
            }
        });

        addServiceButton.setOnAction(event -> {
            ServiceItem selectedService = serviceComboBox.getValue();

            if (selectedService != null &&
                    !servicesTable.getItems().contains(selectedService)) {

                servicesTable.getItems().add(selectedService);
                serviceComboBox.getSelectionModel().clearSelection();

                updateSummary();
            }
        });

        servicesTable.getColumns().addAll(
                serviceColumn,
                timeColumn,
                priceColumn
        );

        // Cost responsibility finns endast på Warranty
        if ("warranty".equals(orderType)) {
            servicesTable.getColumns().add(
                    costResponsibilityColumn
            );
        }

        servicesTable.getColumns().add(
                actionColumn
        );

        // Summary
        summaryTitle = UIComponents.createSectionTitle(languageManager.getString("summaryTitle"));
        totalTimeTitle = UIComponents.createFormLabel(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle = UIComponents.createFormLabel(languageManager.getString("estimatedTotalPrice"));

        totalTimeLabel = new Label("--");
        totalPriceLabel = new Label("--");

        VBox timeCard = UIComponents.createSummaryCard(
                "fa-clock-o",
                totalTimeTitle,
                totalTimeLabel
        );

        VBox priceCard = UIComponents.createSummaryCard(
                "fa-money",
                totalPriceTitle,
                totalPriceLabel
        );

        HBox summaryCards = UIComponents.createSummaryCards(
                timeCard,
                priceCard
        );

        servicesSection.getChildren().addAll(
                servicesTitle,
                selectServiceLabel,
                serviceSelectionBox,
                selectedServicesLabel,
                servicesTable,
                summaryTitle,
                summaryCards
        );


        // Knappar
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        nextButton = new Button(languageManager.getString("nextButton"));
        nextButton.getStyleClass().add("create-order-button");

        saveDraftButton = new Button(languageManager.getString("saveDraft"));

        HBox buttonBox = UIComponents.createButtonBox();
        buttonBox.getChildren().addAll(
                backButton,
                nextButton,
                saveDraftButton
        );

        // Lägg allt på sidan
        box.getChildren().addAll(
                titleBox,
                subtitle
        );

        if (orderType.equals("planned")) {
            box.getChildren().add(bookingSection);
        }

        if (orderType.equals("warranty")) {
            box.getChildren().add(originalWorkOrderSection);
        }

        box.getChildren().addAll(
                customerVehicleSection,
                orderInformationSection,
                servicesSection,
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(box);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.getStyleClass().add("page-scroll-pane");

        this.root = scrollPane;


        // Språkbyte
        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    changeTextAllComponents();
                }
        );
    }

    private String getTitleForOrderType(String orderType) {
        switch (orderType) {
            case "planned":
                return languageManager.getString("createPlannedOrderTitle");

            case "dropIn":
                return languageManager.getString("createDropInOrderTitle");

            case "warranty":
                return languageManager.getString("createWarrantyOrderTitle");

            default:
                return languageManager.getString("createWorkOrderTitle");
        }
    }

    private String getSubtitleForOrderType(String orderType) {
        switch (orderType) {
            case "planned":
                return languageManager.getString("createPlannedOrderSubtitle");

            case "dropIn":
                return languageManager.getString("createDropInOrderSubtitle");

            case "warranty":
                return languageManager.getString("createWarrantyOrderSubtitle");

            default:
                return languageManager.getString("createWorkOrderSubtitle");
        }
    }

    public Button getSaveDraftButton() {
        return saveDraftButton;
    }

    // Skapar sektionen som visar en översikt av den valda ursprungliga arbetsordern
    private VBox createOriginalWorkOrderOverview() {
        Label overviewTitle = UIComponents.createSectionTitle(languageManager.getString("selectedOriginalWorkOrder"));
        overviewTitle.getStyleClass().add("original-work-order-title");
        Label orderIdLabel = UIComponents.createFormLabel(languageManager.getString("originalOrderId"));
        Label statusLabel = UIComponents.createFormLabel(languageManager.getString("originalOrderStatus"));
        Label customerLabel = UIComponents.createFormLabel(languageManager.getString("originalOrderCustomer"));
        Label vehicleLabel = UIComponents.createFormLabel(languageManager.getString("originalOrderVehicle"));
        Label mechanicLabel = UIComponents.createFormLabel(languageManager.getString("originalOrderMechanic"));

        GridPane grid = new GridPane();

        grid.setHgap(30);
        grid.setVgap(10);

        grid.add(orderIdLabel, 0, 0);
        grid.add(originalOrderIdValue, 1, 0);

        grid.add(statusLabel, 0, 1);
        grid.add(originalOrderStatusValue, 1, 1);

        grid.add(customerLabel, 0, 2);
        grid.add(originalOrderCustomerValue, 1, 2);

        grid.add(vehicleLabel, 0, 3);
        grid.add(originalOrderVehicleValue, 1, 3);

        grid.add(mechanicLabel, 0, 4);
        grid.add(originalOrderMechanicValue, 1, 4);

        VBox overview = UIComponents.createFormSection();

        overview.getStyleClass().add("original-work-order-overview");

        overview.getChildren().addAll(
                overviewTitle,
                grid
        );

        return overview;
    }


    // Visar information om den valda ursprungliga arbetsordern
    public void showOriginalWorkOrder(
            WorkOrder workOrder,
            Customer customer,
            Vehicle vehicle,
            Mechanic mechanic) {

        if (workOrder == null) {
            originalWorkOrderOverview.setVisible(false);
            originalWorkOrderOverview.setManaged(false);
            return;
        }

        originalOrderIdValue.setText("WO-" + workOrder.getId());
        originalOrderStatusValue.setText(workOrder.getStatus());

        originalOrderCustomerValue.setText(customer != null ? customer.getName() : "-");

        originalOrderVehicleValue.setText(
                vehicle != null
                        ? vehicle.getBrand() + " "
                        + vehicle.getModel()
                        + " · "
                        + vehicle.getRegistrationNumber()
                        : "-"
        );

        originalOrderMechanicValue.setText(
                mechanic != null ? mechanic.getName() : "-"
        );

        originalWorkOrderOverview.setVisible(true);
        originalWorkOrderOverview.setManaged(true);
    }


    public void setBookingServiceItemIds(List<Integer> serviceItemIds) {
        bookingServiceItemIds.clear();

        if (serviceItemIds != null) {
            bookingServiceItemIds.addAll(serviceItemIds);
        }
        servicesTable.refresh();
    }

    public void showSummary(String totalTime, String totalPrice) {
        totalTimeLabel.setText(totalTime);
        totalPriceLabel.setText(totalPrice);
    }

    private void updateSummary() {

        int totalMinutes = servicesTable.getItems().stream()
                .mapToInt(ServiceItem::getEstimatedMinutes)
                .sum();

        double totalPrice = servicesTable.getItems().stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();

        if (servicesTable.getItems().isEmpty()) {
            totalTimeLabel.setText("--");
            totalPriceLabel.setText("--");
            return;
        }

        totalTimeLabel.setText(totalMinutes + " min");
        totalPriceLabel.setText(String.format("%,.0f kr", totalPrice));
    }


    public Parent getView() {
        return root;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public ComboBox<ServiceItem> getServiceComboBox() {
        return serviceComboBox;
    }

    public ComboBox<Booking> getBookingComboBox() {
        return bookingComboBox;
    }

    public ComboBox<WorkOrder> getOriginalWorkOrderComboBox() {
        return originalWorkOrderComboBox;
    }

    public ComboBox<Vehicle> getVehicleComboBox() {
        return vehicleComboBox;
    }

    public ComboBox<Customer> getCustomerComboBox() {
        return customerComboBox;
    }

    public ComboBox<Mechanic> getMechanicComboBox() {
        return mechanicComboBox;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public TextArea getDescriptionField() {return descriptionField;}
    //Alexander
    //beskrivningen av vad reklamationen gäller (visas bara för warranty order)
    public TextArea getWarrantyDescriptionField() {return warrantyDescriptionField;}

    public Button getAddServiceButton() {
        return addServiceButton;
    }

    public TableView<ServiceItem> getServicesTable() {
        return servicesTable;
    }

    private void changeTextAllComponents() {
        title.setText(getTitleForOrderType(orderType));
        subtitle.setText(getSubtitleForOrderType(orderType));
        bookingTitle.setText(languageManager.getString("bookingSection"));
        bookingLabel.setText(languageManager.getString("bookingLabel"));
        bookingDescription.setText(languageManager.getString("bookingDescription"));
        customerVehicleTitle.setText(languageManager.getString("customerVehicleSection"));
        customerLabel.setText(languageManager.getString("customerLabel"));
        vehicleLabel.setText(languageManager.getString("vehicleLabel"));
        orderInformationTitle.setText(languageManager.getString("orderInformationSection"));
        mechanicLabel.setText(languageManager.getString("mechanicLabel"));
        dateLabel.setText(languageManager.getString("dateLabel"));
        descriptionLabel.setText(languageManager.getString("descriptionLabel"));
        warrantyDescriptionLabel.setText(languageManager.getString("warrantyDescriptionLabel"));
        servicesTitle.setText(languageManager.getString("servicesSection"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        timeColumn.setText(languageManager.getString("timeColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        costResponsibilityColumn.setText(languageManager.getString("costResponsibility"));
        actionColumn.setText(languageManager.getString("actionColumn"));
        selectServiceLabel.setText(languageManager.getString("selectService"));
        selectedServicesLabel.setText(languageManager.getString("selectedServices"));
        noServicesLabel.setText(languageManager.getString("noServices"));
        addServiceButton.setText(languageManager.getString("addService"));

        originalWorkOrderTitle.setText(languageManager.getString("originalWorkOrderSection"));
        originalWorkOrderDescription.setText(languageManager.getString("originalWorkOrderDescription"));
        originalWorkOrderLabel.setText(languageManager.getString("originalWorkOrderLabel"));

        summaryTitle.setText(languageManager.getString("summaryTitle"));
        totalTimeTitle.setText(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle.setText(languageManager.getString("estimatedTotalPrice"));

        dateLabel.setText(languageManager.getString("dateLabel"));
        backButton.setText(languageManager.getString("backButton"));
        nextButton.setText(languageManager.getString("nextButton"));
        saveDraftButton.setText(languageManager.getString("saveDraft"));
    }
}