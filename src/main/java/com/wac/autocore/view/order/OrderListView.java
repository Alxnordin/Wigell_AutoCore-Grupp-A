package com.wac.autocore.view.order;

import com.wac.autocore.dao.ServiceItemDAO;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.service.MechanicService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

//UI-vy som visar arbetsorder listan samt kontroller för att starta och slutföra en arbetsorder
public class OrderListView {

    private final Parent root;
    private final TableView<WorkOrder> orderTable;

    private final TableColumn<WorkOrder, String> orderIdColumn;
    private final TableColumn<WorkOrder, String> bookingIdColumn;
    private final TableColumn<WorkOrder, String> mechanicIdColumn;
    private final TableColumn<WorkOrder, String> serviceColumn;
    private final TableColumn<WorkOrder, String> statusColumn;
    private final TableColumn<WorkOrder, Void> actionColumn;

    private Consumer<WorkOrder> onViewOrder;

    private final Label title;
    private final Label subtitle;

    private final Button backButton;
    private final Button createOrderButton;
    private Consumer<String> onCreateOrderType;
    private final ContextMenu createOrderMenu;

    private final Label plannedOrderTitle;
    private final Label plannedOrderDescription;

    private final Label dropInOrderTitle;
    private final Label dropInOrderDescription;

    private final Label warrantyOrderTitle;
    private final Label warrantyOrderDescription;

    private final ServiceItemDAO serviceItemDAO;
    private final MechanicService mechanicService;

    LanguageManager languageManager = LanguageManager.getInstance();


    public OrderListView() {
        serviceItemDAO = new ServiceItemDAO();
        mechanicService = new MechanicService();

        //Huvudlayout
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        //title
        title = new Label(languageManager.getString("ordersTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-list-ul");

        //Knapp för att skapa ny arbetsorder
        createOrderButton = new Button(
                languageManager.getString("createOrderButton")
        );
        createOrderButton.getStyleClass().add("create-order-button");

        //Skapa dropdownmeny för dom olika typerna av order man kan skapa
        createOrderMenu = new ContextMenu();
        plannedOrderTitle = new Label(languageManager.getString("plannedOrder"));
        plannedOrderDescription = new Label(languageManager.getString("plannedOrderDescription"));
        dropInOrderTitle = new Label(languageManager.getString("dropInOrder"));
        dropInOrderDescription = new Label(languageManager.getString("dropInOrderDescription"));
        warrantyOrderTitle = new Label(languageManager.getString("warrantyOrder"));
        warrantyOrderDescription = new Label(languageManager.getString("warrantyOrderDescription"));


        MenuItem plannedOrderItem = UIComponents.createOrderMenuItem(
                plannedOrderTitle,
                plannedOrderDescription,
                "fa-calendar"
        );

        MenuItem dropInOrderItem = UIComponents.createOrderMenuItem(
                dropInOrderTitle,
                dropInOrderDescription,
                "fa-user"
        );

        MenuItem warrantyOrderItem = UIComponents.createOrderMenuItem(
                warrantyOrderTitle,
                warrantyOrderDescription,
                "fa-refresh"
        );

        //vad händer när man väljer en odertyp:
        plannedOrderItem.setOnAction(event -> {
            if (onCreateOrderType != null) {
                onCreateOrderType.accept("planned");
            }
        });

        dropInOrderItem.setOnAction(event -> {
            if (onCreateOrderType != null) {
                onCreateOrderType.accept("dropIn");
            }
        });

        warrantyOrderItem.setOnAction(event -> {
            if (onCreateOrderType != null) {
                onCreateOrderType.accept("warranty");
            }
        });

        createOrderMenu.getItems().addAll(
                plannedOrderItem,
                dropInOrderItem,
                warrantyOrderItem
        );

        createOrderButton.setOnAction(event -> {
            createOrderMenu.show(
                    createOrderButton,
                    javafx.geometry.Side.BOTTOM,
                    0,
                    0
            );
        });



        HBox orderHeader = new HBox();
        orderHeader.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        orderHeader.getChildren().addAll(
                titleBox,
                spacer,
                createOrderButton
        );

        //Underrubrik
        subtitle = UIComponents.createSubtitle(languageManager.getString("ordersSubtitle"));

        //Tabell
        orderTable = UIComponents.createTable();

        orderIdColumn = new TableColumn<>(languageManager.getString("orderIdInTable"));
        bookingIdColumn = new TableColumn<>(languageManager.getString("bookingIdInTable"));
        mechanicIdColumn = new TableColumn<>(languageManager.getString("mechanicIdInTable"));
        serviceColumn = new TableColumn<>(languageManager.getString("serviceItemIdsInTable"));
        statusColumn = new TableColumn<>(languageManager.getString("statusInTable"));
        actionColumn = new TableColumn<>(languageManager.getString("actionInTable"));

        // Visar arbetsorder-ID som WO-1, WO-2 osv.
        orderIdColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        "WO-" + cellData.getValue().getId()
                )
        );

        // Visar boknings-ID som Bokning #1, Bokning #2 osv.
        bookingIdColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        languageManager.getString("bookingNumber")
                                + " #"
                                + cellData.getValue().getBookingId()
                )
        );

        // Visar mekanikerns namn istället för mekaniker-ID
        mechanicIdColumn.setCellValueFactory(
                cellData -> {

                    int mechanicId =
                            cellData.getValue().getMechanicId();

                    Mechanic mechanic =
                            mechanicService.findMechanic(mechanicId);

                    if (mechanic == null) {
                        return new SimpleStringProperty("-");
                    }

                    return new SimpleStringProperty(
                            mechanic.getName()
                    );
                }
        );

        serviceColumn.setCellValueFactory(
                cellData -> {

                    WorkOrder workOrder = cellData.getValue();
                    List<ServiceItem> allServices = serviceItemDAO.findAll();

                    String serviceNames = workOrder.getServiceItemIds()
                                    .stream()
                                    .map(serviceId ->
                                            allServices.stream()
                                                    .filter(service ->
                                                            service.getId() == serviceId)
                                                    .findFirst()
                                                    .orElse(null)
                                    )
                            .filter(service -> service != null)
                            .map(service -> getTranslatedServiceName(service.getName()))
                            .collect(
                                    java.util.stream.Collectors.joining(", ")
                            );

                    return new SimpleStringProperty(
                            serviceNames
                    );
                }
        );


        statusColumn.setCellValueFactory(
                cellData -> {

                    String status =
                            cellData.getValue().getStatus();

                    String translatedStatus;

                    switch (status) {

                        case "CREATED":
                            translatedStatus =
                                    languageManager.getString(
                                            "statusCreated"
                                    );
                            break;

                        case "IN_PROGRESS":
                            translatedStatus =
                                    languageManager.getString(
                                            "statusInProgress"
                                    );
                            break;

                        case "COMPLETED":
                            translatedStatus =
                                    languageManager.getString(
                                            "statusCompleted"
                                    );
                            break;

                        default:
                            translatedStatus = status;
                    }

                    return new SimpleStringProperty(
                            translatedStatus
                    );
                }
        );

        statusColumn.setCellFactory(column ->
                new TableCell<WorkOrder, String>() {

                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);

                        if (empty || status == null) {
                            setGraphic(null);
                            return;
                        }

                        setGraphic(
                                UIComponents.createStatusBadge(status)
                        );
                    }
                }
        );


        actionColumn.setCellFactory(column -> new TableCell<WorkOrder, Void>() {
            private final Button viewButton = UIComponents.createViewButton(
                            languageManager.getString("viewOrder"));

            {
                viewButton.setOnAction(event -> {
                    WorkOrder workOrder = getTableView().getItems().get(getIndex());

                    if (onViewOrder != null) {
                        onViewOrder.accept(workOrder);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        //lägg till kolumner
        orderTable.getColumns().addAll(
                orderIdColumn,
                bookingIdColumn,
                mechanicIdColumn,
                serviceColumn,
                statusColumn,
                actionColumn
        );

        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //tillbakaknapp
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));



        //lägg till allt i layouten
        box.getChildren().addAll(
                orderHeader,
                subtitle,
                orderTable,
                backButton
        );
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}
    public TableView<WorkOrder> getOrderTable() {return orderTable;}
    public Button getBackButton() {return backButton;}

    public void setOnViewOrder(Consumer<WorkOrder> onViewOrder) {
        this.onViewOrder = onViewOrder;}

    public void setOnCreateOrderType(Consumer<String> onCreateOrderType) {
        this.onCreateOrderType = onCreateOrderType;
    }

    // Översätter service-namn beroende på valt språk
    private String getTranslatedServiceName(String serviceName) {

        switch (serviceName) {

            case "Diagnostics":
                return languageManager.getString("serviceDiagnostics");

            case "Annual service":
                return languageManager.getString("serviceAnnual");

            case "Brake service":
                return languageManager.getString("serviceBrake");

            case "Oil change":
                return languageManager.getString("serviceOil");

            default:
                return serviceName;
        }
    }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("ordersTitle"));
        subtitle.setText(languageManager.getString("ordersSubtitle"));
        orderIdColumn.setText(languageManager.getString("orderIdInTable"));
        bookingIdColumn.setText(languageManager.getString("bookingIdInTable"));
        mechanicIdColumn.setText(languageManager.getString("mechanicIdInTable"));
        serviceColumn.setText(languageManager.getString("serviceItemIdsInTable"));
        statusColumn.setText(languageManager.getString("statusInTable"));
        actionColumn.setText(languageManager.getString("actionInTable"));
        backButton.setText(languageManager.getString("backButton"));
        createOrderButton.setText(languageManager.getString("createOrderButton"));

        plannedOrderTitle.setText(languageManager.getString("plannedOrder"));
        plannedOrderDescription.setText(languageManager.getString("plannedOrderDescription"));
        dropInOrderTitle.setText(languageManager.getString("dropInOrder"));
        dropInOrderDescription.setText(languageManager.getString("dropInOrderDescription"));
        warrantyOrderTitle.setText(languageManager.getString("warrantyOrder"));
        warrantyOrderDescription.setText(languageManager.getString("warrantyOrderDescription"));

        orderTable.refresh();
    }
}