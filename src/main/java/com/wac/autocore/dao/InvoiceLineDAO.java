package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.InvoiceLine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvoiceLineDAO {

    public List<InvoiceLine> findAll() {

        List<InvoiceLine> invoiceLines = new ArrayList<>();

        String sql = "SELECT id, invoice_id, service_item_description, " +
                "service_item_price, discount, final_price_after_discount " +
                "FROM invoice_line ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                InvoiceLine invoiceLine = new InvoiceLine(
                        resultSet.getInt("id"),
                        resultSet.getInt("invoice_id"),
                        resultSet.getString("service_item_description"),
                        resultSet.getDouble("service_item_price"),
                        resultSet.getDouble("discount"),
                        resultSet.getDouble("final_price_after_discount")
                );

                invoiceLines.add(invoiceLine);
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not fetch invoice lines.", e);
        }

        return invoiceLines;
    }

    public InvoiceLine save(InvoiceLine invoiceLine) {

        String sql = "INSERT INTO invoice_line " +
                "(invoice_id, service_item_description, service_item_price, " +
                "discount, final_price_after_discount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, invoiceLine.getInvoiceId());
            statement.setString(2, invoiceLine.getServiceItemDescription());
            statement.setDouble(3, invoiceLine.getServiceItemPrice());
            statement.setDouble(4, invoiceLine.getDiscount());
            statement.setDouble(5, invoiceLine.getFinalPricePerServiceAfterDiscount());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    invoiceLine.setInvoiceLineId(generatedKeys.getInt(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not save invoice line.", e);
        }

        return invoiceLine;
    }

}