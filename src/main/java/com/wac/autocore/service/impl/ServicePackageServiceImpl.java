package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.validation.Validator;
import com.wac.autocore.model.ServicePackage;
import com.wac.autocore.service.interf.ServiceItemService;
import com.wac.autocore.service.interf.ServicePackageService;
import com.wac.autocore.repository.ServicePackageRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

//Alexander'
//Servicepaket: hämtar, skapar och ändrar paket
//paketet är en mall. Bokningar som redan skapats från ett paket påverkas inte när paketet ändras
@Service
public class ServicePackageServiceImpl implements ServicePackageService {

    //private final ServicePackageDAO servicePackageDAO = new ServicePackageDAO();
    private final ServicePackageRepository servicePackageRepository;
    private final ServiceItemService serviceItemService;

    public ServicePackageServiceImpl (ServicePackageRepository servicePackageRepository,
                                      ServiceItemService serviceItemService) {
        this.servicePackageRepository = servicePackageRepository;
        this.serviceItemService = serviceItemService;
    }

    public List<ServicePackage> getServicePackages() {
        return servicePackageRepository.findAll();
    }

    public ServicePackage findServicePackage(int id) {
        for (ServicePackage servicePackage : servicePackageRepository.findAll()) {
            if (servicePackage.getId()==id){
                return servicePackage;
            }
        }

        return null;
    }

    //skapar ett nytt tomt paket. Namnet måste finnas & får ej vara tomt
    //Alexander: tomt namn -> ValidationException, namnet redan upptaget -> BusinessRuleException
    @PreAuthorize("hasRole('ADMIN')")
    public ServicePackage createServicePackage(String name) {
        Validator.requireText(name, "packageNameRequiredError");

        for (ServicePackage servicePackage : servicePackageRepository.findAll()) {
            if (servicePackage.getName().equalsIgnoreCase(name.trim())) {
                throw new BusinessRuleException("packageNameExistsError", name.trim());
            }
        }

        return servicePackageRepository.save(new ServicePackage(0, name.trim()));
    }

    //lägger till en befintlig tjänst i ett paket
    @PreAuthorize("hasRole('ADMIN')")
    public void addServiceToPackage(int servicePackageId, int serviceItemId) {
        ServicePackage servicePackage = requireServicePackage(servicePackageId);

        if (serviceItemService.findServiceItem(serviceItemId) == null) {
            throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
        }

        if (servicePackage.containsServiceItem(serviceItemId)) {
            throw new BusinessRuleException("serviceAlreadyInPackageWarning");
        }

        servicePackageRepository.addServiceItem(servicePackageId, serviceItemId);
    }

    //tar bort en tjänst från ett paket
    @PreAuthorize("hasRole('ADMIN')")
    public void removeServiceFromPackage(int servicePackageId, int serviceItemId) {
        ServicePackage servicePackage = requireServicePackage(servicePackageId);

        if (!servicePackage.containsServiceItem(serviceItemId)) {
            throw new BusinessRuleException("serviceNotInPackageError");
        }

        servicePackageRepository.removeServiceItem(servicePackageId, serviceItemId);
    }

    //Alexander
    //paketet måste finnas - samma kontroll används av add och remove
    private ServicePackage requireServicePackage(int servicePackageId) {
        ServicePackage servicePackage = findServicePackage(servicePackageId);

        if (servicePackage == null) {
            throw new NotFoundException("packageNotFoundError", String.valueOf(servicePackageId));
        }
        return servicePackage;
    }
}
