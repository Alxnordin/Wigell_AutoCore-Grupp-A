package com.wac.autocore.repository;

import com.wac.autocore.model.ServicePackage;

import java.util.List;

public interface ServicePackageRepository {
    List<ServicePackage> findAll();

    ServicePackage save(ServicePackage servicePackage);

    void addServiceItem(int servicePackageId, int serviceItemId);

    void removeServiceItem(int servicePackageId, int serviceItemId);
}
