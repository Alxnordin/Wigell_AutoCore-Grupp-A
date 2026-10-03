package com.wac.autocore.view;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.util.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

//UI-vy som visar arbetsorder listan samt kontroller för att starta och slutföra en arbetsorder
public class OrderListView {

    private final Parent root;
    private final TableView<WorkOrder> orderTable;

    private final TableColumn<WorkOrder, Integer> orderIdColumn;
    private final TableColumn<WorkOrder, Integer> bookingIdColumn;
    private final TableColumn<WorkOrder, Integer> mechanicIdColumn;
    private final TableColumn<WorkOrder, String> serviceColumn;
    private final TableColumn<WorkOrder, String> statusColumn;
    private final TableColumn<WorkOrder, Void> actionColumn;

    private Consumer<WorkOrder> onViewOrder;

    private final Label title;
    private final Label subtitle;

    private final Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public OrderListView() {
        //Huvudlayout
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        //title
        title = new Label(languageManager.getString("ordersTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-list-ul");

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

        //koppla kolumner till workorder
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        mechanicIdColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicId"));

        serviceColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getServiceItemIds().toString()));
        statusColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));


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
                titleBox,
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
    }
}