package com.wac.autocore.repository;

import com.wac.autocore.model.Customer;
import java.util.List;


public interface CustomerRepository {
    List<Customer> findAll();

    Customer save(Customer customer);
}
