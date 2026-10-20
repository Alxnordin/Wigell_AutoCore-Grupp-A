package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.repository.CustomerRepository;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.validation.Validator;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomer(int id) {

        System.out.println("Nu är jag i customerservice");

        for (Customer customer : customerRepository.findAll()) {
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

        //Alexander
        //samma e-post får inte användas av två kunder (verksamhetsregel -> BusinessRuleException)
        if (emailAlreadyExists(email)) {
            throw new BusinessRuleException("customerEmailExistsError", email.trim());
        }

        String fullName = firstName.trim() + " " + lastName.trim();
        Customer customer = new Customer(0, fullName, phone.trim(), email.trim());

        customerRepository.save(customer);

        return customer;

    }

    private boolean emailAlreadyExists(String email) {

        for (Customer customer : customerRepository.findAll()) {
            //email.trim() först, så att en kund utan e-post (null) inte kan krascha jämförelsen
            if (email.trim().equalsIgnoreCase(customer.getEmail())) {
                return true;
            }
        }

        return false;
    }



}