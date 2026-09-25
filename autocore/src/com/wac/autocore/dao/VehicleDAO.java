package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    public List<Vehicle> findAll(){
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, registration_number, brand, model,year,customer_id " +
                "FROM vehicle " +
                "ORDER BY id";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {

            while(resultSet.next()){
                Vehicle vehicle = new Vehicle(
                        resultSet.getInt("id"),
                        resultSet.getString("registration_number"),
                        resultSet.getString("brand"),
                        resultSet.getString("model"),
                        resultSet.getInt("year"),
                        resultSet.getInt("customer_id")
                );
                vehicles.add(vehicle);
            }
        }catch(Exception e) {
            throw new RuntimeException("Could not fetch vehicles.", e);
        }
        return vehicles;
    }
    public Vehicle save(Vehicle vehicle){
        String sql = "INSERT INTO vehicle " +
                "(registration_number, brand, model, year,customer_id)" +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            statement.setString(1, vehicle.getRegistrationNumber());
            statement.setString(2, vehicle.getBrand());
            statement.setString(3, vehicle.getModel());
            statement.setInt(4, vehicle.getYear());
            statement.setInt(5, vehicle.getCustomerId());

            statement.executeUpdate();

            try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    vehicle.setId(generatedKeys.getInt(1));
                }
            }

        } catch(Exception e) {
            throw new RuntimeException("Could not save vehicle.", e);
        }
        return vehicle;
    }

}
