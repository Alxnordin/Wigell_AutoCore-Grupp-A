package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.service.interf.ServiceItemService;
import com.wac.autocore.repository.ServiceItemRepository;
import com.wac.autocore.validation.Validator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemServiceImpl(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItemRepository.findAll();
    }

    public ServiceItem findServiceItem(int id) {
        for (ServiceItem serviceItem : serviceItemRepository.findAll()) {
            if (serviceItem.getId() == id) {
                return serviceItem;
            }
        }

        return null;
    }

    //Alexander
    //priset tolkas och kontrolleras här, så att samma regel gäller oavsett vem som anropar.
    //ogiltigt pris -> ValidationException, tjänsten finns inte -> NotFoundException
    @PreAuthorize("hasRole('ADMIN')")
    public void changeServicePrice(int serviceItemId, String priceText) {
        double newPrice = Validator.requireDecimal(priceText, "invalidPriceError");
        Validator.requirePositive(newPrice, "invalidPriceError");

        ServiceItem serviceItem = findServiceItem(serviceItemId);
        if (serviceItem == null) {
            throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
        }

        if (serviceItem.getPrice() == newPrice) {
            throw new BusinessRuleException("noChangeMade");
        }

        serviceItemRepository.updatePrice(serviceItemId, newPrice);
        serviceItem.setPrice(newPrice);
    }
}