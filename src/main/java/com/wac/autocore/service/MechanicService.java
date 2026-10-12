package com.wac.autocore.service;

import com.wac.autocore.dao.MechanicDAO;
import com.wac.autocore.model.Mechanic;

import java.util.List;

public class MechanicService {

    private final MechanicDAO mechanicDAO = new MechanicDAO();

    public List<Mechanic> getMechanics() {
        return mechanicDAO.findAll();
    }

    public Mechanic findMechanic(int id) {
        for (Mechanic mechanic : mechanicDAO.findAll()) {
            if (mechanic.getId() == id) {
                return mechanic;
            }
        }

        return null;
    }

    public void updateAvailable(Mechanic mechanic) {
        mechanicDAO.updateAvailable(mechanic);
    }


}
