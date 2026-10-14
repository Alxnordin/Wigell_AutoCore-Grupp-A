package com.wac.autocore.repository;

import com.wac.autocore.model.ServiceItem;

import java.util.List;

public interface ServiceItemRepository {

    List<ServiceItem> findAll();

    ServiceItem save(ServiceItem serviceItem);

    void updatePrice(int serviceItemId, double newPrice);

}
