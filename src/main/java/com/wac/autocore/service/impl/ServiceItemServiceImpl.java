package com.wac.autocore.service.impl;

import com.wac.autocore.dao.ServiceItemDAO;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.service.interf.ServiceItemService;

import java.util.List;

public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemDAO serviceItemDAO;

    public ServiceItemServiceImpl(ServiceItemDAO serviceItemDAO) {
        this.serviceItemDAO = serviceItemDAO;
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItemDAO.findAll();
    }

    public ServiceItem findServiceItem(int id) {
        for (ServiceItem serviceItem : serviceItemDAO.findAll()) {
            if (serviceItem.getId() == id) {
                return serviceItem;
            }
        }

        return null;
    }

    public void changeServicePrice(int serviceItemId, double newPrice){
        if (newPrice <= 0){
            System.out.println("Select price higher than 0");
            return;
        }
        ServiceItem serviceItem = findServiceItem(serviceItemId);
        if (serviceItem == null){
            System.out.println("ServiceItemId " + serviceItemId + " does not exist.");
            return;
        }

        serviceItemDAO.updatePrice(serviceItemId,newPrice);
        serviceItem.setPrice(newPrice);
    }


}
