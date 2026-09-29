package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.ServiceItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServiceItemDAO {
    public List<ServiceItem> findAll(){
        List<ServiceItem> serviceItems = new ArrayList<>();
        String sql = "SELECT id, name, description, price, estimated_minutes " +
                "FROM service_item " +
                "ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next()){
                ServiceItem serviceItem = new ServiceItem(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("estimated_minutes")
                );
                serviceItems.add(serviceItem);
            }
    }catch(Exception e) {
        throw new RuntimeException("Could not fetch serviceItems.", e);
        }
        return serviceItems;
    }
    public ServiceItem save(ServiceItem serviceItem){
        String sql = "INSERT INTO service_item " +
                "(name, description, price, estimated_minutes) " +
                "VALUES (?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            statement.setString(1, serviceItem.getName());
            statement.setString(2, serviceItem.getDescription());
            statement.setDouble(3, serviceItem.getPrice());
            statement.setInt(4, serviceItem.getEstimatedMinutes());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    serviceItem.setId(generatedKeys.getInt(1));
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Could not save serviceItem.", e);
        }
        return serviceItem;
    }
}

