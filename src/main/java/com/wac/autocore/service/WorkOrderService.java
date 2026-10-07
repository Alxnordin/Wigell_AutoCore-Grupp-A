package com.wac.autocore.service;

import com.wac.autocore.dao.WorkOrderDAO;
import com.wac.autocore.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderService {

    private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();

    private final BookingService bookingService;
    private final MechanicService mechanicService;
    private final ServiceItemService serviceItemService;
    private final CustomerService customerService;
    private final VehicleService vehicleService;

    public WorkOrderService(BookingService bookingService, MechanicService mechanicService, ServiceItemService serviceItemService, CustomerService customerService, VehicleService vehicleService) {
        this.bookingService = bookingService;
        this.mechanicService = mechanicService;
        this.serviceItemService = serviceItemService;
        this.customerService = customerService;
        this.vehicleService = vehicleService;
    }

    public List<WorkOrder> findAllWorkOrders() {
        return workOrderDAO.findAll();
    }

    public WorkOrder findWorkOrder(int id) {
        for (WorkOrder workOrder : workOrderDAO.findAll()) {
            if (workOrder.getId() == id) {
                return workOrder;
            }
        }

        return null;
    }

    //true om bokningen redan har en arbetsorder (en bokning kan bara ha en)
    public boolean hasWorkOrder(int bookingId) {
        for (WorkOrder workOrder : workOrderDAO.findAll()) {
            if (workOrder.getBookingId() == bookingId && !workOrder.isComplaint()) {
                return true;
            }
        }
        return false;
    }

     public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     int... serviceItemIds) {

        Booking booking = bookingService.findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return null;
        }

        Mechanic mechanic = mechanicService.findMechanic(mechanicId);

        if (mechanic == null) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
        }

        if (!mechanic.isAvailable()) {
            System.out.println("Mechanic " + mechanic.getName() + " is not available.");
            return null;
        }

        for (int serviceItemId : serviceItemIds) {
            if (serviceItemService.findServiceItem(serviceItemId) == null) {
                System.out.println("Service item with ID " + serviceItemId + " does not exist.");
                return null;
            }
        }
        WorkOrder workOrder = new WorkOrder(
                0,
                bookingId,
                mechanicId
        );

        for (int serviceItemId : serviceItemIds) {
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);
            workOrder.addServiceItem(serviceItemId, serviceItem.getPrice());
        }

        workOrderDAO.save(workOrder);

        booking.setStatus("WORK_ORDER_CREATED");

        System.out.println("Work order created successfully.");
        System.out.println(workOrder);

        return workOrder;
    }

    public void removeServiceItemFromWorkOrder(WorkOrder workOrder, int serviceItemId){

        if (workOrder == null) {
            return;
        }
        workOrder.removeServiceItem(serviceItemId);
        workOrderDAO.removeServiceItem(workOrder.getId(), serviceItemId);

        System.out.println("ServiceItem " + serviceItemId + " removed from workOrder " + workOrder.getId());
    }

    // Kontrollera mekaniker
    public boolean isMechanicBookedOnDate(int mechanicId,
                                          LocalDate date, int excludingBookingId) {

        for (WorkOrder workOrder : findAllWorkOrders()) {

            if (workOrder.getMechanicId() == mechanicId
                    && workOrder.getBookingId() != excludingBookingId) {

                Booking otherBooking = bookingService.findBooking(workOrder.getBookingId());

                if (otherBooking != null
                        && otherBooking.getDate().equals(date)) {

                    return true;
                }
            }
        }
        return false;
    }

    public List<Mechanic> getAvailableMechanics(
            Booking currentBooking, WorkOrder currentWorkOrder) {

        List<Mechanic> allMechanics = mechanicService.getMechanics();
        List<WorkOrder> allWorkOrders = findAllWorkOrders();

        List<Mechanic> availableMechanics = new ArrayList<>(allMechanics);

        for (WorkOrder otherWorkOrder : allWorkOrders) {

            // Hoppa över den arbetsorder vi just tittar på
            if (otherWorkOrder.getId() == currentWorkOrder.getId()) {
                continue;
            }

            Booking otherBooking = bookingService.findBooking(otherWorkOrder.getBookingId());

            if (otherBooking == null) {
                continue;
            }

            // Kontrollera om arbetsordern ligger samma dag
            if (!otherBooking.getDate().equals(currentBooking.getDate())) {
                continue;
            }

            // Hitta mekanikern som redan är bokad
            for (Mechanic mechanic : allMechanics) {

                if (mechanic.getId() == otherWorkOrder.getMechanicId()) {
                    availableMechanics.remove(mechanic);
                }
            }
        }

        return availableMechanics;
    }

    public List<ServiceItem> getServicesForWorkOrder(WorkOrder workOrder) {
        List<ServiceItem> result = new ArrayList<>();

        for (int serviceItemId : workOrder.getServiceItemIds()) {
            ServiceItem serviceItem =
                    serviceItemService.findServiceItem(serviceItemId);

            if (serviceItem != null) {
                result.add(serviceItem);
            }
        }

        return result;
    }

    public void startWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        if (!workOrder.getStatus().equals("CREATED")) {
            System.out.println("Work order cannot be started.");
            return;
        }

        Mechanic mechanic = mechanicService.findMechanic(workOrder.getMechanicId());
        Booking booking = bookingService.findBooking(workOrder.getBookingId());

        if (mechanic != null) {
            mechanic.setAvailable(false);
            mechanicService.updateAvailable(mechanic);
        }

        if (booking != null) {
            booking.setStatus("IN_PROGRESS");
            bookingService.updateStatus(booking);
        }

        workOrder.setStatus("IN_PROGRESS");
        workOrderDAO.updateStatus(workOrder);

        System.out.println("Work order " + workOrderId + " has been started.");
    }

    public void completeWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        if (!workOrder.getStatus().equals("IN_PROGRESS")) {
            System.out.println("Only work orders in progress can be completed.");
            return;
        }

        Mechanic mechanic = mechanicService.findMechanic(workOrder.getMechanicId());
        Booking booking = bookingService.findBooking(workOrder.getBookingId());

        workOrder.setStatus("COMPLETED");
        workOrderDAO.updateStatus(workOrder);

        if (mechanic != null) {
            mechanic.setAvailable(true);
            mechanicService.updateAvailable(mechanic);
        }

        if (booking != null) {
            booking.setStatus("COMPLETED");
            bookingService.updateStatus(booking);
        }

        System.out.println("Work order " + workOrderId + " has been completed.");
    }
    //skapa reklamation - prototype-mönster
    public WorkOrder createComplaint(int originalWorkOrderId){
        WorkOrder original = findWorkOrder(originalWorkOrderId);

        if(original == null){
            System.out.println("WorkOrder with ID " + originalWorkOrderId + " does not exist.");
            return null;
        }

        if(!original.getStatus().equals("COMPLETED")){
            System.out.println("A complaint can only be created for a completed work order");
            return null;
        }
        WorkOrder complaint = original.clone();

        complaint.setId(0);
        complaint.setStatus("CREATED");
        complaint.setComplaint(true);

        complaint.setOriginalWorkOrderId(original.getId());

        workOrderDAO.save(complaint);

        System.out.println("Complaint created");
        System.out.println(complaint);
        return complaint;
    }

    //DROP-IN workOrder
    public WorkOrder createDropInWorkOrder(int customerId,
                                           int vehicleId,
                                           int mechanicId,
                                           int... serviceItemIds) {

        Customer customer = customerService.findCustomer(customerId);

        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
        }

        Vehicle vehicle = vehicleService.findVehicle(vehicleId);
        if(vehicle == null) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
        }
        if (vehicle.getCustomerId() != customerId){
            System.out.println("Vehicle does not belong to customer " + customerId + ".");
            return null;
        }

        Mechanic mechanic = mechanicService.findMechanic(mechanicId);

        if (mechanic == null) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
        }

        if (!mechanic.isAvailable()) {
            System.out.println("Mechanic " + mechanic.getName() + " is not available.");
            return null;
        }

        for (int serviceItemId : serviceItemIds) {
            if (serviceItemService.findServiceItem(serviceItemId) == null) {
                System.out.println(
                        "Service item with ID " + serviceItemId + " does not exist."
                );
                return null;
            }
        }

        WorkOrderBuilder builder = new WorkOrderBuilder()
                                .customerId(customerId)
                                .vehicleId(vehicleId)
                                .mechanicId(mechanicId);

        for (int serviceItemId : serviceItemIds) {
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);
            builder.addServiceItem(serviceItemId,serviceItem.getPrice());
        }
        WorkOrder workOrder = builder.build();

        workOrderDAO.save(workOrder);

        System.out.println("Drop-in work order created successfully.");
        System.out.println(workOrder);

        return workOrder;
    }

    public WorkOrder createDraftWorkOrder(int customerId, int vehicleId, String description) {
        Customer customer = customerService.findCustomer(customerId);

        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return null;
        }

        Vehicle vehicle = vehicleService.findVehicle(vehicleId);
        if(vehicle == null) {
            System.out.println("Vehicle with ID " + vehicleId + " does not exist.");
            return null;
        }
        if (vehicle.getCustomerId() != customerId){
            System.out.println("Vehicle does not belong to customer " + customerId + ".");
            return null;
        }

        WorkOrderBuilder builder = new WorkOrderBuilder()
                .customerId(customerId)
                .vehicleId(vehicleId)
                .description(description)
                .status("DRAFT");

        WorkOrder workOrder = builder.build();
        workOrderDAO.save(workOrder);

        System.out.println("Draft work order created successfully");
        System.out.println(workOrder);
        return workOrder;
    }

    public WorkOrder completeDraftWorkOrder(int workOrderId,
                                            int mechanicId,
                                            int... serviceItemsIds) {

        WorkOrder workOrder = workOrderDAO.findById(workOrderId);

        if(workOrder == null) {
            System.out.println("work order with ID " + workOrderId + " does not exist.");
            return null;
        }

        if(!"DRAFT".equals(workOrder.getStatus())) {
            System.out.println("work order with ID " + workOrderId + " is not a draft");
            return null;
        }
        Mechanic mechanic = mechanicService.findMechanic(mechanicId);

        if (mechanic == null){
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
        }
        if (!mechanic.isAvailable()){
            System.out.println("Mechanic with ID " + mechanicId + " is not available.");
            return null;
        }
        for (int serviceItemId : serviceItemsIds) {
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);

            if(serviceItem == null){
                System.out.println("ServiceItem with ID" + serviceItemId + " does not exist");
                return null;
            }
            workOrder.addServiceItem(serviceItemId, serviceItem.getPrice());
        }
        workOrder.setMechanicId(mechanicId);
        workOrder.setStatus("CREATED");

        workOrderDAO.update(workOrder);
        System.out.println("Draft workorder completed successfully");
        System.out.println(workOrder);

        return workOrder;
    }


}
