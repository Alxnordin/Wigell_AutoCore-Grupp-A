package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class UIComponents {

    //dropdownemenyerna
    public static <T> ComboBox<T> createComboBox(){
        ComboBox <T> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(250);
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
    public static DatePicker createDatePicker(){
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(250);
        datePicker.getStyleClass().add("standard-date-picker");

        return datePicker;
    }

    //tabell
    public static <T> TableView<T> createTable(){
        TableView<T> table = new TableView<>();
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

    //Skapa underrubrik i viewklasserna
    public static Label createSubtitle (String text)  {
        Label subtitle = new Label(text);
        subtitle.getStyleClass().add("page-subtitle");
        return subtitle;
    }

    public static Label createInfoLabel(String text) {
        Label infoLabel = new Label(text);
        infoLabel.getStyleClass().add("info-label");
        return infoLabel;
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

}
