package com.wac.autocore.repository;

import com.wac.autocore.model.Mechanic;

import java.util.List;

public interface MechanicRepository {
    List<Mechanic> findAll();

    Mechanic save(Mechanic mechanic);

    void updateAvailable(Mechanic mechanic);
}
