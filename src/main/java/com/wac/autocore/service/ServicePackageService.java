package com.wac.autocore.service;

import com.wac.autocore.dao.ServicePackageDAO;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePackage;
import java.util.List;

//Alexander'
//Servicepaket: hämtar, skapar och ändrar paket
//paketet är en mall. Bokningar som redan skapats från ett paket påverkas inte när paketet ändras
public class ServicePackageService {

    private final ServicePackageDAO servicePackageDAO = new ServicePackageDAO();

    private final ServiceItemService serviceItemService;

    public ServicePackageService (ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    public List<ServicePackage> servicePackages() {
        return servicePackageDAO.findAll();
    }

    public ServicePackage findServicePackage(int id) {
        for (ServicePackage servicePackage : servicePackageDAO.findAll()) {
            if (servicePackage.getId()==id){
                return servicePackage;
            }
        }

        return null;
    }

    //skapar ett nytt tomt paket. Namnet måste finnas & får ej vara tomt
    public ServicePackage createServicePackage(String name) {
        if (name==null|| name.trim().isEmpty()) {
            System.out.println("Service package name is missing");
            return null;
        }
        for (ServicePackage servicePackage : servicePackageDAO.findAll()) {
            if (servicePackage.getName().equalsIgnoreCase(name.trim())) {
                System.out.println("Service package " + name.trim() + " already exists");
                return null;
            }
        }

        ServicePackage servicePackage = servicePackageDAO.save(new ServicePackage(0, name.trim()));
        System.out.println("Service package created: " + servicePackage);
        return servicePackage;
    }

    //lägger till en befintlig tjänst i ett paket
    public boolean addServiceToPackage(int servicePackageId, int serviceItemId) {
        ServicePackage servicePackage = findServicePackage(servicePackageId);

        if (servicePackage == null) {
            System.out.println("Servivce package with ID " + servicePackageId +
                " does not exist.");
            return false;
        }

        if (serviceItemService.findServiceItem(serviceItemId) == null) {
            System.out.println("Service item with ID " + serviceItemId + " does not exist.");
            return false;
        }

        if (servicePackage.containsServiceItem(serviceItemId)) {
            System.out.println("Service item " + serviceItemId + " is already in service package " + servicePackageId + ".");
            return false;
        }

        servicePackageDAO.addServiceItem(servicePackageId, serviceItemId);
        System.out.println("Service item " + serviceItemId + " added to service package " + servicePackageId + ".");
        return true;
    }

    //tar bort en tjänst från ett paket
    public boolean removeServiceFromPackage(int servicePackageId, int serviceItemId) {
        ServicePackage servicePackage = findServicePackage(servicePackageId);

        if (servicePackage == null) {
            System.out.println("Service package with ID " + servicePackageId + " does not exist.");
            return false;
        }

        if (!servicePackage.containsServiceItem(serviceItemId)) {
            System.out.println("Service item " + serviceItemId + " is not in service package " + servicePackageId + ".");
            return false;
        }

        servicePackageDAO.removeServiceItem(servicePackageId, serviceItemId);
        System.out.println("Service item " + serviceItemId + " removed from service package " + servicePackageId + ".");
        return true;
    }
}
