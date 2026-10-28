package com.wac.autocore.view.mechanic;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Function;


//Visar mekanikerlistan och den valda mekanikerns tilldelade arbetsordrar
public class MechanicView {
    private final Parent root;

    private Label mechanicLabel;
    private Label mechanicSubtitle;
    private TableView<Mechanic> mechanicListView;

    private Label mechanicWorkLabel;
    private TableView<WorkOrder> mechanicWorkTable;

    private TableColumn<WorkOrder, Number> workOrderIdColumn;
    private TableColumn<WorkOrder, String> bookingIdColumn;
    private TableColumn<WorkOrder, String> servicesColumn;
    private TableColumn<WorkOrder, String> statusColumn;

    // Sparar kolumnerna så att rubrikerna kan språkväxlas.
    private TableColumn<Mechanic, Number> idColumn;
    private TableColumn<Mechanic, String> nameColumn;
    private TableColumn<Mechanic, String> phoneColumn;
    private TableColumn<Mechanic, String> specializationColumn;
    private TableColumn<Mechanic, String> availabilityColumn;

    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public MechanicView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        mechanicLabel = new Label(languageManager.getString("mechanicLabel"));
        box.getChildren().add(
                UIComponents.createPageTitle(
                        mechanicLabel,
                        "fa-wrench"
                )
        );

        // Lista med alla mekaniker.
        VBox mechanicSection = UIComponents.createSectionBox();

        // Återanvänder tabellens gemensamma styling.
        mechanicListView = UIComponents.createTable();
        mechanicListView.setPrefHeight(250);
        mechanicListView.setMaxWidth(Double.MAX_VALUE);

        mechanicSubtitle = UIComponents.createSubtitle(
                languageManager.getString("mechanicSubtitle")
        );

        // Tabellen ligger kvar i sektionsrutan.
        mechanicSection.getChildren().add(mechanicListView);

        // Underrubriken placeras direkt under huvudrubriken.
        box.getChildren().add(mechanicSubtitle);

        // Sektion för den valda mekanikerns arbete.
        VBox workSection = UIComponents.createSectionBox();

        // Skapar rubriken för arbetsordersektionen.
        mechanicWorkLabel = UIComponents.createSubtitle(
                languageManager.getString("mechanicWorkLabel")
        );

        // Skapar arbetsordertabellen med projektets gemensamma tabellstil.
        mechanicWorkTable = UIComponents.createTable();
        mechanicWorkTable.setPrefHeight(250);
        mechanicWorkTable.setMaxWidth(Double.MAX_VALUE);

        // Skapar arbetsorderns kolumner med rubriker från språkfilen.
        workOrderIdColumn = new TableColumn<>(
                languageManager.getString("workOrderId"));

        bookingIdColumn = new TableColumn<>(
                languageManager.getString("workOrderBookingId"));

        servicesColumn = new TableColumn<>(
                languageManager.getString("workOrderServices"));

        statusColumn = new TableColumn<>(
                languageManager.getString("workOrderStatus"));


        workOrderIdColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getId()
                )
        );

        bookingIdColumn.setCellValueFactory(cell -> {
            Integer bookingId = cell.getValue().getBookingId();

            return new javafx.beans.property.SimpleStringProperty(
                    bookingId == null ? "—" : bookingId.toString()
            );
        });

        // Visar tjänsternas namn i stället för deras ID:n.
        servicesColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        serviceNamesProvider.apply(cell.getValue())
                )
        );

        statusColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        getLocalizedWorkOrderStatus(cell.getValue().getStatus())
                )
        );

        // Visar status som en färgad badge i arbetsordertabellen.
        statusColumn.setCellFactory(column ->
                new TableCell<WorkOrder, String>() {

                    private final Label statusLabel = new Label();

                    {
                        statusLabel.setMaxWidth(Double.MAX_VALUE);
                        statusLabel.setAlignment(javafx.geometry.Pos.CENTER);
                        statusLabel.getStyleClass().add("status-badge");
                    }

                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);

                        if (empty || status == null) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        statusLabel.setText(status);

                        // Rensar tidigare statusklass när cellen återanvänds.
                        statusLabel.getStyleClass().removeAll(
                                "status-created",
                                "status-in-progress",
                                "status-completed",
                                "status-draft"
                        );

                        // Väljer färgklass utifrån arbetsorderns status.
                        switch (status) {
                            case "Created":
                            case "Skapad":
                                statusLabel.getStyleClass().add("status-created");
                                break;

                            case "In progress":
                            case "Pågående":
                                statusLabel.getStyleClass().add("status-in-progress");
                                break;

                            case "Completed":
                            case "Slutförd":
                                statusLabel.getStyleClass().add("status-completed");
                                break;

                            case "Draft":
                            case "Utkast":
                                statusLabel.getStyleClass().add("status-draft");
                                break;

                            default:
                                break;
                        }

                        setText(null);
                        setGraphic(statusLabel);
                    }
                }
        );

        // Lägger till kolumnerna i tabellen.
        mechanicWorkTable.getColumns().addAll(
                workOrderIdColumn,
                bookingIdColumn,
                servicesColumn,
                statusColumn
        );

        // Skapar separata kolumner för mekanikerns uppgifter.
        idColumn = new TableColumn<>(
                languageManager.getString("mechanicId"));
        idColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getId()
                )
        );

        nameColumn = new TableColumn<>(
                languageManager.getString("mechanicName"));
        nameColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getName()
                )
        );

        phoneColumn = new TableColumn<>(
                languageManager.getString("mechanicPhone"));
        phoneColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getPhone()
                )
        );

        specializationColumn = new TableColumn<>(
                        languageManager.getString("mechanicSpecialization")
                );
        specializationColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getSpecialization()
                )
        );

        availabilityColumn = new TableColumn<>(
                languageManager.getString("available"));
        availabilityColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        languageManager.getString(
                                cell.getValue().isAvailable()
                                        ? "availableYes"
                                        : "availableNo"
                        )
                )
        );

        // Låter kolumnerna dela på tabellens tillgängliga bredd.
        mechanicListView.getColumns().addAll(
                idColumn,
                nameColumn,
                phoneColumn,
                specializationColumn,
                availabilityColumn
        );

        mechanicListView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        mechanicListView.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );
        mechanicListView.setPrefHeight(250);


        workSection.getChildren().addAll(
                mechanicWorkLabel,
                mechanicWorkTable
        );

        // Låter listorna växa med tillgängligt utrymme.
        VBox.setVgrow(mechanicSection, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(workSection, javafx.scene.layout.Priority.ALWAYS);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(
                mechanicSection,
                workSection,
                backButton
        );

        // Håller båda listorna flexibla när fönstret ändras.
        mechanicSection.setMaxHeight(Double.MAX_VALUE);
        workSection.setMaxHeight(Double.MAX_VALUE);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    private Function<WorkOrder, String> serviceNamesProvider =
            workOrder -> workOrder.getServiceItemIds().toString();

    // Gör det möjligt för controllern att tillhandahålla tjänsternas namn.
    public void setServiceNamesProvider(Function<WorkOrder, String> provider) {
        this.serviceNamesProvider = provider;
        mechanicWorkTable.refresh();
    }

    // Översätter arbetsorderns status utifrån valt gränssnittsspråk.
    private String getLocalizedWorkOrderStatus(String status) {
        switch (status) {
            case "CREATED":
            case "WORK_ORDER_CREATED":
                return languageManager.getString("statusCreated");

            case "IN_PROGRESS":
                return languageManager.getString("statusInProgress");

            case "COMPLETED":
                return languageManager.getString("statusCompleted");

            case "DRAFT":
                return languageManager.getString("statusDraft");

            default:
                return status;
        }
    }

    public Parent getView() { return root; }
    public TableView<Mechanic> getMechanicListView() { return mechanicListView; }
    public TableView<WorkOrder> getMechanicWorkTable() {return mechanicWorkTable;}
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        mechanicLabel.setText(languageManager.getString("mechanicLabel"));
        mechanicSubtitle.setText(languageManager.getString("mechanicSubtitle"));
        mechanicWorkLabel.setText(languageManager.getString("mechanicWorkLabel"));
        backButton.setText(languageManager.getString("backButton"));

        idColumn.setText(languageManager.getString("mechanicId"));
        nameColumn.setText(languageManager.getString("mechanicName"));
        phoneColumn.setText(languageManager.getString("mechanicPhone"));
        specializationColumn.setText(languageManager.getString("mechanicSpecialization"));
        availabilityColumn.setText(languageManager.getString("available"));

        workOrderIdColumn.setText(languageManager.getString("workOrderId"));
        bookingIdColumn.setText(languageManager.getString("workOrderBookingId"));
        servicesColumn.setText(languageManager.getString("workOrderServices"));
        statusColumn.setText(languageManager.getString("workOrderStatus"));

        mechanicWorkTable.refresh();
    }
}