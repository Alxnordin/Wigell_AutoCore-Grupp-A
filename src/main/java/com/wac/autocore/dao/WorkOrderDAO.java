package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.WorkOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDAO {
    public List<WorkOrder> findAll() {
        List<WorkOrder> workOrders = new ArrayList<WorkOrder>();

        String sql = "SELECT id, booking_id, mechanic_id, status " +
                "FROM work_order " +
                "ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                WorkOrder workOrder = new WorkOrder(
                        resultSet.getInt("id"),
                        resultSet.getInt("booking_id"),
                        resultSet.getInt("mechanic_id")
                );
                workOrder.setStatus(resultSet.getString("status"));
                loadServiceItems(connection, workOrder);
                workOrders.add(workOrder);
            }
        } catch (Exception e){
            throw new RuntimeException("Could not fetch work orders.", e);
        }
        return workOrders;
    }
    private void loadServiceItems(Connection connection, WorkOrder workOrder) throws Exception{
        String sql = "SELECT service_item_id " +
                "FROM work_order_service_item " +
                "WHERE work_order_id = ? " +
                "ORDER BY service_item_id";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, workOrder.getId());
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    workOrder.addServiceItem(
                            resultSet.getInt("service_item_id")
                    );
                }
            }
        }
    }
    public WorkOrder save(WorkOrder workOrder){
        String sql = "INSERT INTO work_order " +
                "(booking_id, mechanic_id, status) " +
                "VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            statement.setInt(1, workOrder.getBookingId());
            statement.setInt(2, workOrder.getMechanicId());
            statement.setString(3, workOrder.getStatus());

            statement.executeUpdate();

            try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    workOrder.setId(generatedKeys.getInt(1));
                }
            }
            saveServiceItems(connection,workOrder);

        } catch(Exception e) {
            throw new RuntimeException("Could not save workOrder.", e);
        }
        return workOrder;
        }
    private void saveServiceItems(Connection connection, WorkOrder workOrder) throws Exception{
        String sql = "INSERT INTO work_order_service_item " +
                "(work_order_id, service_item_id) " +
                "VALUES (?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            for(Integer serviceItemId : workOrder.getServiceItemIds()){
                statement.setInt(1, workOrder.getId());
                statement.setInt(2,serviceItemId);

                statement.executeUpdate();
            }
        }
    }
    public void updateStatus (WorkOrder workOrder){
        String sql = "UPDATE work_order SET status = ? where id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

                 statement.setString(1, workOrder.getStatus());
                statement.setInt(2, workOrder.getId());

                statement.executeUpdate();

        } catch(Exception e){
                 throw new RuntimeException("Could not update workOrder.", e);
        }
    }
}
