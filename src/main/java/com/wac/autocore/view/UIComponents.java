package com.wac.autocore.view;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UIComponents {

    //dropdownemenyerna
    public static <T> ComboBox<T> createComboBox() {
        ComboBox<T> comboBox = new ComboBox<>();
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

    public static DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(250);
        datePicker.getStyleClass().add("standard-date-picker");

        return datePicker;
    }

    public static <T> TableView<T> createTable() {
        TableView<T> table = new TableView<>();
        table.getStyleClass().add("standard-table");

        return table;
    }
}

