package com.wac.autocore.service.impl;

import com.wac.autocore.dao.VehicleDAO;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.service.interf.VehicleService;
import com.wac.autocore.validation.Validator;

import java.time.Year;
import java.util.List;

public class VehicleServiceImpl implements VehicleService {

    private final VehicleDAO vehicleDAO;
    private final CustomerService customerService;

    //Alexander
    //första bilen i världen kom 1886, och nästa års modeller kan redan finnas
    private static final int FIRST_MODEL_YEAR = 1886;


    public VehicleServiceImpl(VehicleDAO vehicleDAO, CustomerService customerService) {
        this.vehicleDAO = vehicleDAO;
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

    //Alexander
    //servicen avgör nu själv vad som är giltigt, inte controllern.
    //årsmodellen tas emot som text och tolkas här, så att samma regel gäller oavsett vem som anropar.
    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 String yearText,
                                 Integer customerId) {

        if (customerId == null) {
            throw new ValidationException("missingCustomerWarning");
        }

        Validator.requireText(registrationNumber, "missingVehicleDetailsWarning");
        Validator.requireText(brand, "missingVehicleDetailsWarning");
        Validator.requireText(model, "missingVehicleDetailsWarning");

        int year = Validator.requireInteger(yearText, "invalidYearWarning");
        Validator.requireRange(year, FIRST_MODEL_YEAR, Year.now().getValue() + 1, "invalidYearRangeError");

        Customer customer = customerService.findCustomer(customerId);
        if (customer == null) {
            throw new NotFoundException("customerNotFoundError", String.valueOf(customerId));
        }

        Vehicle vehicle = new Vehicle(0, registrationNumber.trim(), brand.trim(), model.trim(), year, customerId);
        vehicleDAO.save(vehicle);

        return vehicle;
    }
}