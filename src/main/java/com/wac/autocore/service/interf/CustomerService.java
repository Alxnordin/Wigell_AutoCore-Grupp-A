package com.wac.autocore.service.interf;

import com.wac.autocore.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();
    Customer findCustomer(int id);
    Customer createCustomer(String firstName, String lastName, String phone,
                            String email, boolean vip);








}
