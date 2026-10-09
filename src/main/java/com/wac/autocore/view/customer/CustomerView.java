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

    private Button createCustomerButton;

    private Label firstNameLabel;
    private TextField firstNameField;
    private Label firstNameWrongInputLabel;

    private Label lastNameLabel;
    private TextField lastNameField;
    private Label lastNameWrongInputLabel;

    private Label phoneLabel;
    private TextField phoneField;
    private Label phoneWrongInputLabel;

    private Label emailLabel;
    private TextField emailField;
    private Label emailWrongInputLabel;

    private Label vipLabel;
    private ComboBox<String> vipComboBox;

    private Label confirmationLabel;

    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public CustomerView() {

        VBox vBox = UIComponents.createVBoxForViews();

        title = new Label(languageManager.getString("customerViewTitle"));
        HBox titleHBox = UIComponents.createPageTitle(title, "fa-user-plus");
        subtitle = UIComponents.createSubtitle(languageManager.getString("customerViewSubtitle"));

        VBox firstNameVBox = buildFirstNameVBox();
        VBox lastNameVBox = buildLastNameVBox();
        HBox hBoxFirstLastName = buildFirstLastNameHBox(firstNameVBox, lastNameVBox);

        VBox phoneVBox = buildPhoneVBox();
        VBox emailVBox = buildEmailVBox();
        HBox hBoxemailPhone = buildPhoneEmailHBox(phoneVBox, emailVBox);

        VBox vipVBox = buildVIPSection();

        HBox bottomButtonHBox = buildBottomButtonHBox();

        //Ej klar med denna!
        confirmationLabel = new Label("");
        VBox summarySection = UIComponents.createSectionBox();
        summarySection.getChildren().add(confirmationLabel);

        VBox sectionVBox = UIComponents.createSectionBox();
        sectionVBox.setSpacing(30);
        sectionVBox.getChildren().addAll(hBoxFirstLastName, hBoxemailPhone, vipVBox);

        vBox.getChildren().addAll(titleHBox, subtitle, sectionVBox, summarySection, bottomButtonHBox);
        this.root = vBox;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });

    }
    public Parent getView() {
        return root;
    }

    public TextField getFirstNameField() {return firstNameField;}
    public Label getFirstNameWrongInputLabel() {return firstNameWrongInputLabel;}

    public TextField getLastNameField() {return lastNameField;}
    public Label getLastNameWrongInputLabel() {return lastNameWrongInputLabel;}

    public TextField getPhoneField() {return phoneField;}
    public Label getPhoneWrongInputLabel() {return phoneWrongInputLabel;}

    public TextField getEmailField() {return emailField;}
    public Label getEmailWrongInputLabel() {return emailWrongInputLabel;}

    public Label getConfirmationLabel() {return confirmationLabel;}

    public Button getCreateCustomerButton() {return createCustomerButton;}
    public Button getBackButton() {return backButton;}

    private VBox buildFirstNameVBox()   {
        firstNameLabel = UIComponents.createFormLabel(languageManager.getString("firstNameLabel"));
        firstNameField = UIComponents.createTextField();
        firstNameField.setPromptText(languageManager.getString("firstNameField"));
        firstNameWrongInputLabel = UIComponents.createWrongInputLabel("");

        VBox firstNameVBox = UIComponents.createVBoxWithSpacing5();
        firstNameVBox.getChildren().addAll(firstNameLabel, firstNameField,  firstNameWrongInputLabel);
        firstNameVBox.setMaxWidth(Double.MAX_VALUE);
        firstNameField.setMaxWidth(Double.MAX_VALUE);
        return firstNameVBox;
    }

    private VBox buildLastNameVBox()   {
        lastNameLabel = UIComponents.createFormLabel(languageManager.getString("lastNameLabel"));
        lastNameField = UIComponents.createTextField();
        lastNameField.setPromptText(languageManager.getString("lastNameField"));
        lastNameWrongInputLabel = UIComponents.createWrongInputLabel("");

        VBox lastNameVBox = UIComponents.createVBoxWithSpacing5();
        lastNameVBox.getChildren().addAll(lastNameLabel, lastNameField, lastNameWrongInputLabel);
        lastNameVBox.setMaxWidth(Double.MAX_VALUE);
        lastNameField.setMaxWidth(Double.MAX_VALUE);
        return lastNameVBox;
    }

    private HBox buildFirstLastNameHBox(VBox firstNameVBox, VBox lastNameVBox)   {
        HBox hBoxFirstLastName = UIComponents.createHBoxWithSpacing15();
        hBoxFirstLastName.getChildren().addAll(firstNameVBox, lastNameVBox);
        HBox.setHgrow(firstNameVBox, Priority.ALWAYS);
        HBox.setHgrow(lastNameVBox, Priority.ALWAYS);
        return hBoxFirstLastName;
    }

    private HBox buildPhoneEmailHBox(VBox phoneVBox,  VBox emailVBox)   {
        HBox hBoxemailPhone = UIComponents.createHBoxWithSpacing15();
        hBoxemailPhone.getChildren().addAll(phoneVBox, emailVBox);
        HBox.setHgrow(phoneVBox, Priority.ALWAYS);
        HBox.setHgrow(emailVBox, Priority.ALWAYS);

        return  hBoxemailPhone;
    }

    private VBox buildPhoneVBox()    {
        phoneLabel = UIComponents.createFormLabel(languageManager.getString("phoneLabel"));
        phoneField = UIComponents.createTextField();
        phoneField.setPromptText(languageManager.getString("phoneField"));
        phoneWrongInputLabel = UIComponents.createWrongInputLabel("");
        VBox phoneVBox = UIComponents.createVBoxWithSpacing5();
        phoneVBox.getChildren().addAll(phoneLabel, phoneField, phoneWrongInputLabel);

        phoneVBox.setMaxWidth(Double.MAX_VALUE);
        phoneField.setMaxWidth(Double.MAX_VALUE);
        return phoneVBox;
    }

    private VBox buildEmailVBox()    {
        emailLabel = UIComponents.createFormLabel(languageManager.getString("emailLabel"));
        emailField = UIComponents.createTextField();
        emailField.setPromptText(languageManager.getString("emailField"));
        emailWrongInputLabel = UIComponents.createWrongInputLabel("");

        VBox emailVBox = UIComponents.createVBoxWithSpacing5();
        emailVBox.getChildren().addAll(emailLabel, emailField, emailWrongInputLabel);
        emailVBox.setMaxWidth(Double.MAX_VALUE);
        emailField.setMaxWidth(Double.MAX_VALUE);
        return emailVBox;
    }

    //Saknar översättning på vip
    private VBox buildVIPSection() {

        vipLabel = UIComponents.createFormLabel(languageManager.getString("vipLabel"));
        ComboBox vipComboBox = UIComponents.createComboBoxWithNoWidth();
        vipComboBox.setPromptText(languageManager.getString("vipComboBox"));

        //FIXA ÖVERSÄTTNING AV vipYes och vipNo. Kan inte göra så här.
        vipComboBox.getItems().addAll(languageManager.getString("vipYes"), languageManager.getString("vipNo"));

        VBox vipVBox = UIComponents.createVBoxWithSpacing5();
        vipVBox.getChildren().addAll(vipLabel, vipComboBox);
        return vipVBox;
    }

    private HBox buildBottomButtonHBox()    {
        createCustomerButton = new Button(languageManager.getString("createCustomerButton"));
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        HBox buttonBox = UIComponents.createButtonBox();
        buttonBox.getChildren().addAll(backButton, createCustomerButton);
        return buttonBox;
    }

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
