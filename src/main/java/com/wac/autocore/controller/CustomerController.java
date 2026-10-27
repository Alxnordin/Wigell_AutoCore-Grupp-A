package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import com.wac.autocore.view.customer.CustomerListView;
import com.wac.autocore.view.customer.CustomerView;
import com.wac.autocore.view.ErrorHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerController {

    private final CustomerService customerService;
    private final AutoCoreApplication app;
    private final CustomerView customerView;
    private final CustomerListView customerListView;

    LanguageManager languageManager = LanguageManager.getInstance();

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

            String vipValue = customerView.getVipComboBox().getValue();
            if (vipValue == null) {
                throw new BusinessRuleException("customerVipRequiredError");
            }
            boolean vip = languageManager.getString("vipYes").equals(vipValue);

            try {

                Customer customer = customerService.createCustomer(
                        customerView.getFirstNameField().getText(),
                        customerView.getLastNameField().getText(),
                        customerView.getPhoneField().getText(),
                        customerView.getEmailField().getText(),
                        vip);

                refreshCustomerList();

                customerView.getFirstNameField().clear();
                customerView.getLastNameField().clear();
                customerView.getPhoneField().clear();
                customerView.getEmailField().clear();

                showConfirmation(customer);
                app.showCustomerListView();

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

    private void showConfirmation(Customer customer) {
        String heading = languageManager.getString("confirmationNewCustomerHeading");
        String subHeading = languageManager.getString("confirmationNewCustomerSubHeading");

        Map<String, String> confirmationDetails = new LinkedHashMap<>();
        confirmationDetails.put(languageManager.getString("name"), customer.getName());
        confirmationDetails.put(languageManager.getString("emailLabel"), customer.getEmail());
        confirmationDetails.put(languageManager.getString("phoneLabel"),  customer.getPhone());
        confirmationDetails.put("VIP", customer.isVip() ? "Yes" : "No");

        Alert alert = UIComponents.createConfirmationAlert(heading, subHeading, confirmationDetails);
        alert.showAndWait();
    }

    public Parent getCustomerListView() {
        return customerListView.getView();
    }

}
