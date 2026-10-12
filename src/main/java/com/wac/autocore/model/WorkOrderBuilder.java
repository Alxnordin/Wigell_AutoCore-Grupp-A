package com.wac.autocore.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrderBuilder {

    private int id;
    private Integer bookingId;
    private int customerId;
    private int vehicleId;
    private int mechanicId;
    private String description;

    private List<Integer> serviceItemIds = new ArrayList<>();
    private Map<Integer, Double> serviceItemPrices = new HashMap<>();

    private String status = "CREATED";
    private boolean complaint = false;
    private Integer originalWorkOrderId;

    public WorkOrderBuilder bookingId(Integer bookingId){
        this.bookingId = bookingId;
        return this;
    }
    public WorkOrderBuilder customerId(int customerId){
        this.customerId = customerId;
        return this;
    }
    public WorkOrderBuilder vehicleId(int vehicleId){
        this.vehicleId = vehicleId;
        return this;
    }
    public WorkOrderBuilder mechanicId(int mechanicId){
        this.mechanicId = mechanicId;
        return this;
    }
    public WorkOrderBuilder status(String status){
        this.status = status;
        return this;
    }
    public WorkOrderBuilder description(String description){
        this.description = description;
        return this;
    }
    public WorkOrderBuilder complaint(boolean complaint){
        this.complaint = complaint;
        return this;
    }
    public WorkOrderBuilder originalWorkOrderId(Integer originalWorkOrderId){
        this.originalWorkOrderId = originalWorkOrderId;
        return this;
    }
    public WorkOrderBuilder addServiceItem(int serviceItemId, double price){
        this.serviceItemIds.add(serviceItemId);
        this.serviceItemPrices.put(serviceItemId, price);
        return this;
    }
    public WorkOrder build() {
        WorkOrder workOrder = new WorkOrder(id, bookingId, mechanicId);

        workOrder.setCustomerId(customerId);
        workOrder.setVehicleId(vehicleId);
        workOrder.setStatus(status);
        workOrder.setDescription(description);
        workOrder.setComplaint(complaint);
        workOrder.setOriginalWorkOrderId(originalWorkOrderId);


        for(Integer serviceItemId : serviceItemIds){
            workOrder.addServiceItem(serviceItemId,serviceItemPrices.get(serviceItemId));
        }
        return workOrder;
    }


}
