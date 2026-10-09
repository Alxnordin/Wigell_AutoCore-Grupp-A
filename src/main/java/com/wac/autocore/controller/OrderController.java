package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.dao.*;
import com.wac.autocore.model.*;
import com.wac.autocore.service.*;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.order.CreateWorkOrderView;
import com.wac.autocore.view.order.OrderDetailsView;
import com.wac.autocore.view.order.OrderListView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.text.MessageFormat;

import java.util.List;
import java.util.stream.Collectors;

public class OrderController {

    private final BookingService bookingService;
    private final WorkOrderService workOrderService;
    private final MechanicService mechanicService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;

    private final AutoCoreApplication app;

    private final OrderListView orderListView;

    private final LanguageManager languageManager =
            LanguageManager.getInstance();


    public OrderController(
            BookingService bookingService,
            WorkOrderService workOrderService,
            MechanicService mechanicService,
            ServiceItemService serviceItemService,
            VehicleService vehicleService,
            CustomerService customerService,
            AutoCoreApplication app,
            OrderListView orderListView) {

        this.bookingService = bookingService;
        this.workOrderService = workOrderService;
        this.mechanicService = mechanicService;
        this.serviceItemService = serviceItemService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.app = app;
        this.orderListView = orderListView;

        wireEvents();
        refreshOrderList();

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    refreshOrderList();
                }
        );
    }


    private void wireEvents() {

        // Tillbaka
        orderListView.getBackButton().setOnAction(
                actionEvent -> app.showMainMenu()
        );


        // Visa detaljer för vald arbetsorder
        orderListView.setOnViewOrder(workOrder -> {

            //Alexander
            //drop-in saknar bokning. Då hämtas fordonet direkt från arbetsordern i stället för från bokningen.
            Booking booking = null;
            if (workOrder.getBookingId() != null) {
                booking = bookingService.findBooking(workOrder.getBookingId());
            }

            int vehicleId = booking != null ? booking.getVehicleId() : workOrder.getVehicleId();

            Vehicle vehicle =
                    vehicleService.findVehicle(
                            vehicleId
                    );

            if (vehicle == null) {
                return;
            }


            Customer customer =
                    customerService.findCustomer(
                            vehicle.getCustomerId()
                    );

            if (customer == null) {
                return;
            }


            //lediga mekaniker räknas fram utifrån bokningens datum. Utan bokning visas alla mekaniker.
            List<Mechanic> mechanics = booking != null
                    ? workOrderService.getAvailableMechanics(booking, workOrder)
                    : mechanicService.getMechanics();

            List<ServiceItem> orderServices =
                    workOrderService.getServicesForWorkOrder(
                            workOrder
                    );


            OrderDetailsView detailsView =
                    new OrderDetailsView(
                            workOrder,
                            booking,
                            vehicle,
                            customer,
                            mechanics,
                            orderServices
                    );


            detailsView.setOnDeleteService(serviceItem -> {

                System.out.println(
                        "Antal services före delete "
                                + workOrder.getServiceItemIds().size()
                );

                if (workOrder.getServiceItemIds().size() <= 1) {

                    showWarning(
                            languageManager.getString(
                                    "lastServiceWarning"
                            )
                    );

                    return;
                }

                workOrderService.removeServiceItemFromWorkOrder(
                        workOrder,
                        serviceItem.getId()
                );

                detailsView.removeServiceFromTable(
                        serviceItem
                );
            });


            detailsView.showSummary(
                    formatTotalTime(orderServices),
                    formatTotalPrice(orderServices)
            );


            detailsView.setOnStatusChange(newStatus -> {

                if ("IN_PROGRESS".equals(newStatus)) {

                    workOrderService.startWorkOrder(
                            workOrder.getId()
                    );

                } else if ("COMPLETED".equals(newStatus)) {

                    workOrderService.completeWorkOrder(
                            workOrder.getId()
                    );
                }

                detailsView.updateStatus();
            });


            detailsView.setOnBack(() -> {

                app.showView(
                        orderListView.getView()
                );
            });


            app.showView(
                    detailsView.getView()
            );
        });


        orderListView.setOnCreateOrderType(
                this::openCreateWorkOrderView
        );
    }

    private void openCreateWorkOrderView(String orderType) {

        CreateWorkOrderView createWorkOrderView =
                new CreateWorkOrderView(
                        orderType,
                        null
                );

        if ("dropIn".equals(orderType) || "planned".equals(orderType)) {

            createWorkOrderView.getCustomerComboBox()
                    .getItems()
                    .addAll(
                            customerService.getAllCustomers()
                    );

            createWorkOrderView.getVehicleComboBox()
                    .getItems()
                    .addAll(
                            vehicleService.getVehicles()
                    );

            createWorkOrderView.getMechanicComboBox()
                    .getItems()
                    .addAll(
                            mechanicService.getMechanics()
                    );
        }



        // PLANNED
        if ("planned".equals(orderType)) {

            createWorkOrderView.getBookingComboBox()
                    .getItems()
                    .addAll(
                            bookingService.getBookings()
                    );
        }


        // WARRANTY
        if ("warranty".equals(orderType)) {

            // Hämtar alla slutförda arbetsordrar
            List<WorkOrder> completedWorkOrders =
                    workOrderService.findAllWorkOrders()
                            .stream()
                            .filter(workOrder ->
                                    "COMPLETED".equals(
                                            workOrder.getStatus()
                                    )
                            )
                            .collect(Collectors.toList());


            createWorkOrderView
                    .getOriginalWorkOrderComboBox()
                    .getItems()
                    .addAll(
                            completedWorkOrders
                    );


            // När användaren väljer en ursprunglig arbetsorder
            createWorkOrderView
                    .getOriginalWorkOrderComboBox()
                    .setOnAction(event -> {

                        WorkOrder selectedWorkOrder =
                                createWorkOrderView
                                        .getOriginalWorkOrderComboBox()
                                        .getValue();


                        if (selectedWorkOrder == null) {
                            return;
                        }


                        // Debug - visar vilka ID:n originalordern innehåller
                        System.out.println(
                                "=== WARRANTY DEBUG ==="
                        );

                        System.out.println(
                                "WorkOrder ID: "
                                        + selectedWorkOrder.getId()
                        );

                        System.out.println(
                                "Booking ID: "
                                        + selectedWorkOrder.getBookingId()
                        );

                        System.out.println(
                                "Customer ID: "
                                        + selectedWorkOrder.getCustomerId()
                        );

                        System.out.println(
                                "Vehicle ID: "
                                        + selectedWorkOrder.getVehicleId()
                        );

                        System.out.println(
                                "Mechanic ID: "
                                        + selectedWorkOrder.getMechanicId()
                        );


                        // Hämta den ursprungliga bokningen
                        Booking originalBooking = null;

                        if (selectedWorkOrder.getBookingId() != null) {

                            originalBooking =
                                    bookingService.findBooking(
                                            selectedWorkOrder
                                                    .getBookingId()
                                    );
                        }


                        Vehicle vehicle = null;
                        Customer customer = null;


                        // Kund och fordon hämtas från originalbokningen
                        if (originalBooking != null) {

                            vehicle =
                                    vehicleService.findVehicle(
                                            originalBooking.getVehicleId()
                                    );


                            if (vehicle != null) {

                                customer =
                                        customerService.findCustomer(
                                                vehicle.getCustomerId()
                                        );
                            }
                        }


                        // Fyll i Vehicle
                        if (vehicle != null) {

                            createWorkOrderView
                                    .getVehicleComboBox()
                                    .getItems()
                                    .clear();

                            createWorkOrderView
                                    .getVehicleComboBox()
                                    .getItems()
                                    .add(vehicle);

                            createWorkOrderView
                                    .getVehicleComboBox()
                                    .setValue(vehicle);
                        }


                        // Fyll i Customer
                        if (customer != null) {

                            createWorkOrderView
                                    .getCustomerComboBox()
                                    .getItems()
                                    .clear();

                            createWorkOrderView
                                    .getCustomerComboBox()
                                    .getItems()
                                    .add(customer);

                            createWorkOrderView
                                    .getCustomerComboBox()
                                    .setValue(customer);
                        }


                        // Hämta mekanikern från originalordern
                        Mechanic mechanic =
                                mechanicService.findMechanic(
                                        selectedWorkOrder
                                                .getMechanicId()
                                );


                        // Visa originalbokningens beskrivning
                        if (originalBooking != null) {

                            createWorkOrderView
                                    .getDescriptionField()
                                    .setText(
                                            originalBooking.getDescription()
                                    );
                        }


                        // Visa information om originalordern
                        createWorkOrderView.showOriginalWorkOrder(
                                selectedWorkOrder,
                                customer,
                                vehicle,
                                mechanic
                        );
                    });
        }


        // PLANNED - när användaren väljer en bokning
        createWorkOrderView
                .getBookingComboBox()
                .setOnAction(event -> {

                    Booking selectedBooking =
                            createWorkOrderView
                                    .getBookingComboBox()
                                    .getValue();


                    fillFromBooking(
                            createWorkOrderView,
                            selectedBooking
                    );


                    updateAvailableMechanics(
                            createWorkOrderView,
                            selectedBooking
                    );
                });


        // Tillbaka
        createWorkOrderView
                .getBackButton()
                .setOnAction(event -> {

                    app.showView(orderListView.getView());});

        //Alexander
        //utkast används inte just nu, så knappen Spara som utkast döljs
        hideSaveDraftButton(createWorkOrderView);

        //Alexander
        //Create-knappen skapar ordern med rätt metod i WorkOrderService beroende på ordertyp
        createWorkOrderView.getNextButton().setOnAction(event -> createOrder(createWorkOrderView, orderType));

        // Lägg till tjänst
        createWorkOrderView.getAddServiceButton().setOnAction(event -> {
            addServiceToWorkOrder(createWorkOrderView);
        });

        app.showView(createWorkOrderView.getView());
    }


    // Öppna planned order från en bokning
    public void openCreateWorkOrderView(Booking booking) {

        CreateWorkOrderView createWorkOrderView =
                new CreateWorkOrderView(
                        "planned",
                        booking
                );


        createWorkOrderView
                .getBookingComboBox()
                .setValue(booking);


        fillFromBooking(
                createWorkOrderView,
                booking
        );


        updateAvailableMechanics(
                createWorkOrderView,
                booking
        );


        createWorkOrderView
                .getBookingComboBox()
                .getItems()
                .addAll(
                        bookingService.getBookings()
                );


        createWorkOrderView
                .getBookingComboBox()
                .setValue(booking);


        createWorkOrderView
                .getBookingComboBox()
                .setOnAction(event -> {

                    Booking selectedBooking =
                            createWorkOrderView
                                    .getBookingComboBox()
                                    .getValue();


                    fillFromBooking(
                            createWorkOrderView,
                            selectedBooking
                    );


                    updateAvailableMechanics(
                            createWorkOrderView,
                            selectedBooking
                    );
                });


        createWorkOrderView
                .getBackButton()
                .setOnAction(event -> {

                    app.showView(
                            orderListView.getView()
                    );
                });


        createWorkOrderView
                .getAddServiceButton()
                .setOnAction(event -> {

                    addServiceToWorkOrder(
                            createWorkOrderView
                    );
                });

        //Alexander
        //Create-knappen när man kommer från "Skapa arbetsorder" på en bokning
        hideSaveDraftButton(createWorkOrderView);
        createWorkOrderView.getNextButton().setOnAction(event -> createOrder(createWorkOrderView, "planned"));


        app.showView(
                createWorkOrderView.getView()
        );
    }


    //Alexander
    //skapar ordern med den metod i WorkOrderService som hör till ordertypen.
    //Varje metod kontrollerar formuläret först och visar en varning om något saknas.
    private void createOrder(CreateWorkOrderView view, String orderType) {
        WorkOrder workOrder;

        if ("planned".equals(orderType)) {
            workOrder = createPlannedOrder(view);
        } else if ("dropIn".equals(orderType)) {
            workOrder = createDropInOrder(view);
        } else if ("warranty".equals(orderType)) {
            workOrder = createWarrantyOrder(view);
        } else {
            return;
        }

        if (workOrder == null) {
            return;
        }

        if (workOrder.isComplaint()) {
            showInformation(MessageFormat.format(languageManager.getString("warrantyOrderCreatedInfo"),
                    String.valueOf(workOrder.getId()), String.valueOf(workOrder.getOriginalWorkOrderId())));
        } else {
            showInformation(MessageFormat.format(languageManager.getString("workOrderCreatedInfo"),
                    String.valueOf(workOrder.getId())));
        }
        showOrderList();
    }

    //Planned: bokning, mekaniker och minst en tjänst -> createWorkOrder
    private WorkOrder createPlannedOrder(CreateWorkOrderView view) {
        Booking booking = view.getBookingComboBox().getValue();
        Mechanic mechanic = view.getMechanicComboBox().getValue();
        int[] serviceItemIds = getServiceItemIds(view);

        if (booking == null) {
            showWarning(languageManager.getString("selectBookingWarning"));
            return null;
        }
        if (mechanic == null) {
            showWarning(languageManager.getString("selectMechanicWarning"));
            return null;
        }
        if (serviceItemIds.length == 0) {
            showWarning(languageManager.getString("noServiceSelectedWarning"));
            return null;
        }

        WorkOrder workOrder = workOrderService.createWorkOrder(booking.getId(), mechanic.getId(), serviceItemIds);

        if (workOrder == null) {
            showWarning(languageManager.getString("workOrderNotCreatedWarning"));
        }
        return workOrder;
    }

    //Drop-in: kund, fordon (som tillhör kunden), mekaniker och minst en tjänst -> createDropInWorkOrder
    private WorkOrder createDropInOrder(CreateWorkOrderView view) {
        Customer customer = view.getCustomerComboBox().getValue();
        Vehicle vehicle = view.getVehicleComboBox().getValue();
        Mechanic mechanic = view.getMechanicComboBox().getValue();
        int[] serviceItemIds = getServiceItemIds(view);

        if (customer == null || vehicle == null) {
            showWarning(languageManager.getString("selectCustomerAndVehicleWarning"));
            return null;
        }
        if (vehicle.getCustomerId() != customer.getId()) {
            showWarning(languageManager.getString("vehicleNotOwnedByCustomerWarning"));
            return null;
        }
        if (mechanic == null) {
            showWarning(languageManager.getString("selectMechanicWarning"));
            return null;
        }
        if (serviceItemIds.length == 0) {
            showWarning(languageManager.getString("noServiceSelectedWarning"));
            return null;
        }

        WorkOrder workOrder = workOrderService.createDropInWorkOrder(
                customer.getId(), vehicle.getId(), mechanic.getId(), serviceItemIds);

        if (workOrder == null) {
            showWarning(languageManager.getString("workOrderNotCreatedWarning"));
        }
        return workOrder;
    }

    //Warranty: en slutförd ursprunglig order -> createComplaint (Prototype: originalet kopieras)
    private WorkOrder createWarrantyOrder(CreateWorkOrderView view) {
        WorkOrder original = view.getOriginalWorkOrderComboBox().getValue();

        if (original == null) {
            showWarning(languageManager.getString("selectOriginalWorkOrderWarning"));
            return null;
        }

        WorkOrder complaint = workOrderService.createComplaint(
                original.getId(), view.getWarrantyDescriptionField().getText());

        if (complaint == null) {
            showWarning(languageManager.getString("workOrderNotCreatedWarning"));
        }
        return complaint;
    }

    //ID:n för tjänsterna i formulärets tabell
    private int[] getServiceItemIds(CreateWorkOrderView view) {
        return view.getServicesTable().getItems().stream()
                .mapToInt(ServiceItem::getId)
                .toArray();
    }

    //Spara som utkast är dold tills gruppen bestämmer hur utkast ska göras klart
    private void hideSaveDraftButton(CreateWorkOrderView view) {
        view.getSaveDraftButton().setVisible(false);
        view.getSaveDraftButton().setManaged(false);
    }

    //tillbaka till orderlistan, med den nya ordern i listan
    private void showOrderList() {
        refreshOrderList();
        app.showView(orderListView.getView());
    }


    private void fillFromBooking(
            CreateWorkOrderView view,
            Booking booking) {

        if (booking == null) {
            return;
        }


        // Datum
        view.getDatePicker()
                .setValue(
                        booking.getDate()
                );


        // Beskrivning
        view.getDescriptionField()
                .setText(
                        booking.getDescription()
                );


        // Hämta fordonet från bookingens vehicleId
        Vehicle vehicle =
                vehicleService.findVehicle(
                        booking.getVehicleId()
                );


        if (vehicle != null) {

            view.getVehicleComboBox()
                    .getItems()
                    .clear();

            view.getVehicleComboBox()
                    .getItems()
                    .add(vehicle);

            view.getVehicleComboBox()
                    .setValue(vehicle);


            Customer customer =
                    customerService.findCustomer(
                            vehicle.getCustomerId()
                    );


            if (customer != null) {

                view.getCustomerComboBox()
                        .getItems()
                        .clear();

                view.getCustomerComboBox()
                        .getItems()
                        .add(customer);

                view.getCustomerComboBox()
                        .setValue(customer);
            }
        }


        // Hämta tjänster från bokningen
        view.getServicesTable()
                .getItems()
                .clear();


        view.setBookingServiceItemIds(
                booking.getServiceItemIds()
        );


        for (int serviceItemId :
                booking.getServiceItemIds()) {

            ServiceItem serviceItem =
                    serviceItemService.findServiceItem(
                            serviceItemId
                    );


            if (serviceItem != null) {

                view.getServicesTable()
                        .getItems()
                        .add(serviceItem);
            }
        }


        view.showSummary(
                formatTotalTime(
                        view.getServicesTable().getItems()
                ),
                formatTotalPrice(
                        view.getServicesTable().getItems()
                )
        );
    }


    private void updateAvailableMechanics(
            CreateWorkOrderView view,
            Booking booking) {

        if (booking == null) {

            view.getMechanicComboBox()
                    .getItems()
                    .clear();

            return;
        }


        List<Mechanic> allMechanics =
                mechanicService.getMechanics();


        view.getMechanicComboBox()
                .getItems()
                .clear();


        for (Mechanic mechanic :
                allMechanics) {

            if (!workOrderService.isMechanicBookedOnDate(
                    mechanic.getId(),
                    booking.getDate(),
                    booking.getId()
            )) {

                view.getMechanicComboBox()
                        .getItems()
                        .add(mechanic);
            }
        }


        view.getMechanicComboBox()
                .getSelectionModel()
                .clearSelection();
    }


    private void addServiceToWorkOrder(
            CreateWorkOrderView view) {

        ServiceItem selectedService =
                view.getServiceComboBox().getValue();


        List<ServiceItem> selectedServices =
                view.getServicesTable().getItems();


        if (selectedService == null) {
            return;
        }


        boolean alreadyExists =
                selectedServices.stream()
                        .anyMatch(service ->
                                service.getId()
                                        == selectedService.getId()
                        );


        if (!alreadyExists) {

            selectedServices.add(
                    selectedService
            );


            view.getServiceComboBox()
                    .setValue(null);


            view.showSummary(
                    formatTotalTime(selectedServices),
                    formatTotalPrice(selectedServices)
            );
        }
    }


    // Fyller i formuläret från en bokning
    // Används när man kommer från bokningslistan
    public void prefillFromBooking(Booking booking) {

        StringBuilder serviceItemIds =
                new StringBuilder();


        for (int serviceItemId :
                booking.getServiceItemIds()) {

            if (serviceItemIds.length() > 0) {
                serviceItemIds.append(", ");
            }

            serviceItemIds.append(
                    serviceItemId
            );
        }
    }


    // Hantera service-ID:n
    private int[] parseServiceItemIds(String text) {

        if (text == null ||
                text.trim().isEmpty()) {

            return new int[0];
        }


        String[] parts =
                text.split(",");


        int[] ids =
                new int[parts.length];


        for (int i = 0; i < parts.length; i++) {

            ids[i] =
                    Integer.parseInt(
                            parts[i].trim()
                    );
        }


        return ids;
    }


    // Uppdatera arbetsorder-tabellen
    private void refreshOrderList() {

        orderListView
                .getOrderTable()
                .getItems()
                .setAll(
                        workOrderService
                                .findAllWorkOrders()
                );
    }


    // Varningsruta
    private void showWarning(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING,
                        message
                );

        alert.setHeaderText(null);
        alert.showAndWait();
    }


    //Alexander
    //bekräftelse, samma som i VehicleController
    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


    public Parent getOrderListPane() {

        return orderListView.getView();
    }


    private String formatTotalTime(
            List<ServiceItem> services) {

        if (services.isEmpty()) {
            return "--";
        }


        return bookingService.calculateTotalMinutes(
                services
        ) + " min";
    }


    private String formatTotalPrice(
            List<ServiceItem> services) {

        if (services.isEmpty()) {
            return "--";
        }


        return String.format(
                "%,.0f kr",
                bookingService.calculateTotalPrice(
                        services
                )
        );
    }
}