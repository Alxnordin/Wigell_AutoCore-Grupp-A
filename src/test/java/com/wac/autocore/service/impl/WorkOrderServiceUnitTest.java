package com.wac.autocore.service.impl;

import com.wac.autocore.dao.WorkOrderDAO;
import com.wac.autocore.service.interf.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class WorkOrderServiceUnitTest {

    private WorkOrderDAO workOrderDAO;

    private BookingService bookingService;
    private MechanicService mechanicService;
    private ServiceItemService serviceItemService;
    private CustomerService customerService;
    private VehicleService vehicleService;

    private WorkOrderService workOrderService;

    @BeforeEach
    public void setUp() {

       workOrderDAO = mock(WorkOrderDAO.class);

        bookingService = mock(BookingService.class);
        mechanicService = mock(MechanicService.class);
        serviceItemService = mock(ServiceItemService.class);
        customerService = mock(CustomerService.class);
        vehicleService = mock(VehicleService.class);

        workOrderService = new WorkOrderServiceImpl(workOrderDAO, bookingService,
                mechanicService, serviceItemService, customerService, vehicleService);
    }

    //********** AFFÄRSREGLER (enhetstest) *******
    //Kan finnas behov av Mockito
    @Test
    void shouldThrowException_WhenWorkOrderDateIsBeforeTodaysDate() {
      LocalDate workOrderStartDate = LocalDate.now().minusDays(1);
//        assertThrows(WorkOrderStartDateBeforeTodaysDateException.class, () ->
//                workOrderService.validateWorkOrderStartDate(workOrderStartDate));
    }

    @Test
    void shouldNotThrowException_WhenWorkOrderDateIsAfterTodaysDate() {
        LocalDate workOrderStartDate = LocalDate.now().plusDays(1);
//        assertDoesNotThrow(() -> validateWorkOrderStartDate(workOrderStartDate));
    }

    @Test
    void shouldNotThrowException_WhenWorkOrderDateIsTodaysDate() {
        LocalDate workOrderStartDate = LocalDate.now();
//        assertDoesNotThrow(() -> validateWorkOrderStartDate(workOrderStartDate));
    }



}
