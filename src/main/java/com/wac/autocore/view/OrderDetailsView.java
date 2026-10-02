package com.wac.autocore.view;

import com.wac.autocore.model.*;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Consumer;

public class OrderDetailsView {

    private final Parent root;
    private final WorkOrder workOrder;

    private final Label title;
    private final Label statusLabel;
    private final Label subtitle;

    private final Label customerLabel;
    private final Label vehicleLabel;
    private final Label dateLabel;
    private final Label mechanicLabel;
    private final ComboBox<Mechanic> mechanicComboBox;
    private final Label descriptionLabel;

    private final Label servicesTitle;
    private final Label statusValue;
    private final Button statusActionButton;

    private final TableColumn<ServiceItem, String> serviceColumn;
    private final TableColumn<ServiceItem, Integer> timeColumn;
    private final TableColumn<ServiceItem, String> priceColumn;

    private Consumer<String> onStatusChange;

    private final Button backButton;
    private Runnable onBack;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public OrderDetailsView(
            WorkOrder workOrder,
            Booking booking,
            Vehicle vehicle,
            Customer customer,
            List<Mechanic> mechanics,
            List<ServiceItem> orderServices) {

        this.workOrder = workOrder;

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        // Rubrik
        title = new Label(
                languageManager.getString("workOrderTitle")
                        + " #" + workOrder.getId());

        HBox titleBox = UIComponents.createPageTitle(title, "fa-wrench");

        // Underrubrik
        subtitle = new Label(languageManager.getString("workOrderSubtitle"));

        // Information
        VBox bookingInfoBox = UIComponents.createSectionBox();

        // Customer
        customerLabel = new Label(languageManager.getString("customerLabel"));
        customerLabel.getStyleClass().add("info-label");

        Label customerValue = new Label(customer.getName());
        customerValue.getStyleClass().add("info-value");

        VBox customerBox = new VBox(6);
        customerBox.getChildren().addAll(
                customerLabel,
                customerValue
        );


        // Vehicle
        vehicleLabel = new Label(languageManager.getString("vehicleLabel"));
        vehicleLabel.getStyleClass().add("info-label");

        Label vehicleValue = new Label(vehicle.getRegistrationNumber());
        vehicleValue.getStyleClass().add("info-value");


        VBox vehicleBox = new VBox(6);
        vehicleBox.getChildren().addAll(
                vehicleLabel,
                vehicleValue
        );


        // Booking date
        dateLabel = new Label(languageManager.getString("dateLabel"));
        dateLabel.getStyleClass().add("info-label");

        Label dateValue = new Label(booking.getDate().toString());
        dateValue.getStyleClass().add("info-value");

        VBox dateBox = new VBox(6);
        dateBox.getChildren().addAll(
                dateLabel,
                dateValue
        );


        // Övre raden
        HBox informationRow = new HBox(45);

        informationRow.getChildren().addAll(
                customerBox,
                vehicleBox,
                dateBox
        );

        // Mechanic
        mechanicLabel = new Label(languageManager.getString("mechanicLabel"));
        mechanicLabel.getStyleClass().add("info-label");

        mechanicComboBox = UIComponents.createComboBox();
        mechanicComboBox.getItems().addAll(mechanics);
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

        VBox mechanicBox = new VBox(6);
        mechanicBox.getChildren().addAll(
                mechanicLabel,
                mechanicComboBox
        );

        // Status
        statusLabel = new Label(languageManager.getString("statusLabel"));
        statusLabel.getStyleClass().add("info-label");
        statusValue = new Label(languageManager.getString("statusCreated"));

        statusValue.getStyleClass().add("status-value");
        statusValue.getStyleClass().add("status-created");

        statusActionButton = new Button(
                languageManager.getString("startWork")
        );
        statusActionButton.getStyleClass().add("start-button");


        updateStatus();

        mechanicComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> updateStartButtonState()
        );

        updateStartButtonState();


        statusActionButton.setOnAction(event -> {
            if ("CREATED".equals(workOrder.getStatus())) {

                // Kontrollera att en mekaniker är vald
                if (mechanicComboBox.getValue() == null) {
                    return;
                }

                if (onStatusChange != null) {
                    onStatusChange.accept("IN_PROGRESS");
                }

            } else if ("IN_PROGRESS".equals(workOrder.getStatus())) {

                if (onStatusChange != null) {
                    onStatusChange.accept("COMPLETED");
                }
            }
        });


        VBox statusBox = new VBox(6);
        statusBox.getChildren().addAll(
                statusLabel,
                statusValue,
                statusActionButton
        );

        VBox mechanicAndStatusBox = new VBox(12);
        mechanicAndStatusBox.getChildren().addAll(
                mechanicBox,
                statusBox
        );

        HBox informationLayout = new HBox(80);
        informationLayout.setAlignment(Pos.TOP_LEFT);
        informationLayout.getChildren().addAll(
                informationRow,
                mechanicAndStatusBox
        );


        // Description
        descriptionLabel = new Label(languageManager.getString("descriptionLabel"));
        descriptionLabel.getStyleClass().add("info-label");

        Label descriptionValue = new Label(booking.getDescription());
        descriptionValue.getStyleClass().add("info-value");

        VBox descriptionBox = new VBox(6);
        descriptionBox.setPadding(new Insets(15, 0, 0, 0));

        descriptionBox.getChildren().addAll(
                descriptionLabel,
                descriptionValue
        );


        // Lägg informationen i sektionen
        bookingInfoBox.getChildren().addAll(
                informationLayout,
                descriptionBox
        );


        VBox servicesBox = UIComponents.createSectionBox();
        servicesTitle = UIComponents.createSectionTitle(languageManager.getString("servicesTitle"));

        TableView<ServiceItem> servicesTable = UIComponents.createTable();

        serviceColumn = new TableColumn<>(languageManager.getString("serviceInTable"));
        timeColumn = new TableColumn<>(languageManager.getString("estimatedTimeInTable"));
        priceColumn = new TableColumn<>(languageManager.getString("priceInTable"));

        serviceColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getName()
                )
        );

        timeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getEstimatedMinutes()
                )
        );

        //pris som hör till arbetsordern, inte dagens pris på tjänsten
        priceColumn.setCellValueFactory(
                cellData -> {
                    ServiceItem serviceItem = cellData.getValue();

                    Double price =
                            workOrder.getServiceItemPrices()
                                    .get(serviceItem.getId());

                    if (price == null) {
                        price = serviceItem.getPrice();
                    }

                    return new javafx.beans.property.SimpleStringProperty(
                            String.format("%.2f kr", price)
                    );
                }
        );

        servicesTable.getColumns().addAll(
                serviceColumn,
                timeColumn,
                priceColumn
        );

        servicesTable.getItems().addAll(orderServices);
        servicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        servicesBox.getChildren().addAll(servicesTitle, servicesTable);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));
        backButton.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        // Lägg allt på sidan
        box.getChildren().addAll(
                titleBox,
                subtitle,
                bookingInfoBox,
                servicesBox,
                backButton
        );

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    changeTextAllComponents();
                }
        );

        this.root = box;
    }

    public void updateStartButtonState() {
        boolean mechanicSelected = mechanicComboBox.getValue() != null;

        statusActionButton.setDisable(
                "CREATED".equals(workOrder.getStatus()) && !mechanicSelected
        );
    }

    public Parent getView() {
        return root;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    public void setOnStatusChange(Consumer<String> onStatusChange) {
        this.onStatusChange = onStatusChange;
    }

    public void updateStatus() {



        if ("CREATED".equals(workOrder.getStatus())) {

            statusValue.setText(
                    languageManager.getString("statusCreated")
            );

            statusValue.getStyleClass().remove("status-in-progress");
            statusValue.getStyleClass().remove("status-completed");

            if (!statusValue.getStyleClass().contains("status-created")) {
                statusValue.getStyleClass().add("status-created");
            }

            statusActionButton.setText(
                    languageManager.getString("startWork")
            );

            statusActionButton.setVisible(true);
            statusActionButton.setManaged(true);

        } else if ("IN_PROGRESS".equals(workOrder.getStatus())) {

            statusValue.setText(
                    languageManager.getString("statusInProgress")
            );

            statusValue.getStyleClass().remove("status-created");
            statusValue.getStyleClass().remove("status-completed");

            if (!statusValue.getStyleClass().contains("status-in-progress")) {
                statusValue.getStyleClass().add("status-in-progress");
            }

            statusActionButton.setText(
                    languageManager.getString("completeWork")
            );

            statusActionButton.setVisible(true);
            statusActionButton.setManaged(true);

        } else if ("COMPLETED".equals(workOrder.getStatus())) {

            statusValue.setText(
                    languageManager.getString("statusCompleted")
            );

            statusValue.getStyleClass().remove("status-created");
            statusValue.getStyleClass().remove("status-in-progress");

            if (!statusValue.getStyleClass().contains("status-completed")) {
                statusValue.getStyleClass().add("status-completed");
            }

            statusActionButton.setVisible(false);
            statusActionButton.setManaged(false);
        }
    }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("workOrderTitle") + " #" + workOrder.getId());
        subtitle.setText(languageManager.getString("workOrderSubtitle"));

        customerLabel.setText(languageManager.getString("customerLabel"));
        vehicleLabel.setText(languageManager.getString("vehicleLabel"));
        dateLabel.setText(languageManager.getString("dateLabel"));
        mechanicLabel.setText(languageManager.getString("mechanicLabel"));
        descriptionLabel.setText(languageManager.getString("descriptionLabel"));
        statusLabel.setText(languageManager.getString("statusLabel"));

        servicesTitle.setText(languageManager.getString("servicesTitle"));
        serviceColumn.setText(languageManager.getString("serviceInTable"));
        timeColumn.setText(languageManager.getString("estimatedTimeInTable"));
        priceColumn.setText(languageManager.getString("priceInTable"));

        backButton.setText(languageManager.getString("backButton"));
    }
}