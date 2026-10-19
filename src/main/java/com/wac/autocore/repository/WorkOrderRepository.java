package com.wac.autocore.repository;

import com.wac.autocore.model.WorkOrder;

import java.util.List;

public interface WorkOrderRepository {
    List<WorkOrder> findAll();

    WorkOrder findById(int workOrderId);

    void update (WorkOrder workOrder);

    WorkOrder save(WorkOrder workOrder);

    void removeServiceItem(int workOrderId, int serviceItemId);

    void updateStatus(WorkOrder workOrder);


}
