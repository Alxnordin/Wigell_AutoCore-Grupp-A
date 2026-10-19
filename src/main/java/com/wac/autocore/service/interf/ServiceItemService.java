package com.wac.autocore.service.interf;

import com.wac.autocore.model.ServiceItem;

import java.util.List;

public interface ServiceItemService {

    List<ServiceItem> getServiceItems();

    ServiceItem findServiceItem(int id);

    //Alexander
    //priset tas emot som text och valideras i servicen, kastar exception vid fel
    void changeServicePrice(int serviceItemId, String priceText);




}
