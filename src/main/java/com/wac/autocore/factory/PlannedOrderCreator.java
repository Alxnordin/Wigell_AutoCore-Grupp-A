package com.wac.autocore.factory;

import com.wac.autocore.model.*;
import com.wac.autocore.service.BookingService;
import com.wac.autocore.service.WorkOrderService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.order.CreateWorkOrderView;
import javafx.scene.control.Alert;

public class PlannedOrderCreator implements WorkOrderCreator{

    private final WorkOrderService workOrderService;
    private final BookingService bookingService;
    private final LanguageManager languageManager = LanguageManager.getInstance();

    public PlannedOrderCreator(WorkOrderService workOrderService, BookingService bookingService) {
        //Återanvänder projektets befintliga service
        this.workOrderService = workOrderService;
        this.bookingService = bookingService;
    }

    @Override
    public WorkOrderType getType() {
        // Anger vilken ordertyp fabriken ska koppla till skaparen.
        return WorkOrderType.PLANNED;
    }

    @Override
    public void configureView(CreateWorkOrderView view) {
        // Hämtar bokningarna som kan användas för en planerad arbetsorder.
        view.getBookingComboBox()
                .getItems()
                .setAll(bookingService.getBookings());
    }

    @Override
    public WorkOrder createOrder(CreateWorkOrderView view) {
        // Hämtar användarens val från formuläret.
        Booking booking = view.getBookingComboBox().getValue();
        Mechanic mechanic = view.getMechanicComboBox().getValue();

        int[] serviceItemIds = view.getServicesTable()
                .getItems()
                .stream()
                .mapToInt(ServiceItem::getId)
                .toArray();

        // Kontrollerar att alla obligatoriska val finns.
        if (booking == null) {
            showWarning("selectBookingWarning");
            return null;
        }

        if (mechanic == null) {
            showWarning("selectMechanicWarning");
            return null;
        }

        if (serviceItemIds.length == 0) {
            showWarning("noServiceSelectedWarning");
            return null;
        }

        // Anropar servicelagret för att skapa arbetsordern.
        WorkOrder workOrder = workOrderService.createWorkOrder(
                booking.getId(),
                mechanic.getId(),
                serviceItemIds
        );

        if (workOrder == null) {
            showWarning("workOrderNotCreatedWarning");
        }

        return workOrder;
    }

    private void showWarning(String messageKey) {
        // Hämtar varningstexten på det språk användaren valt.
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                languageManager.getString(messageKey)
        );

        alert.setHeaderText(null);
        alert.showAndWait();
    }
}