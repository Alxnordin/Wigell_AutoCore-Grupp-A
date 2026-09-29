package com.wac.autocore.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/autocore";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static void initializeDatabase(){
        String sql =  "CREATE TABLE IF NOT EXISTS customer (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "phone VARCHAR(50), " +
                "email VARCHAR(255), " +
                "vip BOOLEAN NOT NULL DEFAULT FALSE" +
                ")";
        String vehicleSql = "CREATE TABLE IF NOT EXISTS vehicle(" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "registration_number VARCHAR(50) NOT NULL," +
                "brand VARCHAR(100) NOT NULL, " +
                "model VARCHAR(100) NOT NULL, " +
                "year INT NOT NULL," +
                "customer_id INT NOT NULL" +
                ")";
        String bookingSql = "CREATE TABLE IF NOT EXISTS booking (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "vehicle_id INT NOT NULL, " +
                "date DATE NOT NULL, " +
                "description VARCHAR(500), " +
                "status VARCHAR(50) NOT NULL" +
                ")";
        String serviceItemSql = "CREATE TABLE IF NOT EXISTS service_item (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description VARCHAR(500), " +
                "price DOUBLE NOT NULL, " +
                "estimated_minutes INT NOT NULL" +
                ")";
        String mechanicSql = "CREATE TABLE IF NOT EXISTS mechanic (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "phone VARCHAR(50), " +
                "specialization VARCHAR(255), " +
                "available BOOLEAN NOT NULL DEFAULT TRUE" +
                ")";

        String invoiceSql = "CREATE TABLE IF NOT EXISTS invoice (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "work_order_Id INT NOT NULL, " +
                "invoice_date DATE NOT NULL, " +
                "amount DOUBLE NOT NULL, " +
                "discount DOUBLE NOT NULL DEFAULT 0, " +
                "total_amount DOUBLE NOT NULL, " +
                "paid BOOLEAN NOT NULL DEFAULT FALSE" +
                ")";

        String paymentSql = "CREATE TABLE IF NOT EXISTS payment (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "invoice_id INT NOT NULL, " +
                "amount DOUBLE NOT NULL, " +
                "payment_type VARCHAR(50) NOT NULL, " +
                "payment_date DATETIME NOT NULL, " +
                "successful BOOLEAN NOT NULL DEFAULT FALSE" +
                ")";


        String workOrderSql = "CREATE TABLE IF NOT EXISTS work_order (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "booking_id INT NOT NULL, " +
                "mechanic_id INT NOT NULL, " +
                "status VARCHAR(50) NOT NULL" +
                ")";
        String workOrderServiceItemSql = "CREATE TABLE IF NOT EXISTS work_order_service_item (" +
                "work_order_id INT NOT NULL, " +
                "service_item_id INT NOT NULL, " +
                "PRIMARY KEY (work_order_id, service_item_id)" +
                ")";
        try (Connection connection = getConnection();
        Statement statement = connection.createStatement()){
            statement.execute(sql);
            statement.execute(vehicleSql);
            statement.execute(bookingSql);
            statement.execute(serviceItemSql);
            statement.execute(mechanicSql);
            statement.execute(invoiceSql);
            statement.execute(paymentSql);
            statement.execute(workOrderSql);
            statement.execute(workOrderServiceItemSql);
        }catch(SQLException e){
            throw new RuntimeException("Could not initialize database.", e);
        }

    }
}
