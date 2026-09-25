package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.CustomerDAO;
import com.wac.autocore.data.Database;
import com.wac.autocore.model.Customer;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.CustomerView;
import javafx.scene.Parent;

public class CustomerController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final CustomerView customerView;
    //FREDRIK - lagt till
    private final CustomerDAO customerDAO = new CustomerDAO();

    public CustomerController(GarageSystem garageSystem, AutoCoreApplication app, CustomerView customerView) {
        this.garageSystem =garageSystem;
        this.app = app;
        this.customerView = customerView;
        wireEvents();
        refreshCustomerList();
    }

    public void wireEvents() {
        customerView.getCreateCustomerButton().setOnAction(actionEvent -> {
            String name = customerView.getNameField().getText();
            String phone = customerView.getPhoneField().getText();
            String email = customerView.getEmailField().getText();

            Customer customer = garageSystem.createCustomer(name,phone,email);

            if (customer != null) {
            refreshCustomerList();
            customerView.getNameField().clear();
            customerView.getPhoneField().clear();
            customerView.getEmailField().clear();
        }
    });
        customerView.getBackButton().setOnAction(actionEvent ->
                app.showMainMenu());
    }
    private void refreshCustomerList() {
    customerView.getCustomerListView().getItems().clear();

    //FREDRIK - ändrat
    //for (Customer customer : Database.getCustomers())
    for (Customer customer : customerDAO.findAll()){
        customerView.getCustomerListView().getItems().add(customer.toString());
    }
}

    public Parent getView() {
        return customerView.getView();
    }
}
