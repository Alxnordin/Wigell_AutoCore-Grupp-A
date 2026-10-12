package com.wac.autocore.service;

import com.wac.autocore.dao.CustomerDAO;
import com.wac.autocore.model.Customer;

import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    public Customer findCustomer(int id) {

        for (Customer customer : customerDAO.findAll()){
            if (customer.getId() == id){
                return customer;
            }
        }
        return null;
    }

    public Customer createCustomer(String name, String phone, String email) {

        Customer customer = new Customer(0, name, phone, email);
        customerDAO.save(customer);

        System.out.println("Customer created successfully.");
        System.out.println(customer);

        return customer;
    }

    public boolean isInputFieldEmpty(String input)  {
        return input == null || input.trim().isEmpty();
    }

}
