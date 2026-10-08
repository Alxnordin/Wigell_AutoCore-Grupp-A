package com.wac.autocore.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Booking {

    private int id;
    private int vehicleId;
    private LocalDate date;
    private String description;
    private String status;
    private List<Integer> serviceItemIds;

    //EJ KLAR.
    //Testa Prototype-mönster - samma som Fredrik gjort med workorder

    private Integer originalBookingId;

    public Booking(int id, int vehicleId, LocalDate date, String description) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.date = date;
        this.description = description;
        this.status = "BOOKED";
        this.serviceItemIds = new ArrayList<Integer>();
    }

    //EJ KLAR!! Kolla hur Fredrik gjort i WorkOrder
    public Booking cloneBooking(){
        Booking bookingClone = new Booking(this.id, this.vehicleId, this.date, this.description);
        return bookingClone;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getServiceItemIds() {
        return serviceItemIds;
    }

    public void setServiceItemIds(List<Integer> serviceItemIds) {
        this.serviceItemIds = serviceItemIds;
    }

    public void addServiceItem(int serviceItemId) {
        serviceItemIds.add(serviceItemId);
    }

    public void removeServiceItem(int serviceItemId) {
        serviceItemIds.remove(Integer.valueOf(serviceItemId));
    }

    public boolean containsServiceItem(int serviceItemId) {
        return serviceItemIds.contains(serviceItemId);}

    public String getStatus() {return status;}

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - Vehicle ID: " + vehicleId +
                " | Date: " + date +
                " | Description: " + description +
                " | Services: " + serviceItemIds +
                " | Status: " + status;
    }
}