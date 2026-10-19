package com.wac.autocore.service.interf;

import com.wac.autocore.model.Mechanic;

import java.util.List;

public interface MechanicService {

    List<Mechanic> getMechanics();

    Mechanic findMechanic(int id);

    void updateAvailable(Mechanic mechanic);
}
