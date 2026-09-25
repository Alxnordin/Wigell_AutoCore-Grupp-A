package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.Mechanic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MechanicDAO {
    public List<Mechanic> findAll() {
        List<Mechanic> mechanics = new ArrayList<>();

        String sql = "SELECT id, name, phone, specialization, available " +
                "FROM mechanic " +
                "ORDER BY id";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
            Mechanic mechanic = new Mechanic(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("phone"),
                    resultSet.getString("specialization")
            );
            mechanic.setAvailable(resultSet.getBoolean("available"));

            mechanics.add(mechanic);
            }
        }catch(Exception e) {
            throw new RuntimeException("Could not fetch mechanics.", e);
        }
        return mechanics;
    }

    public Mechanic save(Mechanic mechanic){
        String sql =
                "INSERT INTO mechanic " +
                 "(name, phone, specialization, available) " +
                 "VALUES (?, ?, ?, ?,)";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, mechanic.getName());
            statement.setString(2, mechanic.getPhone());
            statement.setString(3, mechanic.getSpecialization());
            statement.setBoolean(4, mechanic.isAvailable());

            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    mechanic.setId(generatedKeys.getInt(1));
                }
            }
        }catch (Exception e) {
            throw new RuntimeException("Could not save mechanic.", e);
        }
        return mechanic;
    }
    public void updateAvailable(Mechanic mechanic){
        String sql = "UPDATE mechanic SET available = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, mechanic.isAvailable());
            statement.setInt(2, mechanic.getId());

            statement.executeUpdate();
        } catch(Exception e) {
            throw new RuntimeException("Could not update mechanic", e);
        }
    }
}
