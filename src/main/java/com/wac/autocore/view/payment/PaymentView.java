package com.wac.autocore.view.payment;

import com.wac.autocore.model.Payment;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;


//UI för att visa fakturor och registrera betalningar
public class PaymentView {

    private final Parent root;

    private Label title;
    private Label subtitle;


    private TableView<Payment> paymentTable;

    private TableColumn<Payment, Integer> paymentIdColumn;
    private TableColumn<Payment, Integer> invoiceIdColumn;
    private TableColumn<Payment, Double> amountColumn;
    private TableColumn<Payment, String> paymentTypeColumn;
    private TableColumn<Payment, String> dateColumn;
    private TableColumn<Payment, String> statusColumn;

    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public PaymentView() {
        VBox box = UIComponents.createVBoxForViews();

        title = new Label(languageManager.getString("paymentTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-credit-card");

        subtitle = UIComponents.createSubtitle(
                languageManager.getString("paymentSubtitle"));

        // Skapar tabellen innan den läggs till i layouten.
        createPaymentTable();

        backButton = UIComponents.createBackButton(
                languageManager.getString("backButton"));

        // Lägger till rubrik, historiktabell och tillbaka-knapp.
        box.getChildren().addAll(titleBox, subtitle, paymentTable, backButton);

        // Tabellen får växa och använda tillgängligt utrymme.
        VBox.setVgrow(paymentTable, Priority.ALWAYS);

        this.root = box;

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> changeTextAllComponents());
    }

    // Skapar kolumnerna för betalningshistoriken.
    private void createPaymentTable() {
        paymentTable = UIComponents.createTable();

        paymentIdColumn = new TableColumn<>(
                languageManager.getString("paymentIdColumn"));
        paymentIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        invoiceIdColumn = new TableColumn<>(
                languageManager.getString("invoiceIdInTable"));
        invoiceIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("invoiceId"));

        amountColumn = new TableColumn<>(
                languageManager.getString("amountInTable"));
        amountColumn.setCellValueFactory(
                new PropertyValueFactory<>("amount"));

        paymentTypeColumn = new TableColumn<>(
                languageManager.getString("paymentTypeInTable"));
        paymentTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("paymentType"));

        dateColumn = new TableColumn<>(
                languageManager.getString("dateInTable"));
        dateColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getPaymentDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        statusColumn = new TableColumn<>(
                languageManager.getString("statusColumn"));
        statusColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().isSuccessful()
                                ? languageManager.getString("completedStatus")
                                : languageManager.getString("failedStatus")));

        paymentTable.getColumns().setAll(
                paymentIdColumn,
                invoiceIdColumn,
                amountColumn,
                paymentTypeColumn,
                dateColumn,
                statusColumn
        );

        // Låter kolumnerna dela på tabellens tillgängliga bredd.
        paymentTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Ger controllern möjlighet att fylla tabellen med betalningar.
    public TableView<Payment> getPaymentTable() {
        return paymentTable;
    }

    // Returnerar tillbaka-knappen så att controllern kan koppla händelsen.
    public Button getBackButton() {
        return backButton;
    }

    // Returnerar rotvyn som ska visas i programmets huvudfönster.
    public Parent getView() {
        return root;
    }


    public void changeTextAllComponents() {
        title.setText(languageManager.getString("paymentTitle"));
        subtitle.setText(languageManager.getString("paymentSubtitle"));
        backButton.setText(languageManager.getString("backButton"));

        paymentIdColumn.setText(languageManager.getString("paymentIdColumn"));
        invoiceIdColumn.setText(languageManager.getString("invoiceIdInTable"));
        amountColumn.setText(languageManager.getString("amountInTable"));
        paymentTypeColumn.setText(languageManager.getString("paymentTypeInTable"));
        dateColumn.setText(languageManager.getString("dateInTable"));
        statusColumn.setText(languageManager.getString("statusColumn"));

        // Uppdaterar även statusvärdena på svenska eller engelska.
        paymentTable.refresh();
    }
}