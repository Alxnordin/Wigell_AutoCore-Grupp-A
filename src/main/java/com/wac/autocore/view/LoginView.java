package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {

    private final VBox view;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Label errorLabel;

    public LoginView() {
        Label titleLabel = new Label("Wigell AutoCore");
        Label usernameLabel = new Label("Användarnamn");
        Label passwordLabel = new Label("Lösernord");

        usernameField = new TextField();
        usernameField.setPromptText("Ange användarnamn");

        passwordField = new PasswordField();
        passwordField.setPromptText("Ange lösenord");

        loginButton = new Button("Logga in");

        errorLabel = new Label();
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        usernameField.setMaxWidth(300);
        passwordField.setMaxWidth(300);


        view = new VBox(10, titleLabel, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, errorLabel);
    }
    public Parent getView() {
        return view;
    }
    public String getUsername() {
        return usernameField.getText();
    }
    public String getPassword() {
        return passwordField.getText();
    }
    public Button getLoginButton() {
        return loginButton;
    }

    public void showError(String message){
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    public void clearError(){
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
