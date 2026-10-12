package com.wac.autocore.model;

import java.util.List;

//Alexander
//Composite mönster: gemensamt gränssnitt för en enskild tjänst (ServiceItem) och ett servicepaket (ServicePackage).
//koden som använder gränssnittet behöver inte veta om den har en tjänst eller ett helt paket framför sig.
public interface ServiceComponent {

    String getName();

    //pris för tjänsten, eller summan av paketets tjänster
    double getPrice();

    //beräknad tid i minuter för tjänsten, eller summan av paketets tjänster
    int getEstimatedMinutes();

    //de enskilda tjänsterna: en tjänst ger sig själv, ett paket ger alla sina tjänster
    List<ServiceItem> getServiceItems();
}