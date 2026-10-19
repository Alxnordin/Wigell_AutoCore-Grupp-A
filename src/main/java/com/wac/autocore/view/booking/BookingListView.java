package com.wac.autocore.view.booking;

import com.wac.autocore.model.Booking;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;
import java.util.function.Function;

//UI-vyn som visar listan över befintliga bokningar.
public class BookingListView {

    private final Parent root;
    private TableView<Booking> bookingTable;
    private final Button backButton;

    private final Label title;
    private final Label subtitle;

    //Ej klar med sökfunktionen
    private TextField searchField;
    private ObservableList<Booking> observableBookingList;
    private FilteredList<Booking> filteredBookingList;

    private TableColumn<Booking, String> idColumn;
    private TableColumn<Booking, String> vehicleColumn;
    private TableColumn<Booking, String> dateColumn;
    private TableColumn<Booking, String> descriptionColumn;
    private TableColumn<Booking, String> statusColumn;
    private TableColumn<Booking, Void> createOrderColumn;
    private Consumer<Booking> onCreateWorkOrder;
    private TableColumn<Booking, Void> viewAndEditColumn;
    private Consumer<Booking> onViewBooking;
    private Function<Integer, String> getRegistrationNumber;

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingListView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        title = new Label(languageManager.getString("bookingListTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-calendar");
        subtitle = UIComponents.createSubtitle(languageManager.getString("bookingListSubtitle"));

        HBox searchBox = buildSearchSection();
        bookingTable = buildTable();

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(titleBox, subtitle, searchBox, bookingTable, backButton);
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
            bookingTable.refresh();
        });
    }

    public Parent getView() {return root;}

    //BookingController anger vad som ska hända när man trycker på "Skapa arbetsorder"
    public void setOnCreateWorkOrder(Consumer<Booking> action) {
        this.onCreateWorkOrder = action;
    }

    public void setOnViewBooking(Consumer<Booking> action) {
        this.onViewBooking = action;
    }

    public TableView<Booking> getBookingTable() {return bookingTable;}

    //Används ännu inte
    public ObservableList<Booking> getObservableBookingList() {return observableBookingList;}

    public void setGetRegistrationNumber(Function<Integer, String> function) {
        this.getRegistrationNumber = function;
    }
    public Button getBackButton() {return backButton;}

    public TableView<Booking> buildTable()  {
        bookingTable = UIComponents.createTable();
        bookingTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        vehicleColumn = new TableColumn<>(languageManager.getString("regNrBookingTable"));
        vehicleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
               getRegistrationNumber.apply(cellData.getValue().getVehicleId())));

        dateColumn = new TableColumn<>(languageManager.getString("dateInTable"));
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getDate())));

        descriptionColumn = new TableColumn<>(languageManager.getString("descriptionInTable"));
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        statusColumn = new TableColumn<>(languageManager.getString("statusInTable"));

        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatus())
        );

        statusColumn.setCellFactory(column -> new TableCell<Booking, String>() {

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                   Label statusLabel = UIComponents.createStatusBadge(status);
                    //Alexander
                    //okänd status, gammal data visas som den är i stället för att krascha listan
                    try {
                        statusLabel.setText(languageManager.getString(status));
                    } catch (java.util.MissingResourceException e) {
                        statusLabel.setText(status);
                    }

                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });

        //en knapp per bokning som leder till sidan "Skapa arbetsorder"
        createOrderColumn = new TableColumn<>(languageManager.getString("createOrderColumn"));
        createOrderColumn.setSortable(false);
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
                    createOrderButton.setText(languageManager.getString("createOrderButtonBookingTable"));
                    setGraphic(createOrderButton);
                }
            }
        });

        viewAndEditColumn = new TableColumn<>(languageManager.getString("actionColumn"));
        viewAndEditColumn.setSortable(false);
        viewAndEditColumn.setCellFactory(column -> new TableCell<Booking, Void>() {

        Button viewButton = UIComponents.createViewButton(languageManager.getString("viewBookingInTableButton"));

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
                    viewButton.setText(languageManager.getString("viewBookingInTableButton"));
                    setGraphic(viewButton);
                }
            }
        });

        //Storlek på kolumnerna
        idColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.07));
        vehicleColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.11));
        dateColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.13));
        descriptionColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.16));
        statusColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.17));
        createOrderColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.16));
        viewAndEditColumn.prefWidthProperty().bind(bookingTable.widthProperty().multiply(0.17));

        bookingTable.getColumns().addAll(
                idColumn,
                vehicleColumn,
                dateColumn,
                descriptionColumn,
                statusColumn,
                createOrderColumn,
                viewAndEditColumn);

        return bookingTable;
    }

    //EJ KLAR
    public HBox buildSearchSection() {
        searchField = new TextField();
        HBox searchBox = UIComponents.createSearchBox(searchField);
        searchField.setPromptText(languageManager.getString("searchTextField"));

//        searchField.textProperty().addListener((
//                observable, oldValue, newValue) -> {
//            filteredBookingList.setPredicate(booking -> {
//
//                if (newValue == null || newValue.isEmpty()) {
//                    return true;
//                }
//
//                return booking.getDescription().toLowerCase().contains(newValue.toLowerCase());
//
//            });
//        });
//
//        observableBookingList = FXCollections.observableArrayList();
//        filteredBookingList = new FilteredList<>(observableBookingList, customer -> true);
//        bookingTable.setItems(filteredBookingList);

        return searchBox;
    }

    public void changeTextAllComponents() {
        idColumn.setText(languageManager.getString("bookingIdBookingTable"));
        vehicleColumn.setText(languageManager.getString("regNrBookingTable"));
        dateColumn.setText(languageManager.getString("dateInTable"));
        descriptionColumn.setText(languageManager.getString("descriptionInTable"));
        statusColumn.setText(languageManager.getString("statusInTable"));

        createOrderColumn.setText(languageManager.getString("createOrderColumn"));
        viewAndEditColumn.setText(languageManager.getString("actionColumn"));
        backButton.setText(languageManager.getString("backButton"));
        title.setText(languageManager.getString("bookingListTitle"));
        subtitle.setText(languageManager.getString("bookingListSubtitle"));
        searchField.setText(languageManager.getString("searchTextField"));
    }
}