package com.wac.autocore.view.customer;

import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CustomerView {

    private final Parent root;

    private final Label title;
    private final Label subtitle;

    private final Label firstNameLabel;
    private final TextField firstNameField;
    private Label firstNameWrongInputLabel;

    private final Label lastNameLabel;
    private final TextField lastNameField;
    private Label lastNameWrongInputLabel;

    private final Label phoneLabel;
    private final TextField phoneField;
    private Label phoneWrongInputLabel;

    private final Label emailLabel;
    private final TextField emailField;
    private Label emailWrongInputLabel;

    private final Label vipLabel;
    private final ComboBox<String> vipComboBox;

    private final Button createCustomerButton;
    private final Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public CustomerView() {

        VBox vBox = UIComponents.createVBoxForViews();
        title = new Label(languageManager.getString("customerViewTitle"));
        HBox titleHBox = UIComponents.createPageTitle(title, "fa-user-plus");
        subtitle = UIComponents.createSubtitle(languageManager.getString("customerViewSubtitle"));

        firstNameLabel = UIComponents.createFormLabel(languageManager.getString("firstNameLabel"));
        firstNameField = UIComponents.createTextField();
        firstNameField.setPromptText(languageManager.getString("firstNameField"));
        firstNameWrongInputLabel = UIComponents.createWrongInputLabel("");

        VBox firstNameVBox = UIComponents.createVBoxWithSpacing5();
        firstNameVBox.getChildren().addAll(firstNameLabel, firstNameField,  firstNameWrongInputLabel);
        firstNameVBox.setMaxWidth(Double.MAX_VALUE);
        firstNameField.setMaxWidth(Double.MAX_VALUE);

        lastNameLabel = UIComponents.createFormLabel(languageManager.getString("lastNameLabel"));
        lastNameField = UIComponents.createTextField();
        lastNameField.setPromptText(languageManager.getString("lastNameField"));
        lastNameWrongInputLabel = UIComponents.createWrongInputLabel("");
        VBox lastNameVBox = UIComponents.createVBoxWithSpacing5();
        lastNameVBox.getChildren().addAll(lastNameLabel, lastNameField, lastNameWrongInputLabel);
        lastNameVBox.setMaxWidth(Double.MAX_VALUE);
        lastNameField.setMaxWidth(Double.MAX_VALUE);

        HBox hBoxFirstLastName = UIComponents.createHBoxWithSpacing15();
        hBoxFirstLastName.getChildren().addAll(firstNameVBox, lastNameVBox);
        HBox.setHgrow(firstNameVBox, Priority.ALWAYS);
        HBox.setHgrow(lastNameVBox, Priority.ALWAYS);

        phoneLabel = UIComponents.createFormLabel(languageManager.getString("phoneLabel"));
        phoneField = UIComponents.createTextField();
        phoneField.setPromptText(languageManager.getString("phoneField"));
        phoneWrongInputLabel = UIComponents.createWrongInputLabel("");
        VBox phoneVBox = UIComponents.createVBoxWithSpacing5();
        phoneVBox.getChildren().addAll(phoneLabel, phoneField, phoneWrongInputLabel);

        phoneVBox.setMaxWidth(Double.MAX_VALUE);
        phoneField.setMaxWidth(Double.MAX_VALUE);

        emailLabel = UIComponents.createFormLabel(languageManager.getString("emailLabel"));
        emailField = UIComponents.createTextField();
        emailField.setPromptText(languageManager.getString("emailField"));
        emailWrongInputLabel = UIComponents.createWrongInputLabel("");
        VBox emailVBox = UIComponents.createVBoxWithSpacing5();
        emailVBox.getChildren().addAll(emailLabel, emailField, emailWrongInputLabel);
        emailVBox.setMaxWidth(Double.MAX_VALUE);
        emailField.setMaxWidth(Double.MAX_VALUE);

        HBox hBoxemailPhone = UIComponents.createHBoxWithSpacing15();
        hBoxemailPhone.getChildren().addAll(phoneVBox, emailVBox);
        HBox.setHgrow(phoneVBox, Priority.ALWAYS);
        HBox.setHgrow(emailVBox, Priority.ALWAYS);

        vipLabel = UIComponents.createFormLabel(languageManager.getString("vipLabel"));
        vipComboBox = UIComponents.createComboBoxWithNoWidth();
        vipComboBox.setPromptText(languageManager.getString("vipComboBox"));
        //FIXA ÖVERSÄTTNING AV vipYes och vipNo. Kan inte göra så här.
        vipComboBox.getItems().addAll(languageManager.getString("vipYes"), languageManager.getString("vipNo"));
        VBox vipVBox = UIComponents.createVBoxWithSpacing5();
        vipVBox.getChildren().addAll(vipLabel, vipComboBox);

        createCustomerButton = new Button(languageManager.getString("createCustomerButton"));
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));
        HBox buttonBox = UIComponents.createButtonBox();
        buttonBox.getChildren().addAll(backButton, createCustomerButton);

//        Label summaryLabel = new Label("Summering");
//        VBox summarySection = UIComponents.createSectionBox();
//        summarySection.getChildren().add(summaryLabel);

        VBox sectionVBox = UIComponents.createSectionBox();
        sectionVBox.setSpacing(30);
        sectionVBox.getChildren().addAll(hBoxFirstLastName, hBoxemailPhone, vipVBox);

        vBox.getChildren().addAll(titleHBox, subtitle, sectionVBox, /*summarySection,*/buttonBox);
        this.root = vBox;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });

    }
    public Parent getView() {
        return root;
    }

    public TextField getFirstNameField() {return firstNameField;}
    public Label getFirstNameLabel() {return firstNameLabel;}
    public Label getFirstNameWrongInputLabel() {return firstNameWrongInputLabel;}

    public TextField getLastNameField() {return lastNameField;}
    public Label getLastNameLabel() {return lastNameLabel;}
    public Label getLastNameWrongInputLabel() {return lastNameWrongInputLabel;}

    public TextField getPhoneField() {return phoneField;}
    public Label getPhoneLabel() {return phoneLabel;}
    public Label getPhoneWrongInputLabel() {return phoneWrongInputLabel;}

    public TextField getEmailField() {return emailField;}
    public Label getEmailLabel() {return emailLabel;}
    public Label getEmailWrongInputLabel() {return emailWrongInputLabel;}

    public Label getVipLabel() {return vipLabel;}
    public ComboBox<String> getVipComboBox() {
        return vipComboBox;
    }

    public Button getCreateCustomerButton() {return createCustomerButton;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("customerViewTitle"));
        subtitle.setText(languageManager.getString("customerViewSubtitle"));

        firstNameLabel.setText(languageManager.getString("firstNameLabel"));
        firstNameField.setPromptText(languageManager.getString("lastNameField"));

        lastNameLabel.setText(languageManager.getString("lastNameLabel"));
        lastNameField.setPromptText(languageManager.getString("firstNameField"));

        phoneLabel.setText(languageManager.getString("phoneLabel"));
        phoneField.setPromptText(languageManager.getString("phoneField"));

        emailLabel.setText(languageManager.getString("emailLabel"));
        emailField.setPromptText(languageManager.getString("emailField"));

        vipLabel.setText(languageManager.getString("vipLabel"));
        //FIXA ÖVERSÄTTNING AV vipYes och vipNo
        vipComboBox.setPromptText(languageManager.getString("vipComboBox"));

        createCustomerButton.setText(languageManager.getString("createCustomerButton"));
        backButton.setText(languageManager.getString("backButton"));
    }
}
