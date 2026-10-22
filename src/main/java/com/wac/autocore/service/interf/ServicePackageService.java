package com.wac.autocore.service.interf;

import com.wac.autocore.model.ServicePackage;

import java.util.List;

public interface ServicePackageService {

    List<ServicePackage> getServicePackages();
    ServicePackage findServicePackage(int id);
    //Alexander
    //kastar exception om något inte är tillåtet, istället för att svara null/false
    ServicePackage createServicePackage(String name);
    void addServiceToPackage(int servicePackageId, int serviceItemId);
    void removeServiceFromPackage(int servicePackageId, int serviceItemId);

}
