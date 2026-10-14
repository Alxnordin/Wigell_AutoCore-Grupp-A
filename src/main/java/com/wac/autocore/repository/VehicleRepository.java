package com.wac.autocore.repository;

import com.wac.autocore.model.Vehicle;

import java.util.List;

public interface VehicleRepository {
    List<Vehicle> findAll();

    Vehicle save(Vehicle vehicle);
}
