package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//Klass som ska visa enskild faktura
public class InvoiceDetailsView {

    private final Parent root;
    private Label title;
    //private Label subtitle;

    private TableView<String> invoiceTableView;
    Label invoiceID;
    Label invoiceDate;
    //Label paidOrNot;
    Label customerNameLabel;
    Label customerPhoneLabel;
    Label vehicleLabel;
    Label mechanicNameLabel;

    private TableColumn<String, String> serviceColumn;
    private TableColumn<String, String> priceColumn;
    private TableColumn<String, String> discountColumn;
    private TableColumn<String, String> totalAmount;
    Label companyNameLabel;
    Label companyAddressLabel1;
    Label companyAddressLabel2;
    Label organizationNumberLabel;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceDetailsView(Parent root) {

        VBox box = UIComponents.createVBoxForViews();
        title = new Label(languageManager.getString(""));
        HBox titleBox = UIComponents.createPageTitle(title, "");



        this.root = root;


    }




}
