package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.repository.CustomerRepository;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.validation.Validator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomer(int id) {

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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public Customer createCustomer(String firstName, String lastName, String phone,
                                   String email, boolean vip) {

        Validator.requireText(firstName, "customerFirstNameRequiredError");
        Validator.requireText(lastName, "customerLastNameRequiredError");
        Validator.requireText(phone, "customerPhoneRequiredError");
        Validator.requireText(email, "customerEmailRequiredError");

        phone = String.valueOf(Validator.requireInteger(phone, "phoneNumberMustBeIntegers"));

        //Alexander
        //samma e-post får inte användas av två kunder (verksamhetsregel -> BusinessRuleException)
        if (emailAlreadyExists(email)) {
            throw new BusinessRuleException("customerEmailExistsError", email.trim());
        }

        if (!email.contains("@")) {
            throw new BusinessRuleException("mustContain@", email.trim());
        }

        String fullName = firstName.trim() + " " + lastName.trim();
        Customer customer = new Customer(0, fullName, phone, email.trim(), vip);

        customerRepository.save(customer);
        //bara id loggas, inte namn eller e-post (personuppgifter)
        LOG.info("createCustomer: kund {} skapad", customer.getId());

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