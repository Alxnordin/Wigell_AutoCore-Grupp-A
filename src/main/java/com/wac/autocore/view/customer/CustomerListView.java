package com.wac.autocore.view.customer;

import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerListView {

    private final Parent root;

    private Label title;
    private Label subtitle;

    private TableView<Customer> customerTable;

    private TableColumn<Customer, Integer> customerIdColumn;
    private TableColumn<Customer, String> nameColumn;
    private TableColumn<Customer, String> phoneColumn;
    private TableColumn<Customer, String> emailColumn;
    private TableColumn<Customer, String> vipColumn;

    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public CustomerListView() {

        VBox box = UIComponents.createVBoxForViews();
        title = new Label(languageManager.getString("customerListViewTitle"));
        HBox titleHBox = UIComponents.createPageTitle(title, "fa-address-book");
        subtitle = UIComponents.createSubtitle(languageManager.getString("customerListViewSubtitle"));

        customerTable = UIComponents.createTable();

        customerIdColumn = new TableColumn<>(languageManager.getString("customerIdInTable"));
        nameColumn = new TableColumn<>(languageManager.getString("nameInTable"));
        phoneColumn = new TableColumn<>(languageManager.getString("phoneInTable"));
        emailColumn = new TableColumn<>(languageManager.getString("emailInTable"));
        vipColumn = new TableColumn<>(languageManager.getString("vipInTable"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        vipColumn.setCellValueFactory(cellData -> {

            Customer customer = cellData.getValue();
            String text;

            if (customer.isVip()) {
                text = languageManager.getString("vipYes");
            } else {
                text = languageManager.getString("vipNo");
            }

            return new SimpleStringProperty(text);
        });

        customerTable.getColumns().addAll(customerIdColumn, nameColumn, phoneColumn, emailColumn, vipColumn);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(titleHBox, subtitle, customerTable, backButton);
        this.root = box;

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    changeTextAllComponents();
                }
        );
    }

    public Parent getView() {return root;}
    public TableView<Customer> getCustomerListView() {return customerTable;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {

        title.setText(languageManager.getString("customerListViewTitle"));
        subtitle.setText(languageManager.getString("customerListViewSubtitle"));

        customerIdColumn.setText(languageManager.getString("customerIdInTable"));
        nameColumn.setText(languageManager.getString("nameInTable"));
        phoneColumn.setText(languageManager.getString("phoneInTable"));
        emailColumn.setText(languageManager.getString("emailInTable"));
        vipColumn.setText(languageManager.getString("vipInTable"));

        backButton.setText(languageManager.getString("backButton"));

        customerTable.refresh();
    }
}
