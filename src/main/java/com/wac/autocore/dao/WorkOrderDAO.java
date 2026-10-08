package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.WorkOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
//Har lagt till price så att detta kan lagras i en workOrder. på sätt sparas
//priset på en service ifall priset på en service skulle uppdateras
public class WorkOrderDAO {

    public List<WorkOrder> findAll() {
        List<WorkOrder> workOrders = new ArrayList<WorkOrder>();

        String sql = "SELECT id, booking_id, customer_id, vehicle_id, description, mechanic_id, status, " +
                "is_complaint, original_work_order_id " +
                "FROM work_order " +
                "ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {

                Integer bookingId = resultSet.getObject("booking_id", Integer.class);
                int customerId = resultSet.getInt("customer_id");
                int vehicleId = resultSet.getInt("vehicle_id");
                String description = resultSet.getString("description");
                int mechanicId = resultSet.getInt("mechanic_id");
                WorkOrder workOrder = new WorkOrder(
                        resultSet.getInt("id"),
                        bookingId,
                        mechanicId
                );
                workOrder.setCustomerId(customerId);
                workOrder.setVehicleId(vehicleId);
                workOrder.setDescription(description);
                workOrder.setStatus(resultSet.getString("status"));
                workOrder.setComplaint(resultSet.getBoolean("is_complaint"));
                int originalId = resultSet.getInt("original_work_order_id");

                if (!resultSet.wasNull()){
                    workOrder.setOriginalWorkOrderId(originalId);
                }

                loadServiceItems(connection, workOrder);
                workOrders.add(workOrder);
            }
        } catch (Exception e){
            throw new RuntimeException("Could not fetch work orders.", e);
        }
        return workOrders;
    }

    public WorkOrder findById(int workOrderId){
        String sql = "SELECT id, booking_id, customer_id, vehicle_id, description, mechanic_id, status, " +
                "is_complaint, original_work_order_id " +
                "FROM work_order " +
                "WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, workOrderId);

            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    Integer bookingId = resultSet.getObject("booking_id", Integer.class);

                    WorkOrder workOrder = new WorkOrder(
                            resultSet.getInt("id"),
                            bookingId,
                            resultSet.getInt("mechanic_id")
                    );
                    workOrder.setCustomerId(resultSet.getInt("customer_id"));
                    workOrder.setVehicleId(resultSet.getInt("vehicle_id"));
                    workOrder.setDescription(resultSet.getString("description"));
                    workOrder.setStatus(resultSet.getString("status"));
                    workOrder.setComplaint(resultSet.getBoolean("is_complaint"));
                    int originalId= resultSet.getInt("original_work_order_id");
                    if (!resultSet.wasNull()){
                        workOrder.setOriginalWorkOrderId(originalId);
                    }
                    loadServiceItems(connection, workOrder);
                    return workOrder;
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Could not find workOrder.", e);
        }
        return null;
    }

    public void update(WorkOrder workOrder) {
        String sql = "UPDATE work_order SET " +
                "booking_id = ?, " +
                "customer_id = ?, " +
                "vehicle_id = ?, " +
                "description = ?, " +
                "mechanic_id = ?, " +
                "status = ?, " +
                "is_complaint = ?, " +
                "original_work_order_id = ? " +
                "WHERE id = ?";

        try  (Connection connection = DatabaseConnection.getConnection();
              PreparedStatement statement = connection.prepareStatement(sql)) {
            if (workOrder.getBookingId() != null){
                statement.setInt(1,workOrder.getBookingId());
            }else {
                statement.setNull(1, Types.INTEGER);
            }

            statement.setInt(2, workOrder.getCustomerId());
            statement.setInt(3, workOrder.getVehicleId());
            statement.setString(4, workOrder.getDescription());
            statement.setInt(5, workOrder.getMechanicId());
            statement.setString(6, workOrder.getStatus());
            statement.setBoolean(7, workOrder.isComplaint());

            if (workOrder.getOriginalWorkOrderId() != null){
                statement.setInt(8,workOrder.getOriginalWorkOrderId());
            }else {
                statement.setNull(8, Types.INTEGER);
            }
            statement.setInt(9, workOrder.getId());

            statement.executeUpdate();

            deleteServiceItems(connection,workOrder.getId());
            saveServiceItems(connection, workOrder);

        } catch (Exception e) {
            throw new RuntimeException("Could not update workOrder.", e);
        }
    }

    private void deleteServiceItems(Connection connection, int workOrderId) throws Exception{
        String sql = "DELETE FROM work_order_service_item " +
                "WHERE work_order_id = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, workOrderId);
            statement.executeUpdate();
        }
    }

    private void loadServiceItems(Connection connection, WorkOrder workOrder) throws Exception{
        String sql = "SELECT service_item_id, price " +
                "FROM work_order_service_item " +
                "WHERE work_order_id = ? " +
                "ORDER BY service_item_id";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, workOrder.getId());
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    workOrder.addServiceItem(
                            resultSet.getInt("service_item_id"),
                            resultSet.getDouble("price")
                    );
                }
            }
        }
    }
    public WorkOrder save(WorkOrder workOrder){
        String sql = "INSERT INTO work_order " +
                "(booking_id, customer_id,vehicle_id, description, mechanic_id, status, is_complaint, original_work_order_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            if (workOrder.getBookingId() != null) {
                statement.setInt(1, workOrder.getBookingId());
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }
            statement.setInt(2, workOrder.getCustomerId());
            statement.setInt(3, workOrder.getVehicleId());
            statement.setString(4, workOrder.getDescription());
            statement.setInt(5, workOrder.getMechanicId());
            statement.setString(6, workOrder.getStatus());
            statement.setBoolean(7, workOrder.isComplaint());



            if (workOrder.getOriginalWorkOrderId() != null) {
                statement.setInt(8, workOrder.getOriginalWorkOrderId());
            } else {
                statement.setNull(8, Types.INTEGER);
            }

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
                  "(work_order_id, service_item_id, price) " +
                "VALUES (?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            for(Integer serviceItemId : workOrder.getServiceItemIds()){
                statement.setInt(1, workOrder.getId());
                statement.setInt(2,serviceItemId);
                statement.setDouble(3, workOrder.getServiceItemPrices().get(serviceItemId));
                statement.executeUpdate();
            }
        }
    }
    public void removeServiceItem(int workOrderId, int serviceItemId){
        String sql = "DELETE FROM work_order_service_item " +
                "WHERE work_order_id = ? AND service_item_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, workOrderId);
            statement.setInt(2, serviceItemId);

            statement.executeUpdate();
        } catch(Exception e) {
            throw new RuntimeException("Could not remove serviceItem from workOrder. ", e);
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
