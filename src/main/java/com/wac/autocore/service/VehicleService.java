package com.wac.autocore.service;

import com.wac.autocore.dao.VehicleDAO;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;

import java.util.List;

public class VehicleService {

    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private final CustomerService customerService;

    public VehicleService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public List<Vehicle> getVehicles() {
        return vehicleDAO.findAll();
    }

    public Vehicle findVehicle(int id) {
        for (Vehicle vehicle : vehicleDAO.findAll()) {
            if (vehicle.getId() == id) {
                return vehicle;
            }
        }

        return null;
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {

        Customer customer = customerService.findCustomer(customerId);

        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
        }
        Vehicle vehicle = new Vehicle(0, registrationNumber, brand, model, year, customerId);
        vehicleDAO.save(vehicle);

        System.out.println("Vehicle created successfully.");
        System.out.println(vehicle);

        return vehicle;
    }


}
