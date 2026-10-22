
package com.wac.autocore.view.payment;

import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

public class RegisterPaymentDialog {

    private final LanguageManager languageManager = LanguageManager.getInstance();

    private final Dialog<PaymentDetails> dialog = new Dialog<>();

    private Label titleLabel;

    private final int invoiceId;
    private final double remainingAmount;

    private Label amountTitleLabel;
    private Label amountDescriptionLabel;

    private Label invoiceLabel;
    private Label remainingAmountLabel;

    private Label dateLabel;
    private DatePicker paymentDatePicker;

    private Label paymentTypeLabel;
    private ComboBox<String> paymentTypeComboBox;

    private ButtonType registerButtonType;
    private ButtonType cancelButtonType;

    public RegisterPaymentDialog(int invoiceId, double remainingAmount) {
        this.invoiceId = invoiceId;
        this.remainingAmount = remainingAmount;

        createDialog();

        // Uppdaterar dialogens texter vid språkbyte.
        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> {
                    changeTextAllComponents();
                }
        );
    }


    // Bygger dialogens innehåll och knappar.
    private void createDialog() {

        // Återanvänder sidrubriken med ikon från UIComponents.
        titleLabel = new Label(languageManager.getString("registerPaymentTitle"));
        titleLabel.getStyleClass().add("payment-dialog-title");

        HBox titleBox = UIComponents.createPageTitle(titleLabel, "fa-credit-card");

        dialog.setTitle(languageManager.getString("registerPaymentTitle"));
        dialog.setResizable(false);

        registerButtonType = new ButtonType(
                languageManager.getString("processPaymentButton"),
                ButtonBar.ButtonData.OK_DONE
        );

        cancelButtonType = new ButtonType(
                languageManager.getString("cancelButton"),
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        dialog.getDialogPane().getButtonTypes().setAll(
                cancelButtonType,
                registerButtonType
        );

        createFields();

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("form-section");

        // Visar fakturanumret separat ovanför beloppsrutan.
        VBox invoiceInfo = new VBox(6);
        invoiceInfo.getChildren().add(invoiceLabel);

        // Samlar beloppet i en tydlig, grönmarkerad ruta.
        VBox amountCard = new VBox(4);
        amountCard.getStyleClass().add("payment-amount-card");
        amountCard.getChildren().addAll(
                amountTitleLabel,
                remainingAmountLabel,
                amountDescriptionLabel
        );

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(12);

        // Placerar datum och belopp bredvid varandra.
        form.add(dateLabel, 0, 0);

        form.add(paymentDatePicker, 0, 1);

        // Betalningstypen placeras på en egen rad.
        form.add(paymentTypeLabel, 0, 2);
        form.add(paymentTypeComboBox, 0, 3);

        // Fälten anpassas efter tillgängligt utrymme.
        paymentDatePicker.setMaxWidth(Double.MAX_VALUE);
        paymentTypeComboBox.setMaxWidth(Double.MAX_VALUE);

        // Visar rubrik, fakturanummer, beloppssammanfattning och formulär i ordning.
        content.getChildren().addAll(
                titleBox,
                invoiceInfo,
                amountCard,
                form
        );

        // Ger själva dialogrutan en egen styling.
        dialog.getDialogPane().getStyleClass().add("payment-dialog");
        dialog.getDialogPane().setContent(content);

        // Kontrollerar att betalningsdialogen hittar den gemensamma CSS-filen.
        java.net.URL stylesheet =
                getClass().getResource("/style.css");

        if (stylesheet != null) {
            System.out.println("CSS hittades: " + stylesheet);

            dialog.getDialogPane().getStylesheets()
                    .add(stylesheet.toExternalForm());
        }

        // Återanvänder projektets befintliga knappstilar.
        Node registerButton = dialog.getDialogPane().lookupButton(registerButtonType);
        registerButton.getStyleClass().add("create-order-button");
        registerButton.getStyleClass().add("payment-confirm-button");

        Node cancelButton = dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-editing-button");

        // Returnerar uppgifterna när användaren väljer Registrera.
        dialog.setResultConverter(buttonType -> {
            if (buttonType == registerButtonType) {
                int selectedIndex =
                        paymentTypeComboBox.getSelectionModel()
                                .getSelectedIndex();

                String paymentTypeCode = selectedIndex == 0 ? "CARD"
                        : selectedIndex == 1 ? "SWISH"
                        : selectedIndex == 2 ? "CASH"
                        : null;

                return new PaymentDetails(
                        invoiceId,
                        paymentDatePicker.getValue(),
                        remainingAmount,
                        paymentTypeCode
                );
            }

            return null;
        });
    }


    // Skapar och fyller formulärets fält.
    private void createFields() {
        // Visar vilken faktura betalningen gäller.
        invoiceLabel = new Label(
                languageManager.getString("invoiceIdInTable") + ": " + invoiceId
        );
        invoiceLabel.getStyleClass().add("payment-invoice-label");

        // Rubrik och förklaring till fakturans belopp.
        amountTitleLabel = new Label(
                languageManager.getString("paymentAmountTitle"));
        amountTitleLabel.getStyleClass().add("payment-amount-title");

        amountDescriptionLabel = new Label(
                languageManager.getString("paymentAmountDescription"));
        amountDescriptionLabel.getStyleClass().add("payment-amount-description");

        // Beloppet visas tydligt och formateras efter valt språk.
        remainingAmountLabel = UIComponents.createValueLabel(
                formatAmount(remainingAmount) + " SEK"
        );
        remainingAmountLabel.getStyleClass().add("payment-amount-value");

        dateLabel = UIComponents.createFormLabel(
                languageManager.getString("paymentDateLabel")
        );

        paymentDatePicker = UIComponents.createDatePicker();
        paymentDatePicker.setValue(LocalDate.now());

        paymentTypeLabel = UIComponents.createFormLabel(
                languageManager.getString("paymentTypeComboBox")
        );

        paymentTypeComboBox = UIComponents.createComboBoxWithNoWidth();

        paymentTypeComboBox.getItems().setAll(
                languageManager.getString("paymentTypeCard"),
                languageManager.getString("paymentTypeSwish"),
                languageManager.getString("paymentTypeCash")
        );

        paymentTypeComboBox.setPromptText(
                languageManager.getString("paymentTypeComboBox")
        );
    }


    // Formaterar belopp med samma decimalformat som användarens språk.
    private String formatAmount(double amount) {
        Locale locale = languageManager.localeProperty().get();

        NumberFormat formatter = NumberFormat.getNumberInstance(locale);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(amount);
    }

    // Öppnar dialogen och returnerar uppgifterna om användaren bekräftar.
    public Optional<PaymentDetails> showAndWait() {
        return dialog.showAndWait();
    }

    // Samlar formulärets värden utan att utföra betalningen.
    public static class PaymentDetails {

        private final int invoiceId;
        private final LocalDate paymentDate;
        private final double amount;
        private final String paymentType;

        public PaymentDetails(
                int invoiceId,
                LocalDate paymentDate,
                double amount,
                String paymentType
        ) {
            this.invoiceId = invoiceId;
            this.paymentDate = paymentDate;
            this.amount = amount;
            this.paymentType = paymentType;
        }

        public int getInvoiceId() {
            return invoiceId;
        }

        public LocalDate getPaymentDate() {
            return paymentDate;
        }

        public double getAmount() {
            return amount;
        }

        public String getPaymentType() {
            return paymentType;
        }
    }


    // Uppdaterar alla texter när användaren byter språk.
    public void changeTextAllComponents() {
        dialog.setTitle(languageManager.getString("registerPaymentTitle"));
        invoiceLabel.setText(languageManager.getString("invoiceIdInTable") + ": " + invoiceId);
        dateLabel.setText(languageManager.getString("paymentDateLabel"));
        titleLabel.setText(languageManager.getString("registerPaymentTitle"));
        amountTitleLabel.setText(languageManager.getString("paymentAmountTitle"));
        amountDescriptionLabel.setText(languageManager.getString("paymentAmountDescription"));
        paymentTypeLabel.setText(languageManager.getString("paymentTypeComboBox"));
        paymentTypeComboBox.getItems().setAll(
                languageManager.getString("paymentTypeCard"),
                languageManager.getString("paymentTypeSwish"),
                languageManager.getString("paymentTypeCash")
        );
        paymentTypeComboBox.setPromptText(languageManager.getString("paymentTypeComboBox"));


        // Uppdaterar även dialogknapparnas språk.
        registerButtonType = new ButtonType(
                languageManager.getString("processPaymentButton"),
                ButtonBar.ButtonData.OK_DONE
        );

        cancelButtonType = new ButtonType(
                languageManager.getString("cancelButton"),
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        dialog.getDialogPane().getButtonTypes().setAll(
                cancelButtonType,
                registerButtonType
        );

        // Behåller den gröna Pay-stilen efter språkbyte.
        Node registerButton =
                dialog.getDialogPane().lookupButton(registerButtonType);

        registerButton.getStyleClass().remove("create-order-button");

        if (!registerButton.getStyleClass().contains("payment-confirm-button")) {
            registerButton.getStyleClass().add("payment-confirm-button");
        }
    }
}