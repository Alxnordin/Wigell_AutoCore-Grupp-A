package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

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
        button.getStyleClass().add("view-button");

        return button;
    }

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

    public static Button createCancelEditingButton(String text) {
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

}
