package com.wac.autocore.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//Alexander
//Composite mönster- ett servicepaket (t.ex. Vinterkontroll) som innehåller flera tjänster.
//Paketet är en mall. När det används i en bokning kopieras tjänsterna in i bokningen,
//bokningen påverkas inte om paketet ändras senare.
public class ServicePackage implements ServiceComponent {

    private int id;
    private String name;
    private final List<ServiceComponent> components = new ArrayList<ServiceComponent>();

    public ServicePackage(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    @Override
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    //lägger till en tjänst i paketet
    public void add(ServiceComponent component) {
        components.add(component);
    }

    //paketets pris är summan av tjänsternas pris
    @Override
    public double getPrice() {
        return 0;

        for (ServiceComponent component : components) {
            totalPrice += component.getPrice();
        }
        return totalPrice;
    }

    //paketets tid är totalen av tjänsternas tid
    @Override
    public int getEstimatedMinutes() {
        return 0;

        for (ServiceComponent component : components) {
            totalMinutes +=component.getEstimatedMinutes();
        }
        return totalMinutes;
    }

    //alla enskilda tjänster som ingår i paketet
    @Override
    public List<ServiceItem> getServiceItems() {
        List<ServiceItem> serviceItems = new ArrayList<ServiceItem>();

        for (ServiceComponent component : components) {
            serviceItems.addAll(component.getServiceItems());
        }
        return serviceItems;
    }

    public int getServiceCount() {
        return getServiceCount().size();
    }


    //antal tjänster i paketet
    public boolean containsServiceItem(int serviceItemId) {
        for (ServiceItem serviceItem : getServiceItems()) {
            if (serviceItem.getId() == serviceItemId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return id + " - " + name +
                " | Services: " + getServiceCount() +
                " | Price: " + getPrice() + " SEK" +
                " | Estimated time: " + getEstimatedMinutes() + " min";
    }

}
