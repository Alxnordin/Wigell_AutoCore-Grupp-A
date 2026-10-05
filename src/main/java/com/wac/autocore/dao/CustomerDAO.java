package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public List<Customer> findAll(){
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT id, name, phone, email, vip " +
                "FROM customer " +
                "ORDER BY id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next()){
                Customer customer = new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email")

                );
                customer.setVip(resultSet.getBoolean("vip"));
                customers.add(customer);
            }
        }catch(Exception e){
            throw new RuntimeException("Could not fetch customers.", e);
        }
        return customers;
    }

    public Customer save (Customer customer){
        //Spara kunden
        String sql =
                "INSERT INTO customer (name, phone, email,vip) " +
                "VALUES (?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getPhone());
            statement.setString(3, customer.getEmail());
            statement.setBoolean(4, customer.isVip());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    customer.setId(generatedKeys.getInt(1));
                }
            }

        }catch (Exception e){
            throw new RuntimeException("Could not save customer.", e);
        }
        return customer;
    }


}
