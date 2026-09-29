package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();

        String sql = "SELECT id, work_order_id, invoice_date, amount, discount,total_amount, paid " +
                "FROM invoice ORDER BY id;";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

    while (resultSet.next()) {
        Invoice invoice = new Invoice(
                resultSet.getInt("id"),
                resultSet.getInt("work_order_id"),
                resultSet.getDate("invoice_date").toLocalDate(),
                resultSet.getDouble("amount")
        );
        invoice.setDiscount(resultSet.getDouble("discount"));
        invoice.setPaid(resultSet.getBoolean("paid"));

        invoices.add(invoice);
            }
        } catch (Exception e){
            throw new RuntimeException("Could not fetch invoices.", e);
        }
    return invoices;
    }
    public Invoice save(Invoice invoice) {
        String sql = "INSERT INTO invoice " +
                "(work_order_id, invoice_date, amount, discount, total_amount, paid) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, invoice.getWorkOrderId());
            statement.setDate(2, java.sql.Date.valueOf(invoice.getInvoiceDate()));
            statement.setDouble(3, invoice.getAmount());
            statement.setDouble(4, invoice.getTotalAmount());
            statement.setDouble(5, invoice.getDiscount());
            statement.setBoolean(6, invoice.isPaid());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoice.setId(generatedKeys.getInt(1));
                }
            }

        }catch (Exception e){
        throw new RuntimeException("Could not save invoice.", e);
        }
        return invoice;
    }
    public void updatePaid(Invoice invoice){
        String sql = "UPDATE invoice SET paid = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setBoolean(1,invoice.isPaid());
            statement.setInt(2, invoice.getId());

            statement.executeUpdate();
        } catch(Exception e) {
            throw new RuntimeException("Could not update invoice.", e);
        }
    }
    }