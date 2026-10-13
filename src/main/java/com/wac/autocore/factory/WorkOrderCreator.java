package com.wac.autocore.factory;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderType;
import com.wac.autocore.view.order.CreateWorkOrderView;

public interface WorkOrderCreator {

    //anger vilken typ av arbetsorder skaparen hanterar
    WorkOrderType getType();

    void configureView(CreateWorkOrderView view);

    //skapar arbetsordern utifrån informationen i formuläret
    WorkOrder createOrder(CreateWorkOrderView view);
}
