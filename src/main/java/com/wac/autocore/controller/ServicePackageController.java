package com.wac.autocore.controller;

import com.wac.autocore.AutoCoreApplication;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePackage;
import com.wac.autocore.service.ServiceItemService;
import com.wac.autocore.service.ServicePackageService;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.service.ServicePackageView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;


//Alexander
//kopplar sektionen "Servicepaket" (ServicePackageView) till ServicePackageService:
//skapa paket, visa paketets tjänster, lägga till och ta bort tjänster i ett paket.
public class ServicePackageController {
    private final ServicePackageService servicePackageService;
    private final ServiceItemService serviceItemService;
    private final AutoCoreApplication app;
    private final ServicePackageView servicePackageView;

    private final LanguageManager languageManager = LanguageManager.getInstance();


    public ServicePackageController(ServicePackageService servicePackageService, ServiceItemService serviceItemService,
                                    AutoCoreApplication app, ServicePackageView servicePackageView) {
        this.servicePackageService = servicePackageService;
        this.serviceItemService = serviceItemService;
        this.app = app;
        this.servicePackageView = servicePackageView;
        wireEvents();

        //tjänsterna som kan läggas till i ett paket
        servicePackageView.getServiceComboBox().getItems().addAll(serviceItemService.getServiceItems());
        refreshPackageList();

        //vid språkbyte hämtas paketen på nytt så at belopp skrivs enligt andra språket
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> refreshPackageList());
    }

    private void wireEvents(){
        //när ett paket markeras visas dess tjänster
        servicePackageView.getPackageTable().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldPackage, newPackage) -> showServicesForSelectedPackage());

        servicePackageView.getCreatePackageButton().setOnAction(actionEvent -> createPackage());
        servicePackageView.getAddServiceButton().setOnAction(actionEvent -> addServiceToSelectedPackage());
        servicePackageView.getRemoveServiceButton().setOnAction(actionEvent -> removeServiceFromSelectedPackage());
    }

    //skapar ett nytt tomt paket med namnet i textfältet
    private void createPackage() {
        String name = servicePackageView.getPackageNameField().getText();

        //ServicePackageService kontrollerar namnet (tomt eller redan upptaget ger null)
        ServicePackage servicePackage = servicePackageService.createServicePackage(name);
        if (servicePackage == null) {
            showWarning(languageManager.getString("packageNotCreatedWarning"));
            return;
        }

        servicePackageView.getPackageNameField().clear();
        refreshPackageList();
        selectPackage(servicePackage.getId());
    }

    //visar tjänsterna i det paket som är markerat
    private void showServicesForSelectedPackage() {
        ServicePackage servicePackage = servicePackageView.getPackageTable().getSelectionModel()
                .getSelectedItem();

        if (servicePackage == null) {
            servicePackageView.getPackageServicesTable().getItems().clear();
            servicePackageView.setServiceEditingDisabled(true);
            servicePackageView.setPackageSelected(false);
            return;
        }

        //Composite- paketet lämnar själv ut sina tjänster
        servicePackageView.getPackageServicesTable().getItems().setAll(servicePackage
                .getServiceItems());
        servicePackageView.setServiceEditingDisabled(false);
        servicePackageView.setPackageSelected(true);
    }

    //lägg till den valda tjänsten i det markerade paketet
    private void addServiceToSelectedPackage() {
        ServicePackage servicePackage = servicePackageView.getPackageTable()
                .getSelectionModel().getSelectedItem();
        ServiceItem service = servicePackageView.getServiceComboBox()
                .getValue();

        if (servicePackage == null || service == null) {
            showWarning(languageManager.getString("selectPackageAndServiceWarning"));
            return;
        }

        if (servicePackage.containsServiceItem(service.getId())) {
            showWarning(languageManager.getString("serviceAlreadyInPackageWarning"));
            return;
        }

        //ServicePackageService kontrollerar reglerna och sparar i databasen
        if (!servicePackageService.addServiceToPackage(servicePackage.getId(),
                service.getId())) {
            showWarning(languageManager.getString("packageChangeFailedWarning"));

        }

        servicePackageView.getServiceComboBox().setValue(null);
        refreshPackageList();
    }

    //ta bort den markerade tjänsten från det markerade pakete
    private void removeServiceFromSelectedPackage(){
        ServicePackage servicePackage = servicePackageView.getPackageTable()
                .getSelectionModel().getSelectedItem();
        ServiceItem service = servicePackageView.getPackageServicesTable()
                .getSelectionModel().getSelectedItem();

        if (servicePackage == null || service == null) {
            showWarning(languageManager.getString("selectServiceToRemoveFromPackageWarning"));
            return;
        }
        if (!servicePackageService.removeServiceFromPackage(servicePackage.getId(),
                service.getId())) {
            showWarning(languageManager.getString("packageChangeFailedWarning"));
        }

        refreshPackageList();
    }

    //hämta paketen på nytt, paketet som var markerat markeras igen,
    //så att dess tjänster och summor visas direkt efter en ändring
    private void refreshPackageList() {
        ServicePackage selectedPackage = servicePackageView.getPackageTable()
                .getSelectionModel().getSelectedItem();

        servicePackageView.getPackageTable().getItems().setAll(servicePackageService.servicePackages());

        if (selectedPackage != null) {
            selectPackage(selectedPackage.getId());
        }
        showServicesForSelectedPackage();
    }

    private void selectPackage(int servicePackageId) {
        for (ServicePackage servicePackage : servicePackageView.getPackageTable().getItems()) {
            if (servicePackage.getId()== servicePackageId) {
                servicePackageView.getPackageTable().getSelectionModel()
                        .select(servicePackage);
            }
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public Parent getView() {
        return servicePackageView.getView();
    }

}
