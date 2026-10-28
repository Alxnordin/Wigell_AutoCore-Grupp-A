import com.wac.autocore.dao.*;
import com.wac.autocore.exception.AutoCoreException;
import com.wac.autocore.exception.BusinessRuleException;
import com.wac.autocore.exception.NotFoundException;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.logging.ExceptionLogger;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.repository.*;
import com.wac.autocore.service.impl.*;
import com.wac.autocore.service.interf.*;
import com.wac.autocore.exception.ValidationException;
import com.wac.autocore.util.LanguageManager;
import org.slf4j.MDC;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final LanguageManager languageManager = LanguageManager.getInstance();
    //private static final GarageSystem garageSystem = new GarageSystem();

    public static void main(String[] args) {

        //Alexander
        //det fristående programmet har ingen inloggning, därför märks alla loggrader med user="fristående-program"
        //så att det syns i loggen att händelsen kom från det fristående programmet
        MDC.put("user", "fristående-program");

        WorkOrderRepository workOrderRepository = new WorkOrderDAO();
        CustomerRepository customerRepository = new CustomerDAO();
        ServiceItemRepository serviceItemRepository = new ServiceItemDAO();
        ServicePackageRepository servicePackageRepository = new ServicePackageDAO();
        BookingRepository bookingRepository = new BookingDAO();
        MechanicRepository mechanicRepository = new MechanicDAO();
        VehicleRepository vehicleRepository = new VehicleDAO();

        MechanicService mechanicService = new MechanicServiceImpl(mechanicRepository);
        CustomerService customerService = new CustomerServiceImpl(customerRepository);
        VehicleService vehicleService = new VehicleServiceImpl(vehicleRepository, customerService);
        ServiceItemService serviceItemService = new ServiceItemServiceImpl(serviceItemRepository);
        ServicePackageService servicePackageService =
                new ServicePackageServiceImpl(servicePackageRepository, serviceItemService);

        BookingService bookingService =
                new BookingServiceImpl(bookingRepository, vehicleService, serviceItemService);
        WorkOrderService workOrderService =
                new WorkOrderServiceImpl(workOrderRepository, bookingService,
                        mechanicService, serviceItemService,
                        customerService, vehicleService);

        boolean running = true;

        printHeader();

        while (running) {

            printMenu();

            int choice = readInt("Select option: ");

            switch (choice) {

                case 1:
                    createCustomer(customerService);
                    break;

                case 2:
                    createWorkOrder(workOrderService);
                    break;

                case 3:
                    startWorkOrder(workOrderService);
                    break;

                case 0:
                    running = false;
                    break;

                default:
                    System.out.println("Unknown menu option.");
                    break;
            }
        }

        System.out.println("\nShutting down Wigell AutoCore...");
        System.out.println("Goodbye!");

        scanner.close();
    }

    private static void printHeader() {
        System.out.println("===========================================");
        System.out.println("            WIGELL AUTOCORE");
        System.out.println("         A Wigell Group Company");
        System.out.println("===========================================");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=============== MAIN MENU =================");
        System.out.println(" 1. Create customers");
        System.out.println(" 2. Create work order");
        System.out.println(" 3. Start work order");
        System.out.println(" 0. Exit");
        System.out.println("===========================================");
    }

    private static void createCustomer(CustomerService customerService) {

        System.out.println("\n=== CREATE CUSTOMER ===");

        System.out.print("First name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.println("Skriv ja om kunden är vip eller nej om kunden inte är vip");
        String vipInput = scanner.nextLine();
        boolean vip;

        if (vipInput.equalsIgnoreCase("ja")) {
            vip = true;
        } else {
            vip = false;
        }

        try {
            Customer customer = customerService.createCustomer(firstName, lastName, phone, email, vip);
            System.out.println("Customer is now registered:\n" + customer);

        } catch (ValidationException validationError) {
            handleError(validationError);

        } catch (BusinessRuleException businessRuleError) {
            handleError(businessRuleError);

        } catch (NotFoundException notFoundMessage){
            handleError(notFoundMessage);

        } catch (Exception technicalError) {
            handleError(technicalError);
        }
    }

    private static void createWorkOrder(WorkOrderService workOrderService) {

        System.out.println("\n=== CREATE WORK ORDER ===");

        int bookingId = readInt("Booking ID: ");
        int mechanicId = readInt("Mechanic ID: ");

        System.out.println("\nEnter service IDs separated by comma.");
        System.out.println("Example: 1,3,4");
        System.out.print("Services: ");

        String input = scanner.nextLine();

        String[] parts = input.split(",");
        int[] serviceItemIds = new int[parts.length];

        try {

            for (int i = 0; i < parts.length; i++) {
                serviceItemIds[i] = Integer.parseInt(parts[i].trim());
            }

            WorkOrder workOrder = workOrderService.createWorkOrder(bookingId, mechanicId, serviceItemIds);
            System.out.println("Workorder:\n" + workOrder);

        } catch (NumberFormatException e) {
            System.out.println("Invalid service ID.");

        } catch (BusinessRuleException businessRuleError) {
            handleError(businessRuleError);

        } catch (NotFoundException notFoundError)   {
            handleError(notFoundError);

        } catch (Exception technicalError) {
            handleError(technicalError);
        }
    }

    private static void startWorkOrder(WorkOrderService workOrderService) {

        System.out.println("\n=== START WORK ORDER ===");

        int workOrderId = readInt("Work order ID: ");

        try {
            workOrderService.startWorkOrder(workOrderId);
            System.out.println("Work order " + workOrderId + " started.");

        } catch (NotFoundException notFoundError) {
            handleError(notFoundError);

        } catch (BusinessRuleException businessRuleError) {
            handleError(businessRuleError);

        } catch (Exception technicalError) {
            handleError(technicalError);
        }

    }

    //Alexander
    //loggar felet med samma ExceptionLogger som JavaFX-appen och skriver sedan ett begripligt meddelande
    //tekniska fel t.ex. databasfel visas bara med felkod, detaljerna finns i loggfilen
    private static void handleError(Exception exception) {
        String errorId = ExceptionLogger.log(exception);

        if (errorId != null) {
            System.out.println(MessageFormat.format(
                    languageManager.getString("technicalErrorWithReference"), errorId));
        } else if (exception instanceof AutoCoreException) {
            getAndPrintErrorMessage((AutoCoreException) exception);
        }
    }

    private static void getAndPrintErrorMessage(AutoCoreException exception) {
        String exceptionMessage =
                languageManager.getString(exception.getMessageKey());

        exceptionMessage = MessageFormat.format(exceptionMessage, exception.getArgs());

        System.out.println(exceptionMessage);
    }

    private static int readInt(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }


//    private static void createBooking() {
//
//        System.out.println();
//        System.out.println("=== CREATE BOOKING ===");
//
//        //garageSystem.showVehicles();
//
//        int vehicleId = readInt("Vehicle ID: ");
//
//        LocalDate date = readDate("Date (YYYY-MM-DD): ");
//
//        System.out.print("Description: ");
//        String description = scanner.nextLine();
//
//        garageSystem.createBooking(
//                vehicleId,
//                date,
//                description
//        );
//    }


//    private static void completeWorkOrder() {
//
//        System.out.println();
//        System.out.println("=== COMPLETE WORK ORDER ===");
//
//        garageSystem.showWorkOrders();
//
//        int workOrderId = readInt("Work order ID: ");
//
//        garageSystem.completeWorkOrder(workOrderId);
//    }

    private static LocalDate readDate(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine();

            try {
                return LocalDate.parse(input);

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use format YYYY-MM-DD.");
            }
        }
    }
}