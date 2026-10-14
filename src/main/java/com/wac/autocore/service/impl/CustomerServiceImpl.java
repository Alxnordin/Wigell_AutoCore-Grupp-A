package com.wac.autocore.service.impl;

import com.wac.autocore.dao.CustomerDAO;
import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.validation.Validator;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    public Customer findCustomer(int id) {

        for (Customer customer : customerDAO.findAll()) {
            if (customer.getId() == id) {
                return customer;
            }
        }
        return null;
    }

    //Alexander
    //servicen avgör nu själv vad som är giltigt, inte controllern.
    //saknas en uppgift kastas ValidationException med en språknyckel som säger vad som saknas.
    //samma regler gäller oavsett om kunden skapas från appen, ett test eller ett annat system.
    public Customer createCustomer(String firstName, String lastName, String phone, String email) {

        Validator.requireText(firstName, "customerFirstNameRequiredError");
        Validator.requireText(lastName, "customerLastNameRequiredError");
        Validator.requireText(phone, "customerPhoneRequiredError");
        Validator.requireText(email, "customerEmailRequiredError");

        //OBS! Måste fixa vad som ska skrivas ut, ska gå att översätta.:wq

        if (emailAlreadyExists(email)) {
            throw new BusinessRuleException("E-post finns redan");
        }
        String fullName = firstName.trim() + " " + lastName.trim();
        Customer customer = new Customer(0, fullName, phone.trim(), email.trim());

        customerDAO.save(customer);

        return customer;

    }

    private boolean emailAlreadyExists(String email) {

        for (Customer customer : customerDAO.findAll()) {
            if (customer.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }

        return false;
    }

    //Kan tas bort va?
    public boolean isInputFieldEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }


}
