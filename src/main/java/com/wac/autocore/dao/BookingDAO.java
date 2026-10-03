package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.Booking;

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
                loadServiceItems(connection, booking);
                bookings.add(booking);
            }
        }catch (Exception e) {
            throw new RuntimeException("Could not fetch bookings.", e);
        }
        return bookings;
    }

    //Alexander
    //läser in tjänst från en befintlig bokning i databasen
    private void loadServiceItems(Connection connection, Booking booking) throws Exception {
        String sql = "SELECT service_item_id " +
                "FROM booking_service_item " +
                "WHERE booking_id = ? " +
                "ORDER BY service_item_id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, booking.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    booking.addServiceItem(resultSet.getInt("service_item_id"));
                }
            }
        }
    }

    //sparar bokningen och alla dess tjänster
    public Booking save (Booking booking) {
        String sql = "INSERT INTO booking " +
                "(vehicle_id, date, description, status) " +
                "VALUES (?, ?, ?, ?)";


        try (Connection connection = DatabaseConnection.getConnection()) {   // Yttre: bara uppkopplingen
            connection.setAutoCommit(false);                                  // Starta transaktion

            try {                                                             // Inre: själva arbetet
                try (PreparedStatement statement =
                             connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, booking.getVehicleId());
                    statement.setDate(2, java.sql.Date.valueOf(booking.getDate()));
                    statement.setString(3, booking.getDescription());
                    statement.setString(4, booking.getStatus());

                    statement.executeUpdate();

                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            booking.setId(generatedKeys.getInt(1));
                        }
                    }
                }

                for (Integer serviceItemId : booking.getServiceItemIds()) {
                    insertServiceItem(connection, booking.getId(), serviceItemId);
                }

                connection.commit();      //Om allt gick bra → spara allt

            } catch (Exception e) {
                connection.rollback();    //connection är fortfarande öppen här → ångra allt
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not save booking.", e);
        }
        return booking;
    }

    //Alexander
    //lägger till en tjänst i en bokning (används av save och senare addServiceItem)
    private void insertServiceItem(Connection connection, int bookingId, int serviceItemId) throws Exception {
        String sql = "INSERT INTO booking_service_item " +
                "(booking_id, service_item_id) " +
                "VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, serviceItemId);

            statement.executeUpdate();
        }
    }

    //Alexander
    //lägger till en tjänst i en befintlig bokning, publik för anrop av GarageSystem
    public void addServiceItem(int bookingId, int serviceItemId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            insertServiceItem(connection, bookingId, serviceItemId);   // återanvänder hjälpmetoden från 3b
        } catch (Exception e) {
            throw new RuntimeException("Could not add service item to booking.", e);
        }
    }

    //Alexander
    //tar bort en tjänst från en befintlig bokning, publik för anrop av GarageSystem
    public void removeServiceItem(int bookingId, int serviceItemId) {
        String sql = "DELETE FROM booking_service_item " +
                "WHERE booking_id = ? AND service_item_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, serviceItemId);

            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Could not remove service item from booking.", e);
        }
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

