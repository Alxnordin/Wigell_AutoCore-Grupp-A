package com.wac.autocore.controller;

// Importerar Factory Pattern för skapande av arbetsordrar.
import com.wac.autocore.factory.WorkOrderFactory;
import com.wac.autocore.factory.WorkOrderCreator;
import com.wac.autocore.factory.PlannedOrderCreator;
import com.wac.autocore.factory.DropInOrderCreator;
import com.wac.autocore.factory.WarrantyOrderCreator;
import com.wac.autocore.model.WorkOrderType;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.service.interf.WorkOrderService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.ErrorHandler;
import com.wac.autocore.view.UIComponents;
import com.wac.autocore.view.order.CreateWorkOrderView;
import com.wac.autocore.view.order.OrderDetailsView;
import com.wac.autocore.view.order.OrderListView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.text.MessageFormat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderController {

    private final BookingService bookingService;
    private final WorkOrderService workOrderService;
    private final MechanicService mechanicService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;
    private final InvoiceService invoiceService;
    private final ServicePackageService servicePackageService;

    private final WorkOrderFactory workOrderFactory;

    private final AutoCoreApplication app;

    private final OrderListView orderListView;

    private final LanguageManager languageManager =
            LanguageManager.getInstance();


    public OrderController(
            BookingService bookingService,
            WorkOrderService workOrderService,
            MechanicService mechanicService,
            ServiceItemService serviceItemService,
            ServicePackageService servicePackageService,
            VehicleService vehicleService,
            CustomerService customerService,
            InvoiceService invoiceService,
            AutoCoreApplication app,
            OrderListView orderListView) {

        this.bookingService = bookingService;
        this.workOrderService = workOrderService;
        this.mechanicService = mechanicService;
        this.serviceItemService = serviceItemService;
        this.servicePackageService = servicePackageService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.invoiceService = invoiceService;
        this.app = app;
        this.orderListView = orderListView;

        orderListView.setGetMechanicName(mechanicId -> getMechanicName(mechanicId));
        orderListView.setGetServiceItem(serviceItemId ->
                serviceItemService.findServiceItem(serviceItemId));

        // Registrerar vilken skapare som ansvarar för respektive arbetsorderstyp.
        this.workOrderFactory = new WorkOrderFactory(Arrays.asList(
                new PlannedOrderCreator(workOrderService, bookingService),
                new DropInOrderCreator(workOrderService),
                new WarrantyOrderCreator(workOrderService)
        ));

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

            detailsView.setOnMechanicChange(mechanic -> {
                workOrderService.updateMechanic(
                        workOrder,
                        mechanic.getId()
                );
            });


            detailsView.setOnDeleteService(serviceItem -> {

                // Tjänster får bara tas bort innan arbetsordern har startat.
                if (!"CREATED".equals(workOrder.getStatus())) {
                    showWarning(languageManager.getString(
                            "serviceCanOnlyBeRemovedBeforeWorkStarts"
                    ));
                    return;
                }


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
                    workOrderService.startWorkOrder(workOrder.getId());

                    // Uppdaterar objektet som vyn använder.
                    workOrder.setStatus("IN_PROGRESS");

                } else if ("COMPLETED".equals(newStatus)) {
                    workOrderService.completeWorkOrder(workOrder.getId());

                    // Uppdaterar objektet som vyn använder.
                    workOrder.setStatus("COMPLETED");
                }

                // Visar den nya statusen och uppdaterar knapparna.
                detailsView.updateStatus();
            });


            //Alexander
            //createInvoice kastar exception om fakturan inte kan skapas (t.ex. redan fakturerad),
            //felet visas då via ErrorHandler
            detailsView.setOnCreateInvoice(workOrderToInvoice -> {
                try {
                    invoiceService.createInvoice(workOrderToInvoice.getId(), null);
                    detailsView.showInvoiceCreatedPopup();
                } catch (Exception e) {
                    ErrorHandler.show(e);
                }
            });


            detailsView.setOnBack(() -> {
                refreshOrderList();
                app.showView(orderListView.getView());
            });


            app.showView(
                    detailsView.getView()
            );
        });


        orderListView.setOnCreateOrderType(
                this::openCreateWorkOrderView
        );

        // Kopplar Continue-knappen till funktionen som öppnar ett utkast.
        orderListView.setOnContinueDraft(this::openDraftForEditing);
    }

    private void openCreateWorkOrderView(String orderType) {

        CreateWorkOrderView createWorkOrderView =
                new CreateWorkOrderView(
                        orderType,
                        null
                );

        fillServiceComboBox(createWorkOrderView);


        // Översätter menyvalet till rätt ordertyp.
        WorkOrderType type;
        switch (orderType) {
            case "planned":
                type = WorkOrderType.PLANNED;
                break;
            case "dropIn":
                type = WorkOrderType.DROP_IN;
                break;
            case "warranty":
                type = WorkOrderType.WARRANTY;
                break;
            default:
                return;
        }

        // Låter respektive skapare konfigurera sin del av formuläret.
        WorkOrderCreator creator = workOrderFactory.getCreator(type);
        creator.configureView(createWorkOrderView);


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



            createWorkOrderView.getOriginalWorkOrderComboBox()
                    .setOnAction(event -> {

                        WorkOrder selectedWorkOrder =
                                createWorkOrderView.getOriginalWorkOrderComboBox().getValue();

                        if (selectedWorkOrder == null) {
                            return;
                        }

                        // Hämtar bokningen om arbetsordern har en sådan.
                        Booking originalBooking = null;
                        if (selectedWorkOrder.getBookingId() != null) {
                            originalBooking = bookingService.findBooking(
                                    selectedWorkOrder.getBookingId()
                            );
                        }

                        // Hämtar fordonet från bokningen eller arbetsordern.
                        int vehicleId = originalBooking != null
                                ? originalBooking.getVehicleId()
                                : selectedWorkOrder.getVehicleId();

                        Vehicle vehicle = vehicleId > 0
                                ? vehicleService.findVehicle(vehicleId)
                                : null;

                        // Hämtar kunden från arbetsordern, med fordonet som reserv.
                        int customerId = selectedWorkOrder.getCustomerId();
                        Customer customer = customerId > 0
                                ? customerService.findCustomer(customerId)
                                : null;

                        if (customer == null && vehicle != null) {
                            customer = customerService.findCustomer(
                                    vehicle.getCustomerId()
                            );
                        }

                        // Uppdaterar vald kund och fordon vid varje byte av originalorder.
                        createWorkOrderView.getVehicleComboBox().getItems().clear();
                        createWorkOrderView.getCustomerComboBox().getItems().clear();

                        if (vehicle != null) {
                            createWorkOrderView.getVehicleComboBox().getItems().add(vehicle);
                            createWorkOrderView.getVehicleComboBox().setValue(vehicle);
                        } else {
                            createWorkOrderView.getVehicleComboBox().setValue(null);
                        }

                        if (customer != null) {
                            createWorkOrderView.getCustomerComboBox().getItems().add(customer);
                            createWorkOrderView.getCustomerComboBox().setValue(customer);
                        } else {
                            createWorkOrderView.getCustomerComboBox().setValue(null);
                        }

                        // Hämtar mekanikern utan att riskera null-fel.
                        Mechanic mechanic = selectedWorkOrder.getMechanicId() > 0
                                ? mechanicService.findMechanic(
                                selectedWorkOrder.getMechanicId())
                                : null;

                        if (mechanic != null) {
                            createWorkOrderView.getMechanicComboBox().setValue(mechanic);
                        } else {
                            createWorkOrderView.getMechanicComboBox().setValue(null);
                        }

                        // Använder arbetsorderns beskrivning först.
                        String description = selectedWorkOrder.getDescription();

                        // Hämtar bokningens text endast om arbetsordern saknar beskrivning.
                        if ((description == null || description.trim().isEmpty())
                                && originalBooking != null) {
                            description = originalBooking.getDescription();
                        }

                        createWorkOrderView.getDescriptionField().setText(
                                description != null ? description : ""
                        );

                        // Uppdaterar översikten med den valda originalordern.
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


        //Spara arbetsorder som utkast
        createWorkOrderView.getSaveDraftButton().setOnAction(event -> {

            Customer customer = createWorkOrderView.getCustomerComboBox().getValue();
            Vehicle vehicle = createWorkOrderView.getVehicleComboBox().getValue();
            String description = createWorkOrderView.getDescriptionField().getText();

            if (customer == null || vehicle == null) {
                showWarning(languageManager.getString("selectCustomerAndVehicleWarning"));
                return;
            }


            // Hämtar tjänste-ID:n från formuläret.
            int[] serviceIds = createWorkOrderView.getServicesTable()
                    .getItems()
                    .stream()
                    .mapToInt(ServiceItem::getId)
                    .toArray();



            // Hämtar den valda mekanikern från formuläret.
            Mechanic mechanic = createWorkOrderView.getMechanicComboBox().getValue();

            // Kontrollerar att alla obligatoriska val är gjorda.
            if (customer == null || vehicle == null || mechanic == null) {
                showWarning(languageManager.getString("selectMechanicWarning"));
                return;
            }

            /// Sparar utkastet med vald kund, fordon, mekaniker och tjänster.
            WorkOrder draft = workOrderService.createDraftWorkOrder(
                    customer.getId(),
                    vehicle.getId(),
                    description,
                    mechanic.getId(),
                    serviceIds
            );

            if (draft != null) {
                showOrderList();
            }
        });

        // Create-knappen
        createWorkOrderView.getNextButton()
                .setOnAction(event -> createOrder(createWorkOrderView, orderType));

        // Lägg till tjänst
        createWorkOrderView.getAddServiceButton().setOnAction(event -> {
            addServiceToWorkOrder(createWorkOrderView);
        });

        app.showView(createWorkOrderView.getView());
    }



    // Öppnar ett sparat utkast och förbereder formuläret för redigering.
    private void openDraftForEditing(WorkOrder draft) {
        if (draft == null || !"DRAFT".equals(draft.getStatus())) {
            return;
        }

        CreateWorkOrderView view = new CreateWorkOrderView("draft", null);

        // Hämtar kund och fordon från det sparade utkastet.
        Customer customer = customerService.findCustomer(draft.getCustomerId());
        Vehicle vehicle = vehicleService.findVehicle(draft.getVehicleId());

        if (customer == null || vehicle == null) {
            showWarning(languageManager.getString("draftDataMissing"));
            return;
        }

        // Förifyller kund och fordon.
        view.getCustomerComboBox().getItems().setAll(customer);
        view.getCustomerComboBox().setValue(customer);

        view.getVehicleComboBox().getItems().setAll(vehicle);
        view.getVehicleComboBox().setValue(vehicle);

        // Förifyller beskrivningen.
        view.getDescriptionField().setText(draft.getDescription());

        // Hämtar mekaniker och tillgängliga tjänster.
        view.getMechanicComboBox().getItems().setAll(
                mechanicService.getMechanics()
        );
        // Återställer mekanikern som sparats på utkastet.
        if (draft.getMechanicId() > 0) {
            for (Mechanic mechanic : view.getMechanicComboBox().getItems()) {
                if (mechanic.getId() == draft.getMechanicId()) {
                    view.getMechanicComboBox().setValue(mechanic);
                    break;
                }
            }
        }

        fillServiceComboBox(view);

        // Visar tjänsterna som redan sparats i utkastet.
        List<ServiceItem> services =
                workOrderService.getServicesForWorkOrder(draft);

        view.getServicesTable().getItems().setAll(services);

        // Uppdaterar summeringen.
        view.showSummary(
                formatTotalTime(services),
                formatTotalPrice(services)
        );

        // Kopplar tillbaka-knappen till orderlistan.
        view.getBackButton().setOnAction(event -> showOrderList());

        // Återanvänder befintlig logik för att lägga till tjänster.
        view.getAddServiceButton().setOnAction(
                event -> addServiceToWorkOrder(view)
        );

        // Sparar ändringarna på det befintliga utkastet.
        view.getSaveDraftButton().setOnAction(event -> {
            Customer selectedCustomer =
                    view.getCustomerComboBox().getValue();

            Vehicle selectedVehicle =
                    view.getVehicleComboBox().getValue();

            String description =
                    view.getDescriptionField().getText();

            // Hämtar mekanikern som användaren valt.
            Mechanic selectedMechanic =
                    view.getMechanicComboBox().getValue();

            if (selectedCustomer == null || selectedVehicle == null) {
                showWarning(
                        languageManager.getString(
                                "selectCustomerAndVehicleWarning"
                        )
                );
                return;
            }

            // Hämtar alla tjänster som finns kvar i formuläret.
            int[] serviceIds = view.getServicesTable().getItems()
                    .stream()
                    .mapToInt(ServiceItem::getId)
                    .toArray();

            // Uppdaterar utkastet med samma ID i stället för att skapa ett nytt.
            WorkOrder updatedDraft = workOrderService.updateDraftWorkOrder(
                    draft.getId(),
                    selectedCustomer.getId(),
                    selectedVehicle.getId(),
                    description,
                    selectedMechanic == null ? null : selectedMechanic.getId(),
                    serviceIds
            );

            // Går tillbaka till listan först när sparandet lyckats.
            if (updatedDraft != null) {
                showOrderList();
            }
        });

        // Färdigställer utkastet när användaren väljer Next.
        view.getNextButton().setOnAction(event -> {
            // Kontrollerar att klicket når händelsehanteraren.
            Mechanic mechanic = view.getMechanicComboBox().getValue();

            if (mechanic == null) {
                showWarning(languageManager.getString("selectMechanicWarning"));
                return;
            }

            int[] serviceIds = view.getServicesTable().getItems()
                    .stream()
                    .mapToInt(ServiceItem::getId)
                    .toArray();

            if (serviceIds.length == 0) {
                showWarning(languageManager.getString("selectServiceWarning"));
                return;
            }

            // Färdigställer det befintliga utkastet med dess nuvarande ID.
            WorkOrder completedDraft = workOrderService.completeDraftWorkOrder(
                    draft.getId(),
                    mechanic.getId(),
                    serviceIds
            );


            if (completedDraft != null) {
                // Visar bekräftelse när utkastet har färdigställts.
                showInformation(
                        MessageFormat.format(
                                languageManager.getString("workOrderCreatedInfo"),
                                String.valueOf(completedDraft.getId())
                        )
                );

                showOrderList();
            } else {
                // Visar felruta om utkastet inte kunde färdigställas.
                showWarning(languageManager.getString("mechanicNotAvailable"));
            }

        });

        // Visar det förifyllda formuläret.
        app.showView(view.getView());
    }



    // Öppna planned order från en bokning
    public void openCreateWorkOrderView(Booking booking) {

        CreateWorkOrderView createWorkOrderView =
                new CreateWorkOrderView(
                        "planned",
                        booking
                );

        fillServiceComboBox(createWorkOrderView);


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
        createWorkOrderView.getNextButton().setOnAction(event -> createOrder(createWorkOrderView, "planned"));

        app.showView(
                createWorkOrderView.getView()
        );
    }



    // Fabriken väljer skapare, medan controllern behåller gemensam hantering.
    private void createOrder(CreateWorkOrderView view, String orderType) {
        WorkOrderType type;

        // Översätter de befintliga menyvärdena till enum-värden.
        switch (orderType) {
            case "planned":
                type = WorkOrderType.PLANNED;
                break;
            case "dropIn":
                type = WorkOrderType.DROP_IN;
                break;
            case "warranty":
                type = WorkOrderType.WARRANTY;
                break;
            default:
                return;
        }

        // Hämtar rätt skapare och låter den validera och skapa arbetsordern.
        WorkOrderCreator creator = workOrderFactory.getCreator(type);
        WorkOrder workOrder = creator.createOrder(view);

        if (workOrder == null) {
            return;
        }

        // Behåller samma bekräftelser som tidigare.
        if (workOrder.isComplaint()) {
            showInformation(MessageFormat.format(
                    languageManager.getString("warrantyOrderCreatedInfo"),
                    String.valueOf(workOrder.getId()),
                    String.valueOf(workOrder.getOriginalWorkOrderId())
            ));
        } else {
            showInformation(MessageFormat.format(
                    languageManager.getString("workOrderCreatedInfo"),
                    String.valueOf(workOrder.getId())
            ));
        }

        // Uppdaterar listan och återgår till den.
        showOrderList();
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
    //Alexander
    //rullistan "välj tjänster", först de enskilda tjänsterna sedan servicepaketen
    //ett paket utan tjänster visas inte eftersom det inte finns något att lägga till
    private void fillServiceComboBox(CreateWorkOrderView view) {
        view.getServiceComboBox().getItems().addAll(serviceItemService.getServiceItems());

        for (ServicePackage servicePackage : servicePackageService.getServicePackages()) {
            if (servicePackage.getServiceCount()> 0){
                view.getServiceComboBox().getItems().add(servicePackage);
            }
        }
    }

    //Alexander
    //Composite: en tjänst visas sig själv & ett paket visar alla sina tjänster
    private void addServiceToWorkOrder(CreateWorkOrderView view) {
        ServiceComponent selected = view.getServiceComboBox().getValue();
        List<ServiceItem> selectedServices = view.getServicesTable().getItems();

        if (selected == null) {
            return;
        }

        for (ServiceItem serviceItem : selected.getServiceItems()) {
            if (!containsService(selectedServices, serviceItem.getId())) {
                selectedServices.add(serviceItem);
            }
        }

        view.getServiceComboBox().setValue(null);

        view.showSummary(
                formatTotalTime(selectedServices),
                formatTotalPrice(selectedServices)
        );
    }

    //Alexander
    //true om tjänsten redan finns blan de valda tjänsterna(jämför med ID)
    private boolean containsService(List<ServiceItem> services, int serviceItemId) {
        for (ServiceItem serviceItem : services) {
            if (serviceItem.getId()== serviceItemId) {
                return true;
            }
        }
        return false;
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
//        Alert alert = new Alert(
//                Alert.AlertType.WARNING, message);
//        alert.setHeaderText(null);
        Alert alert = UIComponents.createWarningAlert(Alert.AlertType.WARNING,
        message, languageManager.getString("headingAlertWarning"));
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

    private String getMechanicName(int mechanicId) {
        Mechanic mechanic = mechanicService.findMechanic(mechanicId);

        if (mechanic != null) {
            return mechanic.getName();
        }

        return "-";
    }
}