package com.wac.autocore.service.impl;

import com.wac.autocore.dao.*;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.repository.WorkOrderRepository;
import com.wac.autocore.service.interf.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkOrderServiceIntegrationTest {

    private WorkOrderRepository workOrderRepository;

    private BookingService bookingService;
    private MechanicService mechanicService;
    private ServiceItemService serviceItemService;
    private CustomerService customerService;
    private VehicleService vehicleService;
    private WorkOrderService workOrderService;

    public WorkOrderServiceIntegrationTest() {

        workOrderRepository = new WorkOrderDAO();

        CustomerDAO customerDAO = new CustomerDAO();
        VehicleDAO vehicleDAO = new VehicleDAO();
        BookingDAO bookingDAO = new BookingDAO();
        MechanicDAO mechanicDAO = new MechanicDAO();
        ServiceItemDAO serviceItemDAO = new ServiceItemDAO();

        customerService = new CustomerServiceImpl(customerDAO);
        vehicleService = new VehicleServiceImpl(vehicleDAO, customerService);
        mechanicService = new MechanicServiceImpl(mechanicDAO);
        serviceItemService = new ServiceItemServiceImpl(serviceItemDAO);
        bookingService = new BookingServiceImpl(bookingDAO, vehicleService, serviceItemService);

        workOrderService = new WorkOrderServiceImpl(workOrderRepository,
                bookingService, mechanicService,
                serviceItemService, customerService, vehicleService);
    }

    //*** OM SERVICEKLASS - > DAO -> DATABAS (integrationstest) *******

    @Test
    //"arbetsorder ska kunna registreras"
    void createWorkOrder() {
        WorkOrder workOrder = workOrderService.createWorkOrder(24, 4, 1);
        WorkOrder savedWorkOrder = workOrderService.findWorkOrder(workOrder.getId());
        assertEquals(workOrder.getId(), savedWorkOrder.getId());
    }

    //"arbetsorder ska kunna uppdateras"
    @Test
    void updateMechanic() {
        WorkOrder workOrder = workOrderService.findWorkOrder(39);
        workOrderService.updateMechanic(workOrder, 2);
        WorkOrder updatedWorkOrder = workOrderService.findWorkOrder(39);
        assertEquals(2, updatedWorkOrder.getMechanicId());
    }

    @Test
    //"arbetsorder ska kunna avslutas"
    void completeWorkOrder() {
       workOrderService.startWorkOrder(21);
       workOrderService.completeWorkOrder(21);
       WorkOrder updatedWorkOrder = workOrderService.findWorkOrder(21);
       assertEquals("COMPLETED",  updatedWorkOrder.getStatus());
    }






    //Skippa? Borde väl räcka med de ovanför?
    //Read, R i CRUD
//    @Test
//    void findWorkOrderById() {}
//
//    //Ha med? Måste ju göra start för att kunna complete
//    @Test
//    void startWorkOrder() {}
//    //Delete, D i CRUD
//    @Test
//    void remove_deleteServiceItemFromWorkOrder() {}
//
//    @Test
//    void findAllWorkOrders() {}
//
//    @Test
//    void deleteServiceItem() {}
//
//    @Test
//    void loadServiceItems() {}
//
//    @Test
//    void saveServiceItem() {}
//
//    @Test
//    void hasWorkOrder() {}
//
//    @Test
//    void createComplaint() {}
//
//    @Test
//    void createDropInWorkOrder() {}
//
//    @Test
//    void createDraftWorkOrder() {}
//
//    @Test
//    void completeDraftWorkOrder() {}

}
