package com.wac.autocore.view.booking;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
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

    private final Label title;
    private final Label subtitle;

    private TableColumn<Booking, String> idColumn;
    private TableColumn<Booking, String> vehicleColumn;
    private TableColumn<Booking, String> dateColumn;
    private TableColumn<Booking, String> descriptionColumn;
    private TableColumn<Booking, String> statusColumn;
    private TableColumn<Booking, Void> createOrderColumn;
    private Consumer<Booking> onCreateWorkOrder;
    private TableColumn<Booking, Void> viewColumn;
    private Consumer<Booking> onViewBooking;

    //tjänsterna i den bokning som är markerad i tabellen
//    private Label bookingServicesTitle;
//    private Label selectBookingLabel;
//    private Label lockedLabel;
//    private TableView<ServiceItem> bookingServicesTable;
//    private TableColumn<ServiceItem, String> serviceColumn;
//    private TableColumn<ServiceItem, String> timeColumn;
//    private TableColumn<ServiceItem, String> priceColumn;
//    private ComboBox<ServiceItem> serviceComboBox;
//    private Button addServiceButton;
//    private Button removeServiceButton;
//    private Label totalTimeTitle;
//    private Label totalTimeLabel;
//    private Label totalPriceTitle;
//    private Label totalPriceLabel;

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingListView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        title = new Label(languageManager.getString("bookingListTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-calendar");

        subtitle = UIComponents.createSubtitle(
                languageManager.getString("bookingListSubtitle"));

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
        //bredderna justerade så att knappkolumnen får plats
        idColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.12));
        vehicleColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.12));
        dateColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.16));
        descriptionColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.17));
        statusColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.16));

        //en knapp per bokning som leder till sidan "Skapa arbetsorder"
        createOrderColumn = new TableColumn<>(languageManager.getString("createOrderColumn"));
        createOrderColumn.setSortable(false);
        createOrderColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.24));
        createOrderColumn.setCellFactory(column -> new TableCell<Booking, Void>() {

            private final Button createOrderButton = UIComponents.createAddRemoveServiceButton("");

            {
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

        viewColumn = new TableColumn<>(languageManager.getString("view"));
        viewColumn.setSortable(false);

        viewColumn.setCellFactory(column -> new TableCell<Booking, Void>() {

            Button viewButton = UIComponents.createViewButton(languageManager.getString("view"));

            {
                viewButton.setOnAction(event -> {
                    if (onViewBooking != null) {
                        Booking booking = getTableView().getItems().get(getIndex());
                        onViewBooking.accept(booking);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    viewButton.setText(languageManager.getString("view"));
                    setGraphic(viewButton);
                }
            }
        });

        bookingTable.getColumns().addAll(
                idColumn,
                vehicleColumn,
                dateColumn,
                descriptionColumn,
                statusColumn,
                createOrderColumn,
                viewColumn);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(
                titleBox,
                subtitle,
                bookingTable,
//                createBookingServicesSection(),
                backButton
        );
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    //sektionen under bokningstabellen: tjänsterna i den markerade bokningen,
    //lägg till/ta bort tjänst och summering av tid och pris
//    private VBox createBookingServicesSection() {
//        bookingServicesTitle = UIComponents.createSectionTitle(
//        languageManager.getString("bookingServicesTitle"));
//
//        bookingServicesTable = UIComponents.createTable();
//        bookingServicesTable.setPrefHeight(170);
//        //tabellen får inte tryckas ihop helt när fönstret är litet
//        bookingServicesTable.setMinHeight(130);
//
//        selectBookingLabel = UIComponents.createPlaceholderLabel(
//        languageManager.getString("selectBookingHint"));
//        bookingServicesTable.setPlaceholder(selectBookingLabel);
//
//        serviceColumn = new TableColumn<>(languageManager.getString("serviceColumn"));
//        serviceColumn.setCellValueFactory(cellData ->
//                new SimpleStringProperty(cellData.getValue().getName()));
//
//        timeColumn = new TableColumn<>(languageManager.getString("timeColumn"));
//        timeColumn.setCellValueFactory(cellData ->
//                new SimpleStringProperty(cellData.getValue().getEstimatedMinutes() + " min"));
//
//        priceColumn = new TableColumn<>(languageManager.getString("priceColumn"));
//        priceColumn.setCellValueFactory(cellData ->
//                new SimpleStringProperty(String.format("%.2f kr", cellData.getValue().getPrice())));
//
//        //kolumnerna delar på tabellens bredd (tjänstens namn får dubbelt så mycket plats)
//        bookingServicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        serviceColumn.setPrefWidth(300);
//        timeColumn.setPrefWidth(150);
//        priceColumn.setPrefWidth(150);
//
//        bookingServicesTable.getColumns().addAll(serviceColumn, timeColumn, priceColumn);
//
//        //välj tjänst + lägg till + ta bort
//        serviceComboBox = UIComponents.createComboBox();
//        serviceComboBox.setConverter(new StringConverter<ServiceItem>() {
//            @Override
//            public String toString(ServiceItem serviceItem) {
//                return serviceItem == null ? "" : serviceItem.getName();
//            }
//            @Override
//            public ServiceItem fromString(String string) {
//                return null;
//            }
//        });
//
//        addServiceButton = UIComponents.createAddRemoveServiceButton(
//                languageManager.getString("addService"));
//
//        removeServiceButton = UIComponents.createAddRemoveServiceButton(
//                languageManager.getString("removeService"));
//
//        HBox serviceButtons = new HBox(10);
//        serviceButtons.setAlignment(Pos.CENTER_LEFT);
//        serviceButtons.getChildren().addAll(serviceComboBox, addServiceButton, removeServiceButton);

//        lockedLabel = UIComponents.createPlaceholderLabel(languageManager.getString("bookingLockedInfo"));
//        setLockedMessageVisible(false);
//
//        totalTimeTitle = new Label(languageManager.getString("estimatedTotalTime") + ":");
//        totalTimeTitle.getStyleClass().add("summary-label");
//        totalTimeLabel = new Label("--");
//        totalTimeLabel.getStyleClass().add("summary-value");
//
//        totalPriceTitle = new Label(languageManager.getString("estimatedTotalPrice") + ":");
//        totalPriceTitle.getStyleClass().add("summary-label");
//        totalPriceLabel = new Label("--");
//        totalPriceLabel.getStyleClass().add("summary-value");
//
//        HBox summaryBox = new HBox(10);
//        summaryBox.setAlignment(Pos.CENTER_LEFT);
//        summaryBox.getChildren().addAll(totalTimeTitle, totalTimeLabel, totalPriceTitle, totalPriceLabel);
//        HBox.setMargin(totalPriceTitle, new Insets(0, 0, 0, 30));

//        VBox section = UIComponents.createSectionBox();
//        section.setSpacing(12);
//        section.getChildren().addAll(
//                bookingServicesTitle,
//                bookingServicesTable,
//                serviceButtons,
//                lockedLabel,
//                summaryBox
//        );
//        return section;
//    }

    public Parent getView() {return root;}

//    public TableView<ServiceItem> getBookingServicesTable() {return bookingServicesTable;}
//    public ComboBox<ServiceItem> getServiceComboBox() {return serviceComboBox;}
//    public Button getAddServiceButton() {return addServiceButton;}
//    public Button getRemoveServiceButton() {return removeServiceButton;}

    //BookingController anger vad som ska hända när man trycker på "Skapa arbetsorder"
    public void setOnCreateWorkOrder(Consumer<Booking> action) {
        this.onCreateWorkOrder = action;
    }

    public void setOnViewBooking(Consumer<Booking> action) {
        this.onViewBooking = action;
    }

    //visar summeringen, värdena räknas ut i GarageSystem, vyn visar dem bara.
//    public void showSummary(String totalTime, String totalPrice) {
//        totalTimeLabel.setText(totalTime);
//        totalPriceLabel.setText(totalPrice);
//    }

    //stänger av "lägg till"/"ta bort" när ingen bokning är vald eller arbetet har påbörjats
//    public void setServiceEditingDisabled(boolean disabled) {
//        serviceComboBox.setDisable(disabled);
//        addServiceButton.setDisable(disabled);
//        removeServiceButton.setDisable(disabled);
//    }

    //texten som förklarar att tjänsterna är låsta
//    public void setLockedMessageVisible(boolean visible) {
//        lockedLabel.setVisible(visible);
//        lockedLabel.setManaged(visible);
//    }

    public TableView<Booking> getBookingTable() {return bookingTable;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        idColumn.setText(languageManager.getString("bookingIdInTable"));
        vehicleColumn.setText(languageManager.getString("vehicleIdInTable"));
        dateColumn.setText(languageManager.getString("dateInTable"));
        descriptionColumn.setText(languageManager.getString("descriptionInTable"));
        statusColumn.setText(languageManager.getString("statusInTable"));
        createOrderColumn.setText(languageManager.getString("createOrderColumn"));
        viewColumn.setText(languageManager.getString("view"));

        backButton.setText(languageManager.getString("backButton"));
        title.setText(languageManager.getString("bookingListTitle"));
        subtitle.setText(languageManager.getString("bookingListSubtitle"));

//        bookingServicesTitle.setText(languageManager.getString("bookingServicesTitle"));
//        selectBookingLabel.setText(languageManager.getString("selectBookingHint"));
//        lockedLabel.setText(languageManager.getString("bookingLockedInfo"));
//        serviceColumn.setText(languageManager.getString("serviceColumn"));
//        timeColumn.setText(languageManager.getString("timeColumn"));
//        priceColumn.setText(languageManager.getString("priceColumn"));
//
//        addServiceButton.setText(languageManager.getString("addService"));
//        removeServiceButton.setText(languageManager.getString("removeService"));
//        totalTimeTitle.setText(languageManager.getString("estimatedTotalTime") + ":");
//        totalPriceTitle.setText(languageManager.getString("estimatedTotalPrice") + ":");
    }
}