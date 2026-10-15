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
    //Ska inte vara Mockito här alls??

    @Test
    //"arbetsorder ska kunna registreras"
    //Create, C i CRUD
    void createWorkOrder() {
        WorkOrder workOrder = workOrderService.createWorkOrder(49, 3, 1);
        WorkOrder savedWorkOrder = workOrderService.findWorkOrder(workOrder.getId());
        assertEquals(workOrder.getId(), savedWorkOrder.getId());
    }

    //Read, R i CRUD
    @Test
    void findWorkOrderById() {}

    //Ha med? Måste ju göra start för att kunna complete
    @Test
    void startWorkOrder() {}

    //"arbetsorder ska kunna uppdateras"
    //Update, U i CRUD
    @Test
    void updateMechanic() {}

    @Test
    //"arbetsorder ska kunna avslutas"
    void completeWorkOrder() {}

    //Delete, D i CRUD
    @Test
    void remove_deleteServiceItemFromWorkOrder() {}


    //Skippa? Borde väl räcka med de ovanför?
    @Test
    void findAllWorkOrders() {}

    @Test
    void deleteServiceItem() {}

    @Test
    void loadServiceItems() {}

    @Test
    void saveServiceItem() {}

    @Test
    void hasWorkOrder() {}

    @Test
    void createComplaint() {}

    @Test
    void createDropInWorkOrder() {}

    @Test
    void createDraftWorkOrder() {}

    @Test
    void completeDraftWorkOrder() {}

}
