package com.wac.autocore.service.interf;

import com.wac.autocore.model.Vehicle;

import java.util.List;

public interface VehicleService {

    List<Vehicle> getVehicles();

    Vehicle findVehicle(int id);

    //Alexander
    //årsmodell tas emot som text och kund kan saknas (null), så att servicen
    //validerar allt och kastar ValidationException/NotFoundException vid fel
    Vehicle createVehicle(String registrationNumber,
                          String brand,
                          String model,
                          String yearText,
                          Integer customerId);


}