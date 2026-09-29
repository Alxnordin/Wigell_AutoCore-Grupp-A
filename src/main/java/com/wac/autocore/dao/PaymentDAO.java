package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "Select id, invoice_id, amount, payment_date, successful " +
                    "FROM payment " +
                    "ORDER BY id";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()){
                Payment payment = new Payment(
                        resultSet.getInt("id"),
                        resultSet.getInt("invoice_id"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("payment_type")

                );
                payment.setPaymentDate(resultSet.getTimestamp("payment_date").toLocalDateTime());

                payment.setSuccessful(resultSet.getBoolean("successful"));

                payments.add(payment);
            }
        } catch (Exception e){
            throw new RuntimeException("Could not fetch payments.", e);
        }
        return payments;
    }
    public Payment save(Payment payment){
        String sql = "INSERT INTO payment " +
                "(invoice_id, amount, payment_type, payment_date, successful)" +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            statement.setInt(1, payment.getInvoiceId());
            statement.setDouble(2, payment.getAmount());
            statement.setString(3, payment.getPaymentType());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(payment.getPaymentDate())
            );
            statement.setBoolean(5, payment.isSuccessful());
            statement.executeUpdate();

            try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    payment.setId(generatedKeys.getInt(1));
                }
            }
        } catch(Exception e) {
            throw new RuntimeException("Could not save payment.", e);
        }
        return payment;
    }

}
