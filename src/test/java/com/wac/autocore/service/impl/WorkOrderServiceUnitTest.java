package com.wac.autocore.service.impl;

import com.wac.autocore.dao.WorkOrderDAO;
import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.repository.WorkOrderRepository;
import com.wac.autocore.service.interf.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WorkOrderServiceUnitTest {

    private WorkOrderRepository workOrderRepository;
    private BookingService bookingService;
    private MechanicService mechanicService;
    private ServiceItemService serviceItemService;
    private CustomerService customerService;
    private VehicleService vehicleService;

    private WorkOrderService workOrderService;

    @BeforeEach
    public void setUp() {

       workOrderRepository = mock(WorkOrderDAO.class);

        bookingService = mock(BookingService.class);
        mechanicService = mock(MechanicService.class);
        serviceItemService = mock(ServiceItemService.class);
        customerService = mock(CustomerService.class);
        vehicleService = mock(VehicleService.class);

        workOrderService = new WorkOrderServiceImpl(workOrderRepository, bookingService,
                mechanicService, serviceItemService, customerService, vehicleService);
    }

    //********** AFFÄRSREGLER (enhetstest) *******

    @Test
    void shouldNotCreateComplaint_WhenWorkOrderStatusNotIsCompleted() {
        WorkOrder workOrder = new WorkOrder(1, 1, 1);
        workOrder.setStatus("CREATED");

        when(workOrderRepository.findAll()).thenReturn(Arrays.asList(workOrder));

        assertThrows(BusinessRuleException.class, () ->
                workOrderService.createComplaint(1, "Test description"));
    }

    @Test
    void shouldCreateComplaint_WhenWorkOrderStatusIsCompleted() {
        WorkOrder workOrder = new WorkOrder(1, 1, 1);
        workOrder.setStatus("COMPLETED");

        when(workOrderRepository.findAll()).thenReturn(Arrays.asList(workOrder));

        assertDoesNotThrow(() ->
                workOrderService.createComplaint(1, "Test description"));
    }

    @Test
    void shouldThrowException_WhenWorkOrderIdDoesNotExist() {
        WorkOrder workOrder = new WorkOrder(2, 2, 2);
        workOrder.setId(15);

        when(workOrderRepository.findAll()).thenReturn(Arrays.asList(workOrder));

        assertThrows(NotFoundException.class, () ->
                workOrderService.createComplaint(2, "Test description"));
    }

    @Test
    void shouldThrowException_WhenMechanicIsAlreadyBookedOnDate() {

        LocalDate date = LocalDate.of(2026, 10, 20);

        Booking booking = mock(Booking.class);
        when(booking.getDate()).thenReturn(date);
        when(bookingService.findBooking(1)).thenReturn(booking);

        Mechanic mechanic = mock(Mechanic.class);
        when(mechanic.getName()).thenReturn("Test mechanic");
        when(mechanicService.findMechanic(2)).thenReturn(mechanic);

        WorkOrder existingWorkOrder = mock(WorkOrder.class);
        when(existingWorkOrder.getBookingId()).thenReturn(99);
        when(existingWorkOrder.getMechanicId()).thenReturn(2);

        Booking existingBooking = mock(Booking.class);
        when(existingBooking.getDate()).thenReturn(date);
        when(bookingService.findBooking(99)).thenReturn(existingBooking);

        when(workOrderRepository.findAll())
                .thenReturn(Arrays.asList(existingWorkOrder));

        assertThrows(BusinessRuleException.class, () ->
                workOrderService.createWorkOrder(1, 2, 10));
    }

    @Test
    void shouldChangeStatusToInProgress_WhenWorkOrderIsStarted() {
        WorkOrder workOrder = new WorkOrder(1, 1, 1);
        workOrder.setStatus("CREATED");

        Mechanic mechanic = mock(Mechanic.class);

        when(workOrderRepository.findAll()).thenReturn(Arrays.asList(workOrder));
        when(mechanicService.findMechanic(1)).thenReturn(mechanic);

        workOrderService.startWorkOrder(1);

        assertEquals("IN_PROGRESS", workOrder.getStatus());
    }


//    @Test
//    void shouldThrowException_WhenWorkOrderDateIsBeforeTodaysDate() {
//      LocalDate workOrderStartDate = LocalDate.now().minusDays(1);
//        assertThrows(WorkOrderStartDateBeforeTodaysDateException.class, () ->
//                workOrderService.validateWorkOrderStartDate(workOrderStartDate));
//    }

//    @Test
//    void shouldNotThrowException_WhenWorkOrderDateIsAfterTodaysDate() {
//        LocalDate workOrderStartDate = LocalDate.now().plusDays(1);
//        assertDoesNotThrow(() -> validateWorkOrderStartDate(workOrderStartDate));
//    }

//    @Test
//    void shouldNotThrowException_WhenWorkOrderDateIsTodaysDate() {
//        LocalDate workOrderStartDate = LocalDate.now();
//        assertDoesNotThrow(() -> validateWorkOrderStartDate(workOrderStartDate));
//    }


}
