package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.CustomerDAO;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.CustomerView;
import javafx.scene.Parent;

//Kopplar CustomerView till GarageSystem — hanterar visning och skapande av kunder
public class CustomerController {
    private final GarageSystem garageSystem;
    private final AutoCoreApplication app;
    private final CustomerView customerView;
    //FREDRIK - lagt till
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public CustomerController(GarageSystem garageSystem, AutoCoreApplication app, CustomerView customerView) {
        this.garageSystem =garageSystem;
        this.app = app;
        this.customerView = customerView;
        wireEvents();
        refreshCustomerList();

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            refreshCustomerList();
        });
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


    for (Customer customer : customerDAO.findAll()){
     
        String vipYesOrNo;
        if (customer.isVip()) {
            vipYesOrNo = languageManager.getString("vipYes");
        }
        else {
            vipYesOrNo = languageManager.getString("vipNo");
        }

        String customerInfo = customer.getId() + " | "
                + languageManager.getString("nameInTable")
                + ": " + customer.getName() + " | "
                + languageManager.getString("phoneInTable")
                + ": " + customer.getPhone() + " | "
                + languageManager.getString("emailInTable")
                + ": " + customer.getEmail() + " | "
                + "VIP: " + vipYesOrNo;

        customerView.getCustomerListView().getItems().add(customerInfo);

    }
}

    public Parent getView() {
        return customerView.getView();
    }
}
