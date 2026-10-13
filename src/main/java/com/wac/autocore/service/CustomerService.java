package com.wac.autocore.service;

import com.wac.autocore.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();
    Customer findCustomer(int id);
    Customer createCustomer(String name, String phone, String email);
    boolean isInputFieldEmpty(String input);


}
