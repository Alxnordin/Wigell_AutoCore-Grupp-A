package com.wac.autocore.view.order;

import com.wac.autocore.dao.ServiceItemDAO;
import com.wac.autocore.model.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private final Button saveDraftButton = new Button("Spara som utkast");

    private final Label bookingDescription;
    private final Label customerLabel;
    private final Label vehicleLabel;
    private final Label mechanicLabel;


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

    private final Label summaryTitle;
    private final Label totalTimeTitle;
    private final Label totalPriceTitle;
    private final Label totalTimeLabel;
    private final Label totalPriceLabel;

    private final Label customerVehicleTitle;

    private final Label orderInformationTitle;
    private final Label descriptionLabel;
    private final TextField descriptionField;

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

        bookingComboBox.setConverter(new StringConverter<Booking>() {
            @Override
            public String toString(Booking booking) {
                if (booking == null) {
                    return "";
                }

                return "Booking #" + booking.getId()
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
        if ("planned".equals(orderType) && booking != null) {
            StackPane lockedCustomer = UIComponents.createLockedComboBox(customerComboBox);

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
        if ("planned".equals(orderType) && booking != null) {
            StackPane lockedVehicle = UIComponents.createLockedComboBox(vehicleComboBox);

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

        //description
        descriptionLabel = UIComponents.createFormLabel(languageManager.getString("descriptionLabel"));
        descriptionField = UIComponents.createTextField();
        descriptionField.setMaxWidth(Double.MAX_VALUE);

        VBox descriptionBox = new VBox(5);
        if ("planned".equals(orderType) && booking != null) {
            StackPane lockedDescription = UIComponents.createLockedTextField(descriptionField);
            descriptionBox.getChildren().addAll(
                    descriptionLabel,
                    lockedDescription
            );
        } else {
            descriptionBox.getChildren().addAll(
                    descriptionLabel,
                    descriptionField
            );
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
                priceColumn,
                actionColumn);

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

    public Button getSaveDraftButton(){return saveDraftButton;}

    public ComboBox<ServiceItem> getServiceComboBox() {
        return serviceComboBox;
    }

    public ComboBox<Booking> getBookingComboBox() {
        return bookingComboBox;
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

    public TextField getDescriptionField() {
        return descriptionField;
    }

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
        servicesTitle.setText(languageManager.getString("servicesSection"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        timeColumn.setText(languageManager.getString("timeColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        actionColumn.setText(languageManager.getString("actionColumn"));

        summaryTitle.setText(languageManager.getString("summaryTitle"));
        totalTimeTitle.setText(languageManager.getString("estimatedTotalTime"));
        totalPriceTitle.setText(languageManager.getString("estimatedTotalPrice"));

        dateLabel.setText(languageManager.getString("dateLabel"));
        backButton.setText(languageManager.getString("backButton"));
        nextButton.setText(languageManager.getString("nextButton"));
    }
}
