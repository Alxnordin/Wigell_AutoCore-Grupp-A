package com.wac.autocore.service.interf;

import com.wac.autocore.model.ServiceItem;

import java.util.List;

public interface ServiceItemService {

    List<ServiceItem> getServiceItems();

    ServiceItem findServiceItem(int id);

    void changeServicePrice(int serviceItemId, double newPrice);




}
