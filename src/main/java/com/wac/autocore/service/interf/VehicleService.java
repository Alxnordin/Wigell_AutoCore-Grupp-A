package com.wac.autocore.service.interf;

import com.wac.autocore.model.Vehicle;

import java.util.List;

public interface VehicleService {

    List<Vehicle> getVehicles();

    Vehicle findVehicle(int id);

    Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId);


}
