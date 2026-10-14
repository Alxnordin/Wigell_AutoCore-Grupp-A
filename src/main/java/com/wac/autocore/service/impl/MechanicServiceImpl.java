package com.wac.autocore.service.impl;

import com.wac.autocore.dao.MechanicDAO;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.service.interf.MechanicService;

import java.util.List;

public class MechanicServiceImpl implements MechanicService {

    private final MechanicDAO mechanicDAO;

    public MechanicServiceImpl(MechanicDAO mechanicDAO) {
        this.mechanicDAO = mechanicDAO;
    }

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
