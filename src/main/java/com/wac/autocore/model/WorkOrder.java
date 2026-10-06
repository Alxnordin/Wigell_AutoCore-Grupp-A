package com.wac.autocore.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrder {
    //Ändrar bookingId från int till integer för att den behöver
    //kunna vara null vid drop in.
    private int id;
    private Integer bookingId;
    private int customerId;
    private int vehicleId;
    private int mechanicId;
    private List<Integer> serviceItemIds;
    private Map<Integer, Double> serviceItemPrices;
    private String status;
    //reklamation
    private boolean complaint;
    private Integer originalWorkOrderId;

    public WorkOrder(int id, Integer bookingId, int mechanicId) {
        this.id = id;
        this.bookingId = bookingId;
        this.mechanicId = mechanicId;
        this.serviceItemIds = new ArrayList<Integer>();
        this.serviceItemPrices = new HashMap<Integer, Double>();
        this.status = "CREATED";
        //Reklamation
        this.complaint = false;
        this.originalWorkOrderId = null;
    }

    //Prototypemönster - för reklamation

    public WorkOrder clone(){
        WorkOrder copy = new WorkOrder(this.id,this.bookingId,this.mechanicId);

        copy.customerId = this.customerId;
        copy.vehicleId = this.vehicleId;

        copy.serviceItemIds = new ArrayList<>(this.serviceItemIds);
        copy.serviceItemPrices = new HashMap<>(this.serviceItemPrices);

        copy.status = this.status;
        copy.complaint = this.complaint;
        copy.originalWorkOrderId = this.originalWorkOrderId;

        return copy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public int getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(int mechanicId) {
        this.mechanicId = mechanicId;
    }

    public List<Integer> getServiceItemIds() {
        return serviceItemIds;
    }

    public void setServiceItemIds(List<Integer> serviceItemIds) {
        this.serviceItemIds = serviceItemIds;
    }

    public Map<Integer, Double> getServiceItemPrices() {
        return serviceItemPrices;
    }

    public void setServiceItemPrices(Map<Integer, Double> serviceItemPrices) {
        this.serviceItemPrices = serviceItemPrices;
    }
    public boolean isComplaint() {
        return complaint;
    }
    public void setComplaint(boolean complaint){
        this.complaint = complaint;
    }

    public Integer getOriginalWorkOrderId(){
        return originalWorkOrderId;
    }

    public void setOriginalWorkOrderId(Integer originalWorkOrderId){
        this.originalWorkOrderId = originalWorkOrderId;
    }

    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId){
        this.customerId = customerId;
    }
    public int getVehicleId() {
        return vehicleId;
    }
    public void setVehicleId(int vehicleId){
        this.vehicleId = vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addServiceItem(int serviceItemId) {
        serviceItemIds.add(serviceItemId);
    }
    /*parar ihop tjänst med aktuellt pris och sparar detta*/
    public void addServiceItem(int serviceItemId, double price) {

        serviceItemIds.add(serviceItemId);
        serviceItemPrices.put(serviceItemId, price);
    }

    public void removeServiceItem(int serviceItemId) {

        serviceItemIds.remove(Integer.valueOf(serviceItemId));
        serviceItemPrices.remove(serviceItemId);
    }

    @Override
    public String toString() {
        return id +
                " - Booking ID: " + bookingId +
                " | Mechanic ID: " + mechanicId +
                " | Services: " + serviceItemIds +
                " | Status: " + status;
    }
}