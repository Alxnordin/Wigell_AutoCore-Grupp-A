package com.wac.autocore.service.impl;

import com.wac.autocore.model.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.repository.WorkOrderRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkOrderServiceImpl implements WorkOrderService {

    //private final WorkOrderDAO workOrderDAO = new WorkOrderDAO();
    private final WorkOrderRepository workOrderRepository;

    private final BookingService bookingService;
    private final MechanicService mechanicService;
    private final ServiceItemService serviceItemService;
    private final CustomerService customerService;
    private final VehicleService vehicleService;

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository,
                                BookingService bookingService,
                                MechanicService mechanicService,
                                ServiceItemService serviceItemService,
                                CustomerService customerService,
                                VehicleService vehicleService) {
        this.workOrderRepository = workOrderRepository;
        this.bookingService = bookingService;
        this.mechanicService = mechanicService;
        this.serviceItemService = serviceItemService;
        this.customerService = customerService;
        this.vehicleService = vehicleService;
    }

    public List<WorkOrder> findAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    public WorkOrder findWorkOrder(int id) {
        for (WorkOrder workOrder : workOrderRepository.findAll()) {
            if (workOrder.getId() == id) {
                return workOrder;
            }
        }

        return null;
    }

    public void updateMechanic(WorkOrder workOrder, int mechanicId) {

        Mechanic mechanic = mechanicService.findMechanic(mechanicId);

        if (mechanic == null) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return;
        }

        workOrder.setMechanicId(mechanicId);
        workOrderRepository.update(workOrder);
    }

    //true om bokningen redan har en arbetsorder (en bokning kan bara ha en)
    public boolean hasWorkOrder(int bookingId) {
        for (WorkOrder workOrder : workOrderRepository.findAll()) {
            //Alexander
            //drop-in saknar bokning (null), och då ska den inte jämföras med bokningens id
            Integer orderBookingId = workOrder.getBookingId();
            if (orderBookingId != null && orderBookingId == bookingId && !workOrder.isComplaint()) {
                return true;
            }
        }
        return false;
    }

    //Alexander
    //Planned order: arbetsorder från en befintlig bokning.
    //Tjänsternas priser låses i ordern när den skapas, så senare prisändringar påverkar inte ordern.
    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     int... serviceItemIds) {

        Booking booking = bookingService.findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            return null;
        }

        //en bokning kan bara ha en arbetsorder
        if (hasWorkOrder(bookingId)) {
            System.out.println("Booking " + bookingId + " already has a work order.");
            return null;
        }

        Mechanic mechanic = mechanicService.findMechanic(mechanicId);

        if (mechanic == null) {
            System.out.println("Mechanic with ID " + mechanicId + " does not exist.");
            return null;
        }

        //mekanikern får inte redan ha en arbetsorder samma dag som bokningen
        if (isMechanicBookedOnDate(mechanicId, booking.getDate(), bookingId)) {
            System.out.println("Mechanic " + mechanic.getName() + " is already booked on " + booking.getDate() + ".");
            return null;
        }

        if (serviceItemIds == null || serviceItemIds.length == 0) {
            System.out.println("A work order must contain at least one service.");
            return null;
        }

        for (int serviceItemId : serviceItemIds) {
            if (serviceItemService.findServiceItem(serviceItemId) == null) {
                System.out.println("Service item with ID " + serviceItemId + " does not exist.");
                return null;
            }
        }

        //byggs med WorkOrderBuilder, på samma sätt som drop-in och utkast
        WorkOrderBuilder builder = new WorkOrderBuilder()
                .bookingId(bookingId)
                .mechanicId(mechanicId)
                .description(booking.getDescription());

        Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());
        if (vehicle != null) {
            builder.vehicleId(vehicle.getId())
                    .customerId(vehicle.getCustomerId());
        }

        for (int serviceItemId : serviceItemIds) {
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);
            builder.addServiceItem(serviceItemId, serviceItem.getPrice());
        }

        WorkOrder workOrder = builder.build();
        workOrderRepository.save(workOrder);

        //bokningens nya status sparas i databasen
        booking.setStatus("WORK_ORDER_CREATED");
        bookingService.updateStatus(booking);

        System.out.println("Work order created successfully.");
        System.out.println(workOrder);

        return workOrder;
    }

    public void removeServiceItemFromWorkOrder(WorkOrder workOrder, int serviceItemId){

        if (workOrder == null) {
            return;
        }
        workOrder.removeServiceItem(serviceItemId);
        workOrderRepository.removeServiceItem(workOrder.getId(), serviceItemId);

        System.out.println("ServiceItem " + serviceItemId + " removed from workOrder " + workOrder.getId());
    }

    // Kontrollera mekaniker
    public boolean isMechanicBookedOnDate(int mechanicId,
                                          LocalDate date, int excludingBookingId) {

        for (WorkOrder workOrder : findAllWorkOrders()) {

            if(workOrder.getBookingId() == null){
                continue;
            }

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

            //Hoppa över workOrders som inte har någon booking
            if(otherWorkOrder.getBookingId() == null) {
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


    // Kontrollerar om arbetsordern får övergå till den nya statusen.
    private boolean isAllowedStatusTransition(WorkOrder workOrder, String newStatus) {
        if (workOrder == null || newStatus == null) {
            return false;
        }

        String currentStatus = workOrder.getStatus();

        // Ett utkast kan färdigställas till en skapad arbetsorder.
        if ("DRAFT".equals(currentStatus) && "CREATED".equals(newStatus)) {
            return true;
        }

        // En skapad arbetsorder kan startas.
        if ("CREATED".equals(currentStatus) && "IN_PROGRESS".equals(newStatus)) {
            return true;
        }

        // En pågående arbetsorder kan slutföras.
        if ("IN_PROGRESS".equals(currentStatus) && "COMPLETED".equals(newStatus)) {
            return true;
        }

        return false;
    }


    public void startWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        // Kontrollerar statusövergången innan arbetet startar.
        if (!isAllowedStatusTransition(workOrder, "IN_PROGRESS")) {
            System.out.println("Work order cannot be started.");
            return;
        }

        Mechanic mechanic = mechanicService.findMechanic(workOrder.getMechanicId());

        System.out.println("Mechanic: " + mechanic.getName());

        //Alexander
        //drop-in saknar bokning, då finns ingen bokningsstatus att uppdatera
        Booking booking = workOrder.getBookingId() != null
                ? bookingService.findBooking(workOrder.getBookingId())
                : null;

        if (mechanic != null) {
            mechanic.setAvailable(false);
            mechanicService.updateAvailable(mechanic);
        }

        if (booking != null) {
            booking.setStatus("IN_PROGRESS");
            bookingService.updateStatus(booking);
        }

        workOrder.setStatus("IN_PROGRESS");
        workOrderRepository.updateStatus(workOrder);

        System.out.println("Work order " + workOrderId + " has been started.");
    }

    public void completeWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        if (workOrder == null) {
            System.out.println("Work order with ID " + workOrderId + " does not exist.");
            return;
        }

        // Kontrollerar statusövergången innan arbetet slutförs.
        if (!isAllowedStatusTransition(workOrder, "COMPLETED")) {
            System.out.println("Only work orders in progress can be completed.");
            return;
        }

        Mechanic mechanic = mechanicService.findMechanic(workOrder.getMechanicId());
        //Alexander
        //drop-in saknar bokning, då finns ingen bokningsstatus att uppdatera
        Booking booking = workOrder.getBookingId() != null
                ? bookingService.findBooking(workOrder.getBookingId())
                : null;

        workOrder.setStatus("COMPLETED");
        workOrderRepository.updateStatus(workOrder);

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
        return createComplaint(originalWorkOrderId, null);
    }

    //Alexander
    //samma som ovan, men reklamationens egen beskrivning (vad som är fel) sparas på den nya ordern
    public WorkOrder createComplaint(int originalWorkOrderId, String complaintDescription){
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

        if (complaintDescription != null && !complaintDescription.trim().isEmpty()) {
            complaint.setDescription(complaintDescription.trim());
        }

        workOrderRepository.save(complaint);

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

        workOrderRepository.save(workOrder);

        System.out.println("Drop-in work order created successfully.");
        System.out.println(workOrder);

        return workOrder;
    }

    // Skapar ett utkast och sparar de tjänster som användaren har valt.
    public WorkOrder createDraftWorkOrder(
            int customerId,
            int vehicleId,
            String description,
            int mechanicId,
            int... serviceItemIds) {

        Customer customer = customerService.findCustomer(customerId);
        Vehicle vehicle = vehicleService.findVehicle(vehicleId);

        // Kontrollerar att kunden och fordonet finns.
        if (customer == null || vehicle == null) {
            System.out.println("Customer or vehicle does not exist.");
            return null;
        }

        // Säkerställer att fordonet tillhör den valda kunden.
        if (vehicle.getCustomerId() != customerId) {
            System.out.println("Vehicle does not belong to customer.");
            return null;
        }

        // Kontrollerar alla tjänster innan utkastet skapas.
        if (serviceItemIds != null) {
            for (int serviceItemId : serviceItemIds) {
                if (serviceItemService.findServiceItem(serviceItemId) == null) {
                    System.out.println(
                            "Service item does not exist: " + serviceItemId
                    );
                    return null;
                }
            }
        }

        // Kontrollerar att den valda mekanikern finns.
        Mechanic mechanic = mechanicService.findMechanic(mechanicId);

        if (mechanic == null) {
            System.out.println("Mechanic does not exist: " + mechanicId);
            return null;
        }

        // Bygger ett nytt utkast med statusen DRAFT.
        WorkOrderBuilder builder = new WorkOrderBuilder()
                .customerId(customerId)
                .vehicleId(vehicleId)
                .mechanicId(mechanicId)
                .description(description)
                .status("DRAFT");


        // Sparar varje vald tjänst tillsammans med dess aktuella pris.
        if (serviceItemIds != null) {
            for (int serviceItemId : serviceItemIds) {
                ServiceItem service =
                        serviceItemService.findServiceItem(serviceItemId);
//        WorkOrder workOrder = builder.build();
//        workOrderRepository.save(workOrder);

                builder.addServiceItem(serviceItemId, service.getPrice());
            }
        }

        WorkOrder draft = builder.build();

        // Sparar utkastet och låter databasen tilldela ett ID.
        workOrderRepository.save(draft);

        System.out.println("Draft work order created successfully.");
        System.out.println(draft);

        return draft;
    }


    // Uppdaterar en befintlig draft utan att skapa en ny arbetsorder.
    public WorkOrder updateDraftWorkOrder(
            int workOrderId,
            int customerId,
            int vehicleId,
            String description,
            Integer mechanicId,
            int... serviceItemIds) {

        // Kontrollerar mekanikern om en har valts.
        if (mechanicId != null) {
            Mechanic mechanic = mechanicService.findMechanic(mechanicId);

            if (mechanic == null) {
                System.out.println("Mechanic does not exist: " + mechanicId);
                return null;
            }
        }

        WorkOrder draft = workOrderRepository.findById(workOrderId);

        // Kontrollerar att ordern finns och fortfarande är en draft.
        if (draft == null || !"DRAFT".equals(draft.getStatus())) {
            System.out.println("Draft does not exist or is not editable.");
            return null;
        }

        // Kontrollerar att kunden och fordonet finns.
        Customer customer = customerService.findCustomer(customerId);
        Vehicle vehicle = vehicleService.findVehicle(vehicleId);

        if (customer == null || vehicle == null) {
            System.out.println("Customer or vehicle does not exist.");
            return null;
        }

        // Säkerställer att fordonet tillhör den valda kunden.
        if (vehicle.getCustomerId() != customerId) {
            System.out.println("Vehicle does not belong to customer.");
            return null;
        }


        // Kontrollerar alla tjänster innan utkastet ändras.
        if (serviceItemIds != null) {
            for (int serviceItemId : serviceItemIds) {
                if (serviceItemService.findServiceItem(serviceItemId) == null) {
                    System.out.println(
                            "Service item does not exist: " + serviceItemId
                    );
                    return null;
                }
            }
        }

        // Uppdaterar uppgifterna på det befintliga utkastet.
        draft.setCustomerId(customerId);
        draft.setVehicleId(vehicleId);
        draft.setDescription(description);

        // Ersätter de gamla tjänsterna med formulärets aktuella tjänster.
        draft.setServiceItemIds(new ArrayList<Integer>());
        draft.setServiceItemPrices(
                new HashMap<Integer, Double>()
        );

        if (serviceItemIds != null) {
            for (int serviceItemId : serviceItemIds) {
                ServiceItem service =
                        serviceItemService.findServiceItem(serviceItemId);

                draft.addServiceItem(serviceItemId, service.getPrice());
            }
        }

        // Sparar vald mekaniker, eller noll om ingen är vald.
        draft.setMechanicId(mechanicId == null ? 0 : mechanicId);

        // Sparar ändringarna med samma ID och status.
        workOrderRepository.update(draft);

        return draft;
    }



    public WorkOrder completeDraftWorkOrder(int workOrderId,
                                            int mechanicId,
                                            int... serviceItemsIds) {

        WorkOrder workOrder = workOrderRepository.findById(workOrderId);

        if(workOrder == null) {
            System.out.println("work order with ID " + workOrderId + " does not exist.");
            return null;
        }

        if(!"DRAFT".equals(workOrder.getStatus())) {
            System.out.println("work order with ID " + workOrderId + " is not a draft");
            return null;
        }

        if(workOrder.getCustomerId() <=0){
            System.out.println("Work order has no customer");
            return null;
        }

        if(workOrder.getVehicleId() <=0){
            System.out.println("Work order has no vehicle");
            return null;
        }

        if (serviceItemsIds == null || serviceItemsIds.length == 0){
            System.out.println("Work order must have at least one service item");
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

        // Ersätter utkastets gamla tjänster med tjänsterna från formuläret.
        workOrder.setServiceItemIds(new ArrayList<Integer>());
        workOrder.setServiceItemPrices(new java.util.HashMap<Integer, Double>());

        for (int serviceItemId : serviceItemsIds) {
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);

            if(serviceItem == null){
                System.out.println("ServiceItem with ID" + serviceItemId + " does not exist");
                return null;
            }
            workOrder.addServiceItem(serviceItemId, serviceItem.getPrice());
        }
        // Kontrollerar att utkastet får övergå till CREATED.
        if (!isAllowedStatusTransition(workOrder, "CREATED")) {
            System.out.println("Draft work order cannot be completed.");
            return null;
        }

        // Uppdaterar mekanikern när utkastet färdigställs.
        workOrder.setMechanicId(mechanicId);
        workOrder.setStatus("CREATED");

        // Sparar den färdigställda arbetsordern.
        workOrderRepository.update(workOrder);
        System.out.println("Draft workorder completed successfully");
        System.out.println(workOrder);

        return workOrder;
    }



}