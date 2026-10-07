package com.wac.autocore.view.service;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.function.Consumer;

//Visar listan över tillgängliga tjänster
public class ServiceItemView {

    private final Parent root;


    private Label serviceLabel;

    private TableView<ServiceItem> serviceItemTableView;
    private Button backButton;

    private TableColumn<ServiceItem, String> nameColumn;
    private TableColumn<ServiceItem, Double> priceColumn;
    private TableColumn<ServiceItem, Void> actionColumn;

    LanguageManager languageManager = LanguageManager.getInstance();

    public ServiceItemView(){

        VBox box = UIComponents.createVBoxForViews();


        serviceLabel = UIComponents.createSubtitle(languageManager.getString("serviceLabel"));

        serviceItemTableView = UIComponents.createTable();
        nameColumn = new TableColumn<>(languageManager.getString("serviceItemName"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn = new TableColumn<>(languageManager.getString("serviceItemPrice"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        actionColumn = new TableColumn<>(languageManager.getString("changePrice"));

        serviceItemTableView.getColumns().addAll(nameColumn, priceColumn, actionColumn);

        backButton= UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(serviceLabel, serviceItemTableView, backButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        }
        );
    }

    public Parent getView() { return root; }
    public TableView<ServiceItem> getServiceItemTableView() { return serviceItemTableView; }
    public Button getBackButton() { return backButton; }


    public void setOnChangePrice(Consumer<ServiceItem> action) {

        actionColumn.setCellFactory(column ->
                new TableCell<ServiceItem, Void>() {

                    private final Button button = new Button();

                    {
                        button.setText(
                                languageManager.getString("changePrice")
                        );

                        button.setOnAction(event -> {

                            ServiceItem serviceItem =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            if (serviceItem != null) {
                                action.accept(serviceItem);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {

                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                }
        );
    }


    public void changeTextAllComponents() {
        serviceLabel.setText(languageManager.getString("serviceLabel"));
        nameColumn.setText(languageManager.getString("serviceItemName"));
        priceColumn.setText(languageManager.getString("serviceItemPrice"));
        actionColumn.setText(languageManager.getString("changePrice"));
        backButton.setText(languageManager.getString("backButton"));
    }
}