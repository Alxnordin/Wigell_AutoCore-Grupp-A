package com.wac.autocore.service.impl;

import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.model.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public void updateMechanic(WorkOrder workOrder, int mechanicId) {
        // Kontrollerar att arbetsordern finns.
        if (workOrder == null) {
            throw new NotFoundException("workOrderDoesNotExist");
        }

        // Mekanikern får bara ändras innan ordern har skapats.
        if (!"DRAFT".equals(workOrder.getStatus())) {
            System.out.println("Mechanic cannot be changed after work order creation.");
            throw new BusinessRuleException("mechanicCannotChangeAfterWorkOrderCreation");
            //return;
        }

        // Kontrollerar att den nya mekanikern finns.
        Mechanic mechanic = mechanicService.findMechanic(mechanicId);
        mechanicIsFoundOrThrowException(mechanic, mechanicId);

        // Sparar den nya mekanikern på utkastet.
        workOrder.setMechanicId(mechanicId);
        workOrderRepository.update(workOrder);
    }

    //true om bokningen redan har en arbetsorder (en bokning kan bara ha en)
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     int... serviceItemIds) {

        Booking booking = bookingService.findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking with ID " + bookingId + " does not exist.");
            throw new NotFoundException("bookingNotFoundError", String.valueOf(bookingId));
        }

        //en bokning kan bara ha en arbetsorder
        if (hasWorkOrder(bookingId)) {
            System.out.println("Booking " + bookingId + " already has a work order.");
            throw new BusinessRuleException("bookingAlreadyHasWorkOrder", String.valueOf(bookingId));
        }

        Mechanic mechanic = mechanicService.findMechanic(mechanicId);
        mechanicIsFoundOrThrowException(mechanic, mechanicId);

        //mekanikern får inte redan ha en arbetsorder samma dag som bokningen
        if (isMechanicBookedOnDate(mechanicId, booking.getDate(), bookingId)) {
            System.out.println("Mechanic " + mechanic.getName() +
                    " is already booked on " + booking.getDate() + ".");
            throw new BusinessRuleException("mechanicAlreadyBookedError",
                    mechanic.getName(), booking.getDate());
        }

        workOrderAtLeastOneServiceOrThrowException(serviceItemIds);

        for (int serviceItemId : serviceItemIds) {
            if (serviceItemService.findServiceItem(serviceItemId) == null) {
                System.out.println("Service item with ID " + serviceItemId + " does not exist.");
                throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
            }
        }

        //byggs med WorkOrderBuilder, på samma sätt som drop-in och utkast
        WorkOrderBuilder builder = new WorkOrderBuilder()
                .bookingId(bookingId)
                .mechanicId(mechanicId)
                .description(booking.getDescription());

        // Hämtar fordonet som är kopplat till bokningen.
        Vehicle vehicle = vehicleService.findVehicle(booking.getVehicleId());

        // Avbryter om fordonet saknas, så att en ofullständig arbetsorder inte skapas.
        vehicleIsFoundOrThrowException(vehicle, booking.getVehicleId());

        // Kopplar fordon och kund till arbetsordern.
        builder.vehicleId(vehicle.getId())
                .customerId(vehicle.getCustomerId());

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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public void removeServiceItemFromWorkOrder(WorkOrder workOrder, int serviceItemId) {
        // Kontrollerar att arbetsordern finns.
        if (workOrder == null) {
            throw new NotFoundException("workOrderDoesNotExist");
        }

        // Tjänster får bara tas bort innan arbetet har påbörjats.
        String status = workOrder.getStatus();
        if (!"DRAFT".equals(status) && !"CREATED".equals(status)) {
            System.out.println("Services cannot be removed after work has started.");
            throw new BusinessRuleException("servicesCannotBeRemoved");
        }

        // Tar bort tjänsten från arbetsordern och databasen.
        workOrder.removeServiceItem(serviceItemId);
        workOrderRepository.removeServiceItem(workOrder.getId(), serviceItemId);

        System.out.println("ServiceItem " + serviceItemId
                + " removed from workOrder " + workOrder.getId());
    }

    //Det här fungerar inte eftersom vi ju inte sparar datum för workorder.
    //Datum måste vara valt och får inte ligga bakåt i tiden
    private void validateWorkOrderDate(LocalDate date) {
        if (date == null) {
            throw new ValidationException("missingDateWarning");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new ValidationException("workOrderDateInPastError");
        }
    }

    // Kontrollera mekaniker
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public boolean isAllowedStatusTransition(WorkOrder workOrder, String newStatus) {
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

    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public void startWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        workOrderIsFoundOrThrowException(workOrder, workOrderId);

        // Kontrollerar statusövergången innan arbetet startar.
        if (!isAllowedStatusTransition(workOrder, "IN_PROGRESS")) {
            System.out.println("Work order cannot be started.");
            throw new BusinessRuleException("workOrderCannotBeStarted");
            //return;
        }

        // Hämtar mekanikern som är kopplad till arbetsordern.
        Mechanic mechanic = mechanicService.findMechanic(workOrder.getMechanicId());

        // Avbryter om mekanikern saknas.
        mechanicIsFoundOrThrowException(mechanic, workOrder.getMechanicId());

        mechanicAvailableOrThrowException(mechanic);

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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public void completeWorkOrder(int workOrderId) {
        WorkOrder workOrder = findWorkOrder(workOrderId);

        workOrderIsFoundOrThrowException(workOrder, workOrderId);

        // Kontrollerar statusövergången innan arbetet slutförs.
        if (!isAllowedStatusTransition(workOrder, "COMPLETED")) {
            System.out.println("Only work orders in progress can be completed.");
            throw new BusinessRuleException("onlyWorkOrderInProgressCanBeCompleted");
            //return;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public WorkOrder createComplaint(int originalWorkOrderId){
        return createComplaint(originalWorkOrderId, null);
    }

    //Alexander
    //samma som ovan, men reklamationens egen beskrivning (vad som är fel) sparas på den nya ordern
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public WorkOrder createComplaint(int originalWorkOrderId, String complaintDescription){
        WorkOrder original = findWorkOrder(originalWorkOrderId);

        workOrderIsFoundOrThrowException(original, originalWorkOrderId);

        // Kontrollerar att originalordern är slutförd.
        if (!"COMPLETED".equals(original.getStatus())) {
            throw new BusinessRuleException("complaintMusHaveStatusCompleted");
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public WorkOrder createDropInWorkOrder(int customerId,
                                           int vehicleId,
                                           int mechanicId,
                                           int... serviceItemIds) {

        Customer customer = customerService.findCustomer(customerId);
        customerIsFoundOrThrowException(customer, customerId);

        Vehicle vehicle = vehicleService.findVehicle(vehicleId);
        vehicleIsFoundOrThrowException(vehicle, vehicleId);
        vehicleBelongsToCustomerOrThrowException(vehicle, customer);

        Mechanic mechanic = mechanicService.findMechanic(mechanicId);
        mechanicIsFoundOrThrowException(mechanic, mechanicId);
        mechanicAvailableOrThrowException(mechanic);

        // Kontrollerar att drop-in-arbetsordern innehåller minst en tjänst.
        workOrderAtLeastOneServiceOrThrowException(serviceItemIds);

        for (int serviceItemId : serviceItemIds) {
            if (serviceItemService.findServiceItem(serviceItemId) == null) {
                System.out.println("Service item with ID " + serviceItemId +
                        " does not exist.");
                throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public WorkOrder createDraftWorkOrder(
            int customerId,
            int vehicleId,
            String description,
            int mechanicId,
            int... serviceItemIds) {

        // Kontrollerar att kunden och fordonet finns.
        Customer customer = customerService.findCustomer(customerId);
        customerIsFoundOrThrowException(customer, customerId);

        Vehicle vehicle = vehicleService.findVehicle(vehicleId);
        vehicleIsFoundOrThrowException(vehicle, vehicleId);

        // Säkerställer att fordonet tillhör den valda kunden.
         vehicleBelongsToCustomerOrThrowException(vehicle, customer);

        // Kontrollerar alla tjänster innan utkastet skapas.
        if (serviceItemIds != null) {
            for (int serviceItemId : serviceItemIds) {
                if (serviceItemService.findServiceItem(serviceItemId) == null) {
                    System.out.println("Service item does not exist: " + serviceItemId);
                    throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
                    //return null;
                }
            }
        }

        // Kontrollerar att den valda mekanikern finns.
        Mechanic mechanic = mechanicService.findMechanic(mechanicId);
        mechanicIsFoundOrThrowException(mechanic, mechanicId);

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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
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
            mechanicIsFoundOrThrowException(mechanic, mechanicId);
        }

        WorkOrder draft = workOrderRepository.findById(workOrderId);

        // Kontrollerar att ordern finns och fortfarande är en draft.
        if (draft == null) {
            throw new NotFoundException("workOrderNotFoundError", String.valueOf(workOrderId));
        }

        if (!"DRAFT".equals(draft.getStatus())) {
            throw new BusinessRuleException("workOrderNotADraftError", String.valueOf(workOrderId));
        }

        // Kontrollerar att kunden och fordonet finns.
        Customer customer = customerService.findCustomer(customerId);
        customerIsFoundOrThrowException(customer, customerId);

        Vehicle vehicle = vehicleService.findVehicle(vehicleId);
        vehicleIsFoundOrThrowException(vehicle, vehicleId);

        // Säkerställer att fordonet tillhör den valda kunden.
        vehicleBelongsToCustomerOrThrowException(vehicle, customer);

        // Kontrollerar alla tjänster innan utkastet ändras.
        if (serviceItemIds != null) {
            for (int serviceItemId : serviceItemIds) {
                if (serviceItemService.findServiceItem(serviceItemId) == null) {
                    System.out.println("Service item does not exist: " + serviceItemId);
                    throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
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

    @PreAuthorize("hasAnyRole('ADMIN', 'WORKSHOP')")
    public WorkOrder completeDraftWorkOrder(int workOrderId,
                                            int mechanicId,
                                            int... serviceItemsIds) {

        WorkOrder workOrder = workOrderRepository.findById(workOrderId);

        workOrderIsFoundOrThrowException(workOrder, workOrderId);

        if(!"DRAFT".equals(workOrder.getStatus())) {
            System.out.println("work order with ID " + workOrderId + " is not a draft");
            throw new BusinessRuleException("workOrderNotADraftError", String.valueOf(workOrderId));
            //return null;
        }

        if(workOrder.getCustomerId() <=0){
            System.out.println("Work order has no customer");
            throw new ValidationException("workOrderNoCustomerError");
            //return null;
        }

        if(workOrder.getVehicleId() <=0){
            System.out.println("Work order has no vehicle");
            throw new ValidationException("workOrderNoVehicleError");
        }

        workOrderAtLeastOneServiceOrThrowException(serviceItemsIds);

        Mechanic mechanic = mechanicService.findMechanic(mechanicId);
        mechanicIsFoundOrThrowException(mechanic, mechanicId);
        mechanicAvailableOrThrowException(mechanic);

        // Kontrollerar att utkastet får övergå till CREATED.
        if (!isAllowedStatusTransition(workOrder, "CREATED")) {
            System.out.println("Draft work order cannot be completed.");
            throw new BusinessRuleException("draftWorkOrderCannotBeCompleted");
            //return null;
        }

        // Ersätter utkastets gamla tjänster med tjänsterna från formuläret.
        workOrder.setServiceItemIds(new ArrayList<Integer>());
        workOrder.setServiceItemPrices(new java.util.HashMap<Integer, Double>());

        for (int serviceItemId : serviceItemsIds) {
            ServiceItem serviceItem = serviceItemService.findServiceItem(serviceItemId);

            serviceItemIsFoundOrThrowException(serviceItem, serviceItemId);

            workOrder.addServiceItem(serviceItemId, serviceItem.getPrice());
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

    //Metoder som hanterar exceptions
    private void mechanicAvailableOrThrowException(Mechanic mechanic){
        if(!mechanic.isAvailable()){
            System.out.println("Mechanic not available");
            throw new BusinessRuleException("mechanicNotAvailableError", mechanic.getName());
        }
    }

    private void vehicleBelongsToCustomerOrThrowException(Vehicle vehicle, Customer customer){
        if (vehicle.getCustomerId() != customer.getId()) {
            System.out.println("Vehicle does not belong to customer" + customer.getName());
            throw new BusinessRuleException("vehicleDoesNotBelongToCustomerError", String.valueOf(customer.getName()));
        }
    }

    private void workOrderAtLeastOneServiceOrThrowException(int... serviceItemIds){
        if (serviceItemIds == null || serviceItemIds.length == 0) {
            System.out.println("A work order must contain at least one service.");
            throw new BusinessRuleException("workOrderAtLeastOneServiceError");
        }
    }

    private void mechanicIsFoundOrThrowException(Mechanic mechanic, int mechanicId) {
        if (mechanic == null) {
            System.out.println("Mechanic does not exist: " + mechanicId);
            throw new NotFoundException("mechanicNotFoundError", String.valueOf(mechanicId));
        }
    }

    private void workOrderIsFoundOrThrowException(WorkOrder workOrder, int workOrderId) {
        if (workOrder == null) {
            System.out.println("work order with ID " + workOrderId+ " does not exist.");
            throw new NotFoundException("workOrderNotFoundError", String.valueOf(workOrderId));
        }
    }

    private void serviceItemIsFoundOrThrowException(ServiceItem serviceItem, int serviceItemId) {
        if (serviceItem == null) {
            System.out.println("service item with ID " + serviceItemId + " does not exist.");
            throw new NotFoundException("serviceItemNotFoundError", String.valueOf(serviceItemId));
        }
    }

    private void customerIsFoundOrThrowException(Customer customer, int customerId) {
        if (customer == null) {
            System.out.println("customer with ID " + customerId + " does not exist.");
            throw new NotFoundException("customerNotFoundError", String.valueOf(customerId));
        }
    }

    private void vehicleIsFoundOrThrowException(Vehicle vehicle, int vehicleId) {
        if (vehicle == null) {
            System.out.println("vehicle with ID " + vehicleId + " does not exist.");
            throw new NotFoundException("vehicleNotFoundError", String.valueOf(vehicleId));
        }
    }


}