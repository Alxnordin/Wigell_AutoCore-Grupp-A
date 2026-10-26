package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.repository.VehicleRepository;
import com.wac.autocore.service.interf.CustomerService;
import com.wac.autocore.service.interf.VehicleService;
import com.wac.autocore.validation.Validator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerService customerService;

    //Alexander
    //första bilen i världen kom 1886, och nästa års modeller kan redan finnas
    private static final int FIRST_MODEL_YEAR = 1886;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, CustomerService customerService) {
        this.vehicleRepository = vehicleRepository;
        this.customerService = customerService;
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

    //Alexander
    //servicen avgör nu själv vad som är giltigt, inte controllern.
    //årsmodellen tas emot som text och tolkas här, så att samma regel gäller oavsett vem som anropar.
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
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

//        if (vehicleRepository.existsByRegistrationNumber(registrationNumber)) {
//            throw new BusinessRuleException("Registration number already exists");
//        }

        int year = Validator.requireInteger(yearText, "invalidYearWarning");
        Validator.requireRange(year, FIRST_MODEL_YEAR, Year.now().getValue() + 1, "invalidYearRangeError");

        Customer customer = customerService.findCustomer(customerId);
        if (customer == null) {
            throw new NotFoundException("customerNotFoundError", String.valueOf(customerId));
        }

        Vehicle vehicle = new Vehicle(0, registrationNumber.trim(), brand.trim(), model.trim(), year, customerId);
        vehicleRepository.save(vehicle);

        return vehicle;
    }
}