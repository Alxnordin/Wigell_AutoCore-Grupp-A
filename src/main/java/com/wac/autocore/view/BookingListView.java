package com.wac.autocore.view;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.function.Consumer;

//UI-vyn som visar listan över befintliga bokningar.
public class BookingListView {

    private final Parent root;
    private final TableView<Booking> bookingTable;
    private final Button backButton;

    private Label title;
    private Label subtitle;

    private TableColumn<Booking, String> idColumn;
    private TableColumn<Booking, String> vehicleColumn;
    private TableColumn<Booking, String> dateColumn;
    private TableColumn<Booking, String> descriptionColumn;
    private TableColumn<Booking, String> statusColumn;
    //Alexander
    //kolumn med knappen "Skapa arbetsorder" på varje bokning
    private TableColumn<Booking, Void> createOrderColumn;
    private Consumer<Booking> onCreateWorkOrder;

    //Alexander
    //tjänsterna i den bokning som är markerad i tabellen
    private Label bookingServicesTitle;
    private Label selectBookingLabel;
    private Label lockedLabel;
    private TableView<ServiceItem> bookingServicesTable;
    private TableColumn<ServiceItem, String> serviceColumn;
    private TableColumn<ServiceItem, String> timeColumn;
    private TableColumn<ServiceItem, String> priceColumn;
    private ComboBox<ServiceItem> serviceComboBox;
    private Button addServiceButton;
    private Button removeServiceButton;
    private Label totalTimeTitle;
    private Label totalTimeLabel;
    private Label totalPriceTitle;
    private Label totalPriceLabel;

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingListView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        //title
        title = new Label(languageManager.getString("bookingListTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-calendar");

        //underrubrik
        subtitle = new Label(languageManager.getString("bookingListSubtitle"));
        subtitle.getStyleClass().add("page-subtitle");

        bookingTable = UIComponents.createTable();

        idColumn = new TableColumn<>(languageManager.getString("bookingIdInTable"));
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        vehicleColumn = new TableColumn<>(languageManager.getString("vehicleIdInTable"));
        vehicleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.valueOf(cellData.getValue().getVehicleId())));

        dateColumn = new TableColumn<>(languageManager.getString("dateInTable"));
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getDate())));

        descriptionColumn = new TableColumn<>(languageManager.getString("descriptionInTable"));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        statusColumn = new TableColumn<>(languageManager.getString("statusInTable"));
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        //sätter kolumnernas bredd så dom fyller hela tabellen
        //Alexander
        //bredderna justerade så att knappkolumnen får plats
        idColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.12));
        vehicleColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.12));
        dateColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.16));
        descriptionColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.17));
        statusColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.16));

        //Alexander
        //en knapp per bokning som leder till sidan "Skapa arbetsorder"
        createOrderColumn = new TableColumn<>();
        createOrderColumn.setSortable(false);
        createOrderColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.24));
        createOrderColumn.setCellFactory(column -> new TableCell<Booking, Void>() {
            private final Button createOrderButton = new Button();

            {
                createOrderButton.getStyleClass().add("add-service-button");
                //mindre luft runt knappen så att raden inte blir högre än de andra raderna
                createOrderButton.setStyle("-fx-padding: 3 10 3 10;");
                setStyle("-fx-padding: 4 8 4 8;");

                //vad som händer vid klick bestäms av BookingController (setOnCreateWorkOrder)
                createOrderButton.setOnAction(event -> {
                    if (onCreateWorkOrder != null) {
                        onCreateWorkOrder.accept(getTableView().getItems().get(getIndex()));
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    createOrderButton.setText(languageManager.getString("createOrderButton"));
                    setGraphic(createOrderButton);
                }
            }
        });

        bookingTable.getColumns().addAll(
                idColumn,
                vehicleColumn,
                dateColumn,
                descriptionColumn,
                statusColumn,
                createOrderColumn);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(
                titleBox,
                subtitle,
                bookingTable,
                createBookingServicesSection(),
                backButton
        );
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    //Alexander
    //sektionen under bokningstabellen: tjänsterna i den markerade bokningen,
    //lägg till/ta bort tjänst och summering av tid och pris
    private VBox createBookingServicesSection() {
        bookingServicesTitle = new Label(languageManager.getString("bookingServicesTitle"));
        bookingServicesTitle.getStyleClass().add("section-title");

        bookingServicesTable = UIComponents.createTable();
        bookingServicesTable.setPrefHeight(170);
        //tabellen får inte tryckas ihop helt när fönstret är litet
        bookingServicesTable.setMinHeight(130);

        selectBookingLabel = new Label(languageManager.getString("selectBookingHint"));
        selectBookingLabel.getStyleClass().add("placeholder-text");
        bookingServicesTable.setPlaceholder(selectBookingLabel);

        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
        serviceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        timeColumn = new TableColumn<>(languageManager.getString("timeColumn"));
        timeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstimatedMinutes() + " min"));

        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getPrice())));

        //kolumnerna delar på tabellens bredd (tjänstens namn får dubbelt så mycket plats)
        bookingServicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        serviceColumn.setPrefWidth(300);
        timeColumn.setPrefWidth(150);
        priceColumn.setPrefWidth(150);

        bookingServicesTable.getColumns().addAll(serviceColumn, timeColumn, priceColumn);

        //välj tjänst + lägg till + ta bort
        serviceComboBox = UIComponents.createComboBox();
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

        addServiceButton = new Button(languageManager.getString("addService"));
        addServiceButton.getStyleClass().add("add-service-button");
        removeServiceButton = new Button(languageManager.getString("removeService"));
        removeServiceButton.getStyleClass().add("add-service-button");

        HBox serviceButtons = new HBox(10);
        serviceButtons.setAlignment(Pos.CENTER_LEFT);
        serviceButtons.getChildren().addAll(serviceComboBox, addServiceButton, removeServiceButton);

        //visas bara när arbetet på bokningen har påbörjats
        lockedLabel = new Label(languageManager.getString("bookingLockedInfo"));
        lockedLabel.getStyleClass().add("placeholder-text");
        setLockedMessageVisible(false);

        //summering
        totalTimeTitle = new Label(languageManager.getString("estimatedTotalTime") + ":");
        totalTimeTitle.getStyleClass().add("summary-label");
        totalTimeLabel = new Label("--");
        totalTimeLabel.getStyleClass().add("summary-value");

        totalPriceTitle = new Label(languageManager.getString("estimatedTotalPrice") + ":");
        totalPriceTitle.getStyleClass().add("summary-label");
        totalPriceLabel = new Label("--");
        totalPriceLabel.getStyleClass().add("summary-value");

        HBox summaryBox = new HBox(10);
        summaryBox.setAlignment(Pos.CENTER_LEFT);
        summaryBox.getChildren().addAll(totalTimeTitle, totalTimeLabel, totalPriceTitle, totalPriceLabel);
        HBox.setMargin(totalPriceTitle, new Insets(0, 0, 0, 30));

        VBox section = UIComponents.createSectionBox();
        section.setSpacing(12);
        section.getChildren().addAll(
                bookingServicesTitle,
                bookingServicesTable,
                serviceButtons,
                lockedLabel,
                summaryBox
        );
        return section;
    }

    public Parent getView() {return root;}

    //Alexander
    //används av BookingController
    public TableView<ServiceItem> getBookingServicesTable() {return bookingServicesTable;}
    public ComboBox<ServiceItem> getServiceComboBox() {return serviceComboBox;}
    public Button getAddServiceButton() {return addServiceButton;}
    public Button getRemoveServiceButton() {return removeServiceButton;}

    //Alexander
    //BookingController anger vad som ska hända när man trycker på "Skapa arbetsorder"
    public void setOnCreateWorkOrder(Consumer<Booking> action) {
        this.onCreateWorkOrder = action;
    }

    //Alexander
    //visar summeringen, värdena räknas ut i GarageSystem, vyn visar dem bara.
    public void showSummary(String totalTime, String totalPrice) {
        totalTimeLabel.setText(totalTime);
        totalPriceLabel.setText(totalPrice);
    }

    //Alexander
    //stänger av "lägg till"/"ta bort" när ingen bokning är vald eller arbetet har påbörjats
    public void setServiceEditingDisabled(boolean disabled) {
        serviceComboBox.setDisable(disabled);
        addServiceButton.setDisable(disabled);
        removeServiceButton.setDisable(disabled);
    }

    //Alexander
    //texten som förklarar att tjänsterna är låsta
    public void setLockedMessageVisible(boolean visible) {
        lockedLabel.setVisible(visible);
        lockedLabel.setManaged(visible);
    }

    public TableView<Booking> getBookingTable() {return bookingTable;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        idColumn.setText(languageManager.getString("bookingIdInTable"));
        vehicleColumn.setText(languageManager.getString("vehicleIdInTable"));
        dateColumn.setText(languageManager.getString("dateInTable"));
        descriptionColumn.setText(languageManager.getString("descriptionInTable"));
        statusColumn.setText(languageManager.getString("statusInTable"));
        backButton.setText(languageManager.getString("backButton"));
        title.setText(languageManager.getString("bookingListTitle"));
        subtitle.setText(languageManager.getString("bookingListSubtitle"));

        //Alexander
        bookingServicesTitle.setText(languageManager.getString("bookingServicesTitle"));
        selectBookingLabel.setText(languageManager.getString("selectBookingHint"));
        lockedLabel.setText(languageManager.getString("bookingLockedInfo"));
        serviceColumn.setText(languageManager.getString("serviceColumn"));
        timeColumn.setText(languageManager.getString("timeColumn"));
        priceColumn.setText(languageManager.getString("priceColumn"));
        addServiceButton.setText(languageManager.getString("addService"));
        removeServiceButton.setText(languageManager.getString("removeService"));
        totalTimeTitle.setText(languageManager.getString("estimatedTotalTime") + ":");
        totalPriceTitle.setText(languageManager.getString("estimatedTotalPrice") + ":");
    }
}