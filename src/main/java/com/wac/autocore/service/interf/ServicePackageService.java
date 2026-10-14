package com.wac.autocore.service.interf;

import com.wac.autocore.model.ServicePackage;

import java.util.List;

public interface ServicePackageService {

    List<ServicePackage> servicePackages();
    ServicePackage findServicePackage(int id);
    ServicePackage createServicePackage(String name);
    boolean addServiceToPackage(int servicePackageId, int serviceItemId);
    public boolean removeServiceFromPackage(int servicePackageId, int serviceItemId);

}
