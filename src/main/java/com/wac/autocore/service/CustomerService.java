package com.wac.autocore.service;

import com.wac.autocore.model.Customer;
import com.wac.autocore.validation.Validator;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();
    Customer findCustomer(int id);
    Customer createCustomer(String firstName, String lastName, String phone, String email);
    boolean isInputFieldEmpty(String input);







}
