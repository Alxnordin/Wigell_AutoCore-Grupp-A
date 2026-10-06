package com.wac.autocore.view.order;

import com.wac.autocore.dao.ServiceItemDAO;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

public class CreateWorkOrderView {

    private final Parent root;
    private final String orderType;
    private final Label title;
    private final Label subtitle;
    private final Button backButton;
    private final Button nextButton;

    private final Label bookingDescription;
    private final Label customerLabel;
    private final Label vehicleLabel;
    private final Label mechanicLabel;


    private final Label bookingTitle;
    private final Label bookingLabel;
    private final ComboBox<String> bookingComboBox;

    private final Label dateTitle;
    private final Label dateLabel;
    private final DatePicker datePicker;

    private final Label servicesTitle;
    private final TableColumn<ServiceItem, String> serviceColumn;
    private final TableColumn<ServiceItem, String> timeColumn;
    private final TableColumn<ServiceItem, String> priceColumn;
    private final TableColumn<ServiceItem, Void> actionColumn;
    private final TableView<ServiceItem> servicesTable;

    private final Label customerVehicleTitle;
    private final Label mechanicTitle;

    private final ServiceItemDAO serviceItemDAO = new ServiceItemDAO();

    private final ComboBox<ServiceItem> serviceComboBox;
    private final Button addServiceButton;

    private final Label selectServiceLabel;
    private final Label selectedServicesLabel;
    private final Label noServicesLabel;

    //för test utan koppling till backend ännu:
    private final ComboBox<String> customerComboBox;
    private final ComboBox<String> vehicleComboBox;
    private final ComboBox<String> mechanicComboBox;


    private final LanguageManager languageManager = LanguageManager.getInstance();

    public CreateWorkOrderView(String orderType) {
        this.orderType = orderType;
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

        customerVehicleSection.getChildren().addAll(
                customerVehicleTitle,
                customerLabel,
                customerComboBox,
                vehicleLabel,
                vehicleComboBox
        );

        // Mechanic
        VBox mechanicSection = UIComponents.createFormSection();
        mechanicTitle = UIComponents.createSectionTitle(languageManager.getString("mechanicSection"));

        mechanicLabel = UIComponents.createFormLabel(languageManager.getString("mechanicLabel"));
        mechanicComboBox = UIComponents.createComboBox();

        mechanicSection.getChildren().addAll(
                mechanicTitle,
                mechanicLabel,
                mechanicComboBox
        );


        // Date
        VBox dateSection = UIComponents.createFormSection();

        dateTitle = UIComponents.createSectionTitle(languageManager.getString("dateSection"));
        dateLabel = UIComponents.createFormLabel(languageManager.getString("dateLabel"));
        datePicker = UIComponents.createDatePicker();

        dateSection.getChildren().addAll(
                dateTitle,
                dateLabel,
                datePicker
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
            private final Button deleteButton = UIComponents.createDeleteButton();

            {
                setAlignment(javafx.geometry.Pos.CENTER);
                setStyle("-fx-padding: 2 4 2 4;");

                deleteButton.setOnAction(event -> {
                    getTableView().getItems().remove(getIndex());
                });
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

        servicesTable.getColumns().addAll(
                serviceColumn,
                timeColumn,
                priceColumn,
                actionColumn);

        servicesSection.getChildren().addAll(
                servicesTitle,
                selectServiceLabel,
                serviceSelectionBox,
                selectedServicesLabel,
                servicesTable
        );



        // Knappar
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        nextButton = new Button(languageManager.getString("nextButton"));
        nextButton.getStyleClass().add("create-order-button");

        HBox buttonBox = UIComponents.createButtonBox();
        buttonBox.getChildren().addAll(
                backButton,
                nextButton
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
                mechanicSection,
                dateSection,
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


    public Parent getView() {
        return root;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getNextButton() {
        return nextButton;
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
        mechanicTitle.setText(languageManager.getString("mechanicSection"));
        mechanicLabel.setText(languageManager.getString("mechanicLabel"));
        dateTitle.setText(languageManager.getString("dateSection"));
        dateLabel.setText(languageManager.getString("dateLabel"));
        servicesTitle.setText(languageManager.getString("servicesSection"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        timeColumn.setText(languageManager.getString("timeColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        actionColumn.setText(languageManager.getString("actionColumn"));

        dateLabel.setText(languageManager.getString("dateLabel"));
        backButton.setText(languageManager.getString("backButton"));
        nextButton.setText(languageManager.getString("nextButton"));
    }
}
