package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();

        String sql = "Select id, vehicle_id, date, description, status " +
                "FROM booking " +
                "ORDER BY id";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Booking booking = new Booking(
                        resultSet.getInt("id"),
                        resultSet.getInt("vehicle_id"),
                        resultSet.getDate("date").toLocalDate(),
                        resultSet.getString("description")
                );
                booking.setStatus(resultSet.getString("status"));
                bookings.add(booking);
            }
        }catch (Exception e) {
            throw new RuntimeException("Could not fetch bookings.", e);
        }
        return bookings;
    }
    public Booking save (Booking booking) {
        String sql = "INSERT INTO booking " +
                "(vehicle_id, date, description, status) " +
                "VALUES (?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            statement.setInt(1, booking.getVehicleId());
            statement.setDate(2, java.sql.Date.valueOf(booking.getDate()));
            statement.setString(3, booking.getDescription());
            statement.setString(4, booking.getStatus());

            statement.executeUpdate();

            try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not save booking.", e);
        }
            return booking;
    }
    public void updateStatus(Booking booking){
        String sql = "UPDATE booking SET status = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, booking.getStatus());
            statement.setInt(2, booking.getId());

            statement.executeUpdate();
        } catch(Exception e) {
            throw new RuntimeException("Could not update booking", e);
        }
    }
}
