package com.wac.autocore.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.Map;

public class UIComponents {

    //dropdownemenyerna
    public static <T> ComboBox<T> createComboBox() {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(250);
        comboBox.getStyleClass().add("standard-combo-box");

        return comboBox;
    }

    public static <T> ComboBox<T> createComboBoxWithNoWidth() {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("standard-combo-box");
        return comboBox;
    }

    //textfält
    public static TextField createTextField() {
        TextField textField = new TextField();
        textField.getStyleClass().add("standard-text-field");

        return textField;
    }

    //datumruta
    public static DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(250);
        datePicker.getStyleClass().add("standard-date-picker");

        return datePicker;
    }

    //tabell
    public static <T> TableView<T> createTable() {
        TableView<T> table = new TableView<>();
        table.setMaxWidth(Double.MAX_VALUE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("standard-table");

        return table;
    }

    //sektionsboxarna
    public static VBox createSectionBox() {
        VBox box = new VBox(8);
        box.getStyleClass().add("section-box");

        return box;
    }

    //rubriken inne i sektionsboxarna
    public static Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.getStyleClass().add("section-title");
        return title;
    }

    //Rubrik + ikon överst på sidan
    public static HBox createPageTitle(Label title, String icon) {

        title.getStyleClass().add("page-title");

        FontIcon titleIcon = new FontIcon(icon);
        titleIcon.setIconSize(22);
        titleIcon.getStyleClass().add("page-title-icon");

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.getChildren().addAll(titleIcon, title);

        return titleBox;
    }

    //Ikon + text, används exempelvis i summeringskort
    public static HBox createIconLabel(FontIcon icon, Label label) {

        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(icon, label);

        return box;
    }

    //tillbakaknappen
    public static Button createBackButton(String text) {

        Button button = new Button(text);

        FontIcon backIcon = new FontIcon("fa-arrow-left");
        backIcon.setIconSize(14);

        button.setGraphic(backIcon);
        button.getStyleClass().add("back-button");

        return button;
    }

    //Visa knapp i tabeller
    public static Button createViewButton(String text) {
        Button button = new Button(text);

        button.getStyleClass().addAll("action-button", "view-button");
        button.setGraphic(new FontIcon("fa-eye"));

        return button;
    }

    // Skapar en knapp för att registrera en betalning.
    public static Button createRegisterPaymentButton(String text) {
        Button button = new Button(text);

        button.getStyleClass().add("register-payment-button");

        FontIcon icon = new FontIcon("fa-credit-card");
        icon.setIconColor(Color.WHITE);
        button.setGraphic(icon);

        button.setStyle("-fx-font-weight: bold;");

        return button;
    }

//    // Skapar en inaktiv knapp som visar att fakturan redan är betald.
//    public static Button createPaidButton(String text) {
//        Button button = new Button(text);
//
//        button.getStyleClass().add("paid-button");
//
//        FontIcon icon = new FontIcon("fa-check");
//        icon.setIconColor(javafx.scene.paint.Color.web("#294A32"));
//        button.setGraphic(icon);
//
//        button.setDisable(true);
//
//        return button;
//    }

    //Skapa vBox som är grunden i alla viewklasser.
    public static VBox createVBoxForViews() {
        VBox vBox = new VBox(18);
        vBox.setPadding(new Insets(25));
        return vBox;
    }

    //Skapa underrubrik i viewklasserna, css-klassen "page-subtitle"
    public static Label createSubtitle(String text) {
        Label subtitle = new Label(text);
        subtitle.getStyleClass().add("page-subtitle");
        return subtitle;
    }

    //Skapar labels som har stylingen "info-label"
    public static Label createInfoLabel(String text) {
        Label infoLabel = new Label(text);
        infoLabel.getStyleClass().add("info-label");
        return infoLabel;
    }

    //Skapar labels som har stylingen "info-value"
    //Kan ta emot String, Localdate, int
    public static Label createValueLabel(Object text) {
        Label valueLabel = new Label(text.toString());
        valueLabel.getStyleClass().add("info-value");
        return valueLabel;
    }

    //Skapar labels som har stylingen "status-value"
    public static Label createStatusValueLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("status-value");
        return label;
    }

    //Skapar VBoxar som har spacing 6
    public static VBox createVBoxWithSpacing6() {
        VBox vBox = new VBox(6);
        return vBox;
    }

    //Skapar VBoxar som har spacing 5
    public static VBox createVBoxWithSpacing5() {
        VBox vBox = new VBox(5);
        return vBox;
    }

    //Summeringskort: ikon + rubrik överst och värdet under, t.ex. "Estimated total time" / "165 min".
    //Etiketterna skickas in av vyn, så att vyn själv kan byta språk och uppdatera värdet.
    public static VBox createSummaryCard(String icon, Label titleLabel, Label valueLabel) {

        FontIcon summaryIcon = new FontIcon(icon);
        summaryIcon.setIconSize(30);
        summaryIcon.getStyleClass().add("summary-icon");

        titleLabel.getStyleClass().add("summary-label");
        valueLabel.getStyleClass().add("summary-value");

        VBox card = new VBox(6);
        card.getStyleClass().add("summary-card");
        card.getChildren().addAll(createIconLabel(summaryIcon, titleLabel), valueLabel);
        card.setMaxWidth(Double.MAX_VALUE);

        return card;
    }

    //lägger summeringskort bredvid varandra och gör dem lika breda
    public static HBox createSummaryCards(VBox... cards) {

        HBox box = new HBox(15);
        box.getStyleClass().add("summary-cards");

        for (VBox card : cards) {
            HBox.setHgrow(card, Priority.ALWAYS);
            box.getChildren().add(card);
        }

        return box;
    }

    //HBox för skapa- och tillbakaknapp, placering CENTER_RIGHT
    public static HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        return buttonBox;
    }

    //HBox med spacing 15
    public static HBox createHBoxWithSpacing15() {
        HBox hBox = new HBox(15);
        return hBox;
    }

    //Företagsinfo på faktura
    public static Label createCompanyNameLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("company-name");
        return label;
    }

    //Företagsinfo på faktura VBox
    public static VBox createCompanyInfoBox() {
        VBox companyBox = new VBox(4);
        companyBox.getStyleClass().add("company-info");
        companyBox.setAlignment(Pos.TOP_RIGHT);
        return companyBox;
    }

    public static Label createInvoiceStatusValueLabel(String text, boolean paid) {
        Label label = createStatusValueLabel(text);

        // Centrerar texten och låter etiketten fylla tillgänglig bredd.
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);

        // Befintlig logik för statusens färg behålls här.
        label.getStyleClass().add("status-value");
        label.getStyleClass().add(paid ? "status-paid" : "status-unpaid");

        if (paid) {
            label.getStyleClass().add("status-paid");
        } else {
            label.getStyleClass().add("status-unpaid");
        }

        return label;
    }

    //Label ovanför inputfälten i formulär, css-klass "form-label"
    public static Label createFormLabel(String formLabelText) {
        Label formLabel = new Label(formLabelText);
        formLabel.getStyleClass().add("form-label");
        return formLabel;
    }

    //Label med felmeddelande om fel input i formulär, css-klass "validation-error"
    public static Label createWrongInputLabel(String text) {
        Label wrongInputLabel = new Label(text);
        wrongInputLabel.getStyleClass().add("wrong-input-label");
        return wrongInputLabel;
    }

    // Huvudknappar i huvudmenyn med ikon
    public static Button createMenuButton(String text, String icon) {
        Button button = new Button(text);

        FontIcon menuIcon = new FontIcon(icon);
        menuIcon.setIconSize(18);

        // Ger utloggningsikonen en egen CSS-klass.
        if ("fa-sign-out".equals(icon)) {
            menuIcon.getStyleClass().add("logout-icon");
        }

        button.setGraphic(menuIcon);
        button.setGraphicTextGap(10);

        return button;
    }

    // Skapar menyval för Create new order med ikon, rubrik och beskrivning
    public static MenuItem createOrderMenuItem(
            Label titleLabel,
            Label descriptionLabel,
            String icon
    ) {
        MenuItem item = new MenuItem();

        FontIcon menuIcon = new FontIcon(icon);
        menuIcon.setIconSize(22);
        menuIcon.getStyleClass().add("order-menu-icon");

        titleLabel.getStyleClass().add("order-menu-title");
        descriptionLabel.getStyleClass().add("order-menu-description");

        VBox textBox = new VBox(2);
        textBox.getChildren().addAll(titleLabel, descriptionLabel);

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(menuIcon, textBox);

        item.setGraphic(content);

        return item;
    }


    //skapa forumäret i creatework order:
    public static VBox createFormSection() {
        VBox section = new VBox(10);

        section.setPadding(new Insets(16));

        section.setMaxWidth(Double.MAX_VALUE);

        section.getStyleClass().add("form-section");

        return section;
    }

    // Standardknapp för att ta bort en rad/tjänst -> soptunna
    public static Button createDeleteButton() {
        Button deleteButton = new Button();

        FontIcon deleteIcon = new FontIcon("fa-trash");
        deleteIcon.setIconSize(18);
        deleteIcon.getStyleClass().add("delete-icon");

        deleteButton.setGraphic(deleteIcon);
        deleteButton.getStyleClass().add("delete-button");

        return deleteButton;
    }

    //Skapar Lägg till-knapp
    public static Button createCreateButton(String text)   {
        Button createButton = new Button(text);
        createButton.getStyleClass().add("create-order-button");

        return createButton;
    }

    // Skapar en knapp för att öppna betalningshistoriken.
    public static Button createPaymentHistoryButton(String text) {
        Button button = new Button(text);

        // Återanvänder grundstilen för gemensamma åtgärdsknappar.
        button.getStyleClass().add("payment-history-button");

        // Visar en historikikon i knappen.
        FontIcon icon = new FontIcon("fa-history");
        icon.setIconSize(14);
        icon.setIconColor(Color.web("#2E7D32"));

        button.setGraphic(icon);
        button.setGraphicTextGap(8);

        return button;
    }

    public static Label createPlaceholderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("placeholder-text");
        return label;
    }

    public static Button createAddRemoveServiceButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("add-service-button");
        return button;
    }

    //Skapar redigera-knapp som visas när man är i redigera-läge på t.ex. en bokning.
    //css-klass "editing-button"
    public static Button createIsEditingButton(String text) {
        Button button = new Button(text);

        FontIcon icon = new FontIcon("fa-pencil");
        button.setGraphic(icon);
        button.getStyleClass().add("editing-button");

        return button;
    }

    public static Button createSaveEditingButton(String text) {
        Button button = new Button(text);

        FontIcon icon = new FontIcon("fa-check");
        button.setGraphic(icon);
        button.getStyleClass().add("save-editing-button");

        return button;
    }

    //Används till cancel-knappar
    public static Button createCancelButton(String text) {
        Button button = new Button(text);

        FontIcon icon = new FontIcon("fa-times");
        button.setGraphic(icon);
        button.getStyleClass().add("cancel-editing-button");

        return button;
    }


    //hänglåsikon på icke valbara comboboxar som i planned order
    public static <T> StackPane createLockedComboBox(ComboBox<T> comboBox) {

        comboBox.setMouseTransparent(true);
        comboBox.setFocusTraversable(false);
        comboBox.getStyleClass().add("locked-combo-box");

        FontIcon lockIcon = new FontIcon("fa-lock");
        lockIcon.setIconSize(14);
        lockIcon.getStyleClass().add("locked-field-icon");
        lockIcon.setMouseTransparent(true);

        StackPane container = new StackPane();

        container.setMaxWidth(Double.MAX_VALUE);

        container.getChildren().addAll(
                comboBox,
                lockIcon
        );

        StackPane.setAlignment(lockIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(lockIcon, new Insets(0, 14, 0, 0));

        return container;
    }


    //hänglåsikon på datepicker
    public static StackPane createLockedDatePicker(DatePicker datePicker) {

        // Gör DatePicker ej klickbar
        datePicker.setMouseTransparent(true);
        datePicker.setFocusTraversable(false);

        // CSS-klass för att gömma kalenderknappen
        datePicker.getStyleClass().add("locked-date-picker");

        FontIcon lockIcon = new FontIcon("fa-lock");
        lockIcon.setIconSize(13);
        lockIcon.getStyleClass().add("locked-field-icon");

        StackPane container = new StackPane();
        container.setMaxWidth(Double.MAX_VALUE);
        container.getChildren().addAll(
                datePicker,
                lockIcon
        );

        StackPane.setAlignment(lockIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(lockIcon, new Insets(0, 12, 0, 0));

        return container;
    }

    //hänglåsikon på textrutor
    public static StackPane createLockedTextField(TextField textField) {

        // Användaren kan inte ändra texten
        textField.setEditable(false);
        textField.setFocusTraversable(false);

        FontIcon lockIcon = new FontIcon("fa-lock");
        lockIcon.setIconSize(13);
        lockIcon.getStyleClass().add("locked-field-icon");

        StackPane container = new StackPane();
        container.setMaxWidth(Double.MAX_VALUE);
        container.getChildren().addAll(
                textField,
                lockIcon
        );

        StackPane.setAlignment(lockIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(lockIcon, new Insets(0, 12, 0, 0));

        return container;
    }

    // Hänglåsikon på textarea
    public static StackPane createLockedTextArea(TextArea textArea) {

        // Användaren kan inte ändra texten
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);

        FontIcon lockIcon = new FontIcon("fa-lock");
        lockIcon.setIconSize(13);
        lockIcon.getStyleClass().add("locked-field-icon");

        StackPane container = new StackPane();
        container.setMaxWidth(Double.MAX_VALUE);

        container.getChildren().addAll(
                textArea,
                lockIcon
        );

        StackPane.setAlignment(lockIcon, Pos.CENTER_RIGHT);
        StackPane.setMargin(lockIcon, new Insets(0, 12, 0, 0));

        return container;
    }


    // Skapar en färgad status-badge
    public static Label createStatusBadge(String status) {
        Label statusLabel = new Label(status);

        statusLabel.getStyleClass().add("status-badge");

        switch (status) {

            case "Created":
            case "Skapad":
                statusLabel.getStyleClass().add("status-created");
                break;

            case "In progress":
            case "Pågående":
                statusLabel.getStyleClass().add("status-in-progress");
                break;

            case "Completed":
            case "Slutförd":
                statusLabel.getStyleClass().add("status-completed");
                break;

            case "Draft":
            case "Utkast":
                statusLabel.getStyleClass().add("status-draft");
                break;

            case "BOOKED":
                statusLabel.getStyleClass().add("status-booked");
                break;

            case "WORK_ORDER_CREATED":
                statusLabel.getStyleClass().add("status-created");
                break;

            case "IN_PROGRESS":
                statusLabel.getStyleClass().add("status-in-progress");
                break;

            case "COMPLETED":
                statusLabel.getStyleClass().add("status-completed");
                break;
        }
        return statusLabel;
    }


    public static HBox createSearchBox(TextField textField) {
        HBox searchBox = new HBox(10);

        textField.getStyleClass().add("standard-text-field");
        textField.setPrefWidth(280);

        FontIcon searchIcon = new FontIcon("fa-search");
        searchIcon.setIconSize(18);

        searchBox.getChildren().addAll(textField, searchIcon);

        return searchBox;
    }


    //pop upp ruta för success -> lyckad händelse
    public static void showSuccessPopup(
            StackPane root,
            String message
    ) {

        Label popup = new Label("✓ " + message);
        popup.getStyleClass().add("success-popup");

        StackPane.setAlignment(
                popup,
                Pos.BOTTOM_RIGHT
        );

        StackPane.setMargin(
                popup,
                new Insets(0, 25, 25, 0)
        );

        root.getChildren().add(popup);

        FadeTransition fadeIn =
                new FadeTransition(
                        Duration.millis(200),
                        popup
                );

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause =
                new PauseTransition(
                        Duration.seconds(2)
                );

        FadeTransition fadeOut =
                new FadeTransition(
                        Duration.millis(500),
                        popup
                );

        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence =
                new SequentialTransition(
                        fadeIn,
                        pause,
                        fadeOut
                );

        sequence.setOnFinished(event ->
                root.getChildren().remove(popup)
        );

        sequence.play();
    }

    // Centrerar statusbadgen i tabellens kolumn.
    public static <S> void centerTableColumn(
            TableColumn<S, ?> column) {

        column.setStyle("-fx-alignment: CENTER;");
    }

    // Skapar en formulärrad med en befintlig etikett och ett fält.
    public static HBox createFormRow(Label label, Node control) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        // Gör etiketten fetstil för tydligare läsbarhet.
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        label.setMinWidth(230);
        label.setPrefWidth(230);
        label.setWrapText(true);

        row.getChildren().addAll(label, control);

        // Låter fältet använda tillgängligt utrymme.
        if (control instanceof Region) {
            Region region = (Region) control;
            HBox.setHgrow(region, Priority.ALWAYS);
            region.setMaxWidth(Double.MAX_VALUE);
        }

        return row;
    }

    // Lägger en ikon till vänster i en ComboBox utan att ändra dess grundstil.
    public static StackPane createComboBoxWithIcon(
            ComboBox<?> comboBox, String iconLiteral) {

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(18);
        icon.setIconColor(Color.web("#287A35"));
        icon.setMouseTransparent(true);

        // Låter ComboBoxen fylla hela den tillgängliga bredden.
        comboBox.setMaxWidth(Double.MAX_VALUE);

        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMaxWidth(Double.MAX_VALUE);

        // Ikonen placeras ovanpå ComboBoxen, till vänster.
        container.getChildren().addAll(comboBox, icon);
        StackPane.setAlignment(icon, Pos.CENTER_LEFT);
        StackPane.setMargin(icon, new Insets(0, 0, 0, 12));

        // Markerar ComboBoxen så att textens placering kan justeras separat.
        comboBox.getStyleClass().add("combo-box-with-icon");

        return container;
    }

    public static Alert createWarningAlert(Alert.AlertType alertType, String message, String alertHeading) {
        Alert alert = new Alert(alertType, message);
        alert.setTitle("AutoCore");
        alert.setHeaderText(null);
        alert.setGraphic(null);

        Label icon = new Label("!");
        icon.getStyleClass().add("warning-icon");

        Label heading = new Label(alertHeading);
        heading.getStyleClass().add("warning-heading");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("warning-message");
        messageLabel.setWrapText(true);

        VBox textBox = new VBox(6, heading, messageLabel);

        HBox content = new HBox(16, icon, textBox);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getStyleClass().add("warning-content");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(content);
        dialogPane.getStyleClass().add("warning-dialog");

        URL stylesheet = UIComponents.class.getResource("/style.css");
        if (stylesheet != null) {
            dialogPane.getStylesheets().add(stylesheet.toExternalForm());
        }

        dialogPane.setMinWidth(380);

        return alert;
    }

    public static Alert createConfirmationAlert(String alertHeading, String message,
                                                Map<String, String> informationDetails) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("AutoCore");
        alert.setHeaderText(null);
        alert.setGraphic(null);

        Label successIcon = new Label("✓");
        successIcon.getStyleClass().add("information-icon");

        Label heading = new Label(alertHeading);
        heading.getStyleClass().add("information-heading");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("information-message");
        messageLabel.setWrapText(true);

        VBox header = new VBox(8, successIcon, heading, messageLabel);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("information-header");

        VBox detailsBox = new VBox(12);
        detailsBox.getStyleClass().add("information-details");

        informationDetails.forEach((label, value) -> {
            Label fieldLabel = new Label(label);
            fieldLabel.getStyleClass().add("information-field-label");

            Label fieldValue = new Label(value);
            fieldValue.getStyleClass().add("information-field-value");
            fieldValue.setWrapText(true);

            VBox field = new VBox(3, fieldLabel, fieldValue);
            detailsBox.getChildren().add(field);
        });

        VBox content = new VBox(header, detailsBox);
        content.getStyleClass().add("information-content");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(content);
        dialogPane.getStyleClass().add("information-dialog");

        URL stylesheet = UIComponents.class.getResource("/style.css");
        if (stylesheet != null) {
            dialogPane.getStylesheets().add(stylesheet.toExternalForm());
        }

        dialogPane.setMinWidth(420);

        return alert;
    }

    // Skapar ett enhetligt navigeringskort med ikon, rubrik och beskrivning.
    public static VBox createOverviewCard(
            String icon,
            Label titleLabel,
            Label descriptionLabel,
            Button actionButton) {

        FontIcon cardIcon = new FontIcon(icon);
        cardIcon.setIconSize(28);
        cardIcon.setIconColor(Color.web("#287A35"));
        cardIcon.getStyleClass().add("overview-card-icon");

        // Centrerar rubrik och beskrivning.
        titleLabel.getStyleClass().add("overview-card-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        descriptionLabel.getStyleClass().add("overview-card-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        descriptionLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Skapar en behållare som centrerar knappen i kortet.
        HBox buttonContainer = new HBox(actionButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setMaxWidth(Double.MAX_VALUE);

        // Låter knappen behålla sin storlek och centrerar den i kortet.
        actionButton.setMaxWidth(Region.USE_PREF_SIZE);

        // Samlar och centrerar kortets samtliga komponenter.
        VBox card = new VBox(12);
        card.getStyleClass().add("overview-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.CENTER);

        // Lägger in behållaren i stället för knappen direkt.
        card.getChildren().addAll(
                cardIcon,
                titleLabel,
                descriptionLabel,
                buttonContainer
        );

        return card;
    }

    public static TextInputDialog createPriceInputDialog(
            String title, String heading, String label, String currentPrice) {

        TextInputDialog dialog = new TextInputDialog(currentPrice);

        dialog.setTitle(title);
        dialog.setHeaderText(heading);
        dialog.setContentText(label);
        dialog.setGraphic(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("price-dialog");

        URL stylesheet = UIComponents.class.getResource("/style.css");
        if (stylesheet != null) {
            dialogPane.getStylesheets().add(stylesheet.toExternalForm());
        }

        return dialog;
    }


}




