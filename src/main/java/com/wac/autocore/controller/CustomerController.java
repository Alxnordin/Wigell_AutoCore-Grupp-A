package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.service.CustomerService;
import com.wac.autocore.model.Customer;
import com.wac.autocore.view.customer.CustomerListView;
import com.wac.autocore.view.customer.CustomerView;
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

    //Saknas översättning på felmeddelandena
    public void wireEvents() {
        customerView.getCreateCustomerButton().setOnAction(actionEvent -> {

            boolean validInput = true;

            String firstName = customerView.getFirstNameField().getText();
            String lastName = customerView.getLastNameField().getText();
            String fullName = firstName.trim() + " " + lastName.trim();

            if (customerService.isInputFieldEmpty(firstName)) {
                customerView.getFirstNameWrongInputLabel().setText("Du måste fylla i namn");
                validInput = false;
            } else {
                customerView.getFirstNameWrongInputLabel().setText("");
            }

            if (customerService.isInputFieldEmpty(lastName)) {
                customerView.getLastNameWrongInputLabel().setText("Du måste fylla i namn");
                validInput = false;
            } else {
                customerView.getLastNameWrongInputLabel().setText("");
            }

            String phoneNumber = customerView.getPhoneField().getText().trim();
            if (customerService.isInputFieldEmpty(phoneNumber)) {
                customerView.getPhoneWrongInputLabel().setText("Du måste fylla i telefonnummer");
                validInput = false;
            } else {
                customerView.getPhoneWrongInputLabel().setText("");
            }

            String email = customerView.getEmailField().getText();
            if (customerService.isInputFieldEmpty(email)) {
                customerView.getEmailWrongInputLabel().setText("Du måste fylla i e-post");
                validInput = false;
            } else {
                customerView.getEmailWrongInputLabel().setText("");
            }

            if (!validInput) {
                return;
            }

            Customer customer = customerService.createCustomer(fullName, phoneNumber, email);

            if (customer != null) {
                refreshCustomerList();

                customerView.getFirstNameField().clear();
                customerView.getLastNameField().clear();
                customerView.getPhoneField().clear();
                customerView.getEmailField().clear();

                //Ej klar med denna
                customerView.getConfirmationLabel().setText(
                        "Kund " + firstName + " " + lastName + " har lagts till.");
            }

        });

        //customerView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
        customerView.getBackButton().setOnAction(actionEvent -> {
            app.showCustomerListView();
        });

        customerListView.getCreateCustomerButton().setOnAction(actionEvent -> {
            app.showView(customerView.getView());
        });

        customerListView.getBackButton().setOnAction(actionEvent -> app.showMainMenu());
    }

    private void refreshCustomerList() {
        customerListView.getCustomerListView().getItems().clear();

        for (Customer customer : customerService.getAllCustomers()) {
            customerListView.getCustomerListView().getItems().add(customer);
        }
    }

    public Parent getCustomerView() {
        return customerView.getView();
    }

    public Parent getCustomerListView() {
        return customerListView.getView();
    }

}
