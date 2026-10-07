package com.wac.autocore.dao;

import com.wac.autocore.data.DatabaseConnection;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


//Alexander
//läser och sparar servicepaket (tabellerna service_package & service_package_item)
public class ServicePackageDAO {

    //hämtar alla paket med dess tjänster
    public List<ServicePackage> findAll(){
        List<ServicePackage> servicePackages = new ArrayList<>();

        String sql = "SELECT id, name " +
                "FROM service_package " +
                "ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ServicePackage servicePackage = new ServicePackage(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );
                loadServiceItems(connection, servicePackage);
                servicePackages.add(servicePackage);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not fetch servicePackages.", e);
        }
        return servicePackages;
    }

    //läser in paketets tjänster med namn, pris & tid från service_item
    private void loadServiceItems(Connection connection, ServicePackage servicePackage)
        throws Exception {
        String sql = "SELECT s.id, s.name, s.description, s.price, s.estimated_minutes " +
                "FROM service_package_item p " +
                "JOIN service_item s ON s.id = p.service_item_id " +
                "WHERE p.service_package_id = ? " +
                "ORDER BY s.id ";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, servicePackage.getId());

            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    servicePackage.add(new ServiceItem(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("estimated_minutes")
                    ));
                }

            }

        }
    }

    //sparar ett nytt paket, tjänsterna läggs till efteråt med addServiceItem
    public ServicePackage save(ServicePackage servicePackage) {
        String sql = "INSERT INTO service_package (name) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS
        )){
            statement.setString(1, servicePackage.getName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servicePackage.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not save servicePackage.", e);
        }
        return servicePackage;
    }

    //lägger till en tjänst i paketet
    public void addServiceItem(int servicePackageId, int serviceItemId) {
        String sql = "INSERT INTO service_package_item" +
                "(service_package_id, service_item_id) " +
                "Values (?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, servicePackageId);
            statement.setInt(2, serviceItemId);

            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Could not add service item to servicePackage.", e);

        }
    }

    //tar bort en tjänst från ett paket
    public void removeServiceItem(int servicePackageId, int serviceItemId) {
        String sql = "DELETE FROM service_package_item " +
                "WHERE service_package_id = ? AND service_item_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, servicePackageId);
            statement.setInt(2, serviceItemId);

            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Could not remove service item from servicePackage.", e);
        }
    }
}
