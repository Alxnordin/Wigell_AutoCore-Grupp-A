package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.CustomerService;
import com.wac.autocore.model.Customer;
import com.wac.autocore.view.customer.CustomerListView;
import com.wac.autocore.view.customer.CustomerView;
import com.wac.autocore.view.ErrorHandler;
import javafx.scene.Parent;

public class CustomerController {

    private final CustomerService customerService;
    private final AutoCoreApplication app;
    private final CustomerView customerView;
    private final CustomerListView customerListView;

    public CustomerController(CustomerService customerService,
            AutoCoreApplication app, CustomerView customerView,
            CustomerListView customerListView) {
        this.customerService = customerService;
        this.app = app;
        this.customerView = customerView;
        this.customerListView = customerListView;
        wireEvents();
        refreshCustomerList();
    }


    public void wireEvents() {
        //Alexander
        //Controllern läser bara formuläret och anropar servicen.
        //Kontrollen av tomma fält ligger nu i CustomerService, och fel visas via ErrorHandler.
        customerView.getCreateCustomerButton().setOnAction(actionEvent -> {

            String firstName = customerView.getFirstNameField().getText();
            String lastName = customerView.getLastNameField().getText();
            String phoneNumber = customerView.getPhoneField().getText();
            String email = customerView.getEmailField().getText();

            try {
                customerService.createCustomer(firstName, lastName, phoneNumber, email);

                refreshCustomerList();

                customerView.getFirstNameField().clear();
                customerView.getLastNameField().clear();
                customerView.getPhoneField().clear();
                customerView.getEmailField().clear();

                //Ej klar med denna
                customerView.getConfirmationLabel().setText(
                        "Kund " + firstName.trim() + " " + lastName.trim() + " har lagts till.");

            } catch (Exception e) {
                ErrorHandler.show(e);
            }
        });

        customerView.getBackButton().setOnAction(actionEvent -> {
            app.showCustomerListView();
        });

        customerListView.getCreateCustomerButton().setOnAction(actionEvent -> {
            app.showView(customerView.getView());
        });

        customerListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private void refreshCustomerList() {
        customerListView.getObservableCustomersList().clear();

        for (Customer customer : customerService.getAllCustomers()) {
            customerListView.getObservableCustomersList().add(customer);
        }
    }

    public Parent getCustomerView() {
        return customerView.getView();
    }

    public Parent getCustomerListView() {
        return customerListView.getView();
    }

}
