package com.wac.autocore.service.interf;

import com.wac.autocore.model.*;

import java.time.LocalDate;
import java.util.List;

public interface WorkOrderService {

    List<WorkOrder> findAllWorkOrders();

    WorkOrder findWorkOrder(int id);

    void updateMechanic(WorkOrder workOrder, int mechanicId);

    boolean hasWorkOrder(int bookingId);

    void removeServiceItemFromWorkOrder(WorkOrder workOrder, int serviceItemId);

     boolean isMechanicBookedOnDate(int mechanicId,
                                              LocalDate date, int excludingBookingId);

     WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     int... serviceItemIds);

     List<Mechanic> getAvailableMechanics(
             Booking currentBooking, WorkOrder currentWorkOrder);

    List<ServiceItem> getServicesForWorkOrder(WorkOrder workOrder);

    void startWorkOrder(int workOrderId);

    void completeWorkOrder(int workOrderId);

    WorkOrder createComplaint(int originalWorkOrderId);

    WorkOrder createComplaint(int originalWorkOrderId, String complaintDescription);


    WorkOrder createDropInWorkOrder(int customerId,
                                           int vehicleId,
                                           int mechanicId,
                                           int... serviceItemIds);

    // Skapar ett nytt utkast med valda tjänster.
    WorkOrder createDraftWorkOrder(
            int customerId,
            int vehicleId,
            String description,
            int mechanicId,
            int... serviceItemIds
    );

    // Sparar ändringar på en befintlig draft.
    WorkOrder updateDraftWorkOrder(
            int workorderId,
            int customerId,
            int vehicleId,
            String description,
            Integer mechanicId,
            int... serviceItemIds);

   WorkOrder completeDraftWorkOrder(int workOrderId,
                                            int mechanicId,
                                            int... serviceItemsIds);


}
