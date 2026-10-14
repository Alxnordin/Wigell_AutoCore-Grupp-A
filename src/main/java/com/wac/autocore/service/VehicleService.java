package com.wac.autocore.service;

import com.wac.autocore.dao.VehicleDAO;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.repository.VehicleRepository;

import java.util.List;

public class VehicleService {

    //private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final VehicleRepository vehicleRepository;
    private final CustomerService customerService;

    public VehicleService(CustomerService customerService) {
        this(customerService, new VehicleDAO());
    }
    public VehicleService(CustomerService customerService, VehicleRepository vehicleRepository) {
        this.customerService = customerService;
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle findVehicle(int id) {
        for (Vehicle vehicle : vehicleRepository.findAll()) {
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
        vehicleRepository.save(vehicle);

        System.out.println("Vehicle created successfully.");
        System.out.println(vehicle);

        return vehicle;
    }


}
