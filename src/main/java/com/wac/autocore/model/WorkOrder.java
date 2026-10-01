package com.wac.autocore.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrder {
/*lagt till ny map för att kunna para ihop pris med tjänst
* och spara priset så som det såg ut just när ordern skapades*/
    private int id;
    private int bookingId;
    private int mechanicId;
    private List<Integer> serviceItemIds;
    private Map<Integer, Double> serviceItemPrices;
    private String status;

    public WorkOrder(int id, int bookingId, int mechanicId) {
        this.id = id;
        this.bookingId = bookingId;
        this.mechanicId = mechanicId;
        this.serviceItemIds = new ArrayList<Integer>();
        this.serviceItemPrices = new HashMap<Integer, Double>();
        this.status = "CREATED";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
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

    public Map<Integer, Double> getServiceItemPrices(){
        return serviceItemPrices;
    }

    public void setServiceItemPrices(Map<Integer, Double> serviceItemPrices){
        this.serviceItemPrices = serviceItemPrices;
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