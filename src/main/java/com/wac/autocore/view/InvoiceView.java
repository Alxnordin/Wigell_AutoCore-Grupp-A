package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InvoiceView {

    private final Parent root;

    private Label title;
    private Label subtitle;

    private TextField workOrderIdField;
    private ComboBox<String> discountCodeComboBox;

    private Button createInvoiceButton;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceView() {
        VBox box = UIComponents.createVBoxForViews();
        title = new Label(languageManager.getString("createInvoiceTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-file-text-o");
        subtitle = UIComponents.createSubtitle(languageManager.getString("createInvoiceSubtitle"));

        //Textrutan täcker nu hela sidan?
        workOrderIdField = new TextField();
        workOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));
        //Om vill använda UIComponents istället
//        workOrderIdField = UIComponents.createTextField();
//        workOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));

        discountCodeComboBox = UIComponents.createComboBox();
        discountCodeComboBox.setPromptText(languageManager.getString("discountCodeField"));
        discountCodeComboBox.getItems().addAll("WELCOME10", "SERVICE200");

        createInvoiceButton = new Button(languageManager.getString("createInvoiceButton"));
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(titleBox, subtitle, workOrderIdField, discountCodeComboBox,
                createInvoiceButton, backButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }

    public TextField getWorkOrderIdField() { return workOrderIdField; }
    public ComboBox<String> getDiscountCodeComboBox() { return discountCodeComboBox; }
    public Button getCreateInvoiceButton() { return createInvoiceButton; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("createInvoiceTitle"));
        subtitle.setText(languageManager.getString("createInvoiceSubtitle"));

        workOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));
        discountCodeComboBox.setPromptText(languageManager.getString("discountCodeField"));

        createInvoiceButton.setText(languageManager.getString("createInvoiceButton"));
        backButton.setText(languageManager.getString("backButton"));
    }

}
