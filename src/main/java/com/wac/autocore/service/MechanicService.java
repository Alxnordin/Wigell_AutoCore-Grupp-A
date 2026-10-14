package com.wac.autocore.service;

import com.wac.autocore.dao.MechanicDAO;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.repository.MechanicRepository;

import java.util.List;

public class MechanicService {

    //private final MechanicDAO mechanicDAO = new MechanicDAO();
    private final MechanicRepository mechanicRepository;

    public MechanicService(){
        this(new MechanicDAO());
    }

    public MechanicService(MechanicRepository mechanicRepository){
        this.mechanicRepository = mechanicRepository;
    }

    public List<Mechanic> getMechanics() {
        return mechanicRepository.findAll();
    }

    public Mechanic findMechanic(int id) {
        for (Mechanic mechanic : mechanicRepository.findAll()) {
            if (mechanic.getId() == id) {
                return mechanic;
            }
        }

        return null;
    }

    public void updateAvailable(Mechanic mechanic) {
        mechanicRepository.updateAvailable(mechanic);
    }


}
