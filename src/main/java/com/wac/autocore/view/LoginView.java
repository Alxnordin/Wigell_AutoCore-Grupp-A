package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class LoginView {

    private final BorderPane view;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Label errorLabel;
    private final Label titleLabel;
    private final Label subtitleLabel;
    private final Label usernameLabel;
    private final Label passwordLabel;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    public LoginView() {
        // Skapar bakgrunden och centrerar inloggningskortet.
        view = new BorderPane();
        view.getStyleClass().add("login-root");

        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(460);


        // Hämtar samma logotyp som används i programmets header.
        Image logoImage = new Image(
                getClass().getResourceAsStream("/WAC_1.png")
        );

        // Visar logotypen med bibehållna proportioner.
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(240);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);

        titleLabel = new Label(
                languageManager.getString("loginTitle"));
        titleLabel.getStyleClass().add("login-title");

        subtitleLabel = new Label(
                languageManager.getString("loginSubtitle"));
        subtitleLabel.getStyleClass().add("login-subtitle");

        usernameLabel = new Label(
                languageManager.getString("usernameLabel"));
        usernameLabel.getStyleClass().add("login-field-label");

        usernameField = new TextField();
        usernameField.setPromptText(
                languageManager.getString("usernamePrompt"));
        usernameField.getStyleClass().add("login-input");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        passwordLabel = new Label(
                languageManager.getString("passwordLabel"));
        passwordLabel.getStyleClass().add("login-field-label");

        passwordField = new PasswordField();
        passwordField.setPromptText(
                languageManager.getString("passwordPrompt"));
        passwordField.getStyleClass().add("login-input");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Behåller knappen som controllern använder för inloggning.
        loginButton = new Button(
                languageManager.getString("loginButton"));
        loginButton.getStyleClass().add("login-submit");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("login-error");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Grupperar rubriken separat från inloggningsfälten.
        VBox heading = new VBox(6, logoView, titleLabel, subtitleLabel);
        heading.setAlignment(Pos.CENTER);
        heading.getStyleClass().add("login-heading");

        // Samlar fält och knapp i en återanvändbar formulärdel.
        VBox form = new VBox(
                8,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField
        );
        form.getStyleClass().add("login-form");

        card.getChildren().addAll(
                heading,
                form,
                loginButton,
                errorLabel
        );

        // Centrerar kortet både vågrätt och lodrätt.
        view.setCenter(card);
        BorderPane.setAlignment(card, Pos.CENTER);

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> changeTextAllComponents()
        );
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

    // Uppdaterar all synlig text när användaren byter språk.
    public void changeTextAllComponents() {
        titleLabel.setText(languageManager.getString("loginTitle"));
        subtitleLabel.setText(languageManager.getString("loginSubtitle"));
        usernameLabel.setText(languageManager.getString("usernameLabel"));
        passwordLabel.setText(languageManager.getString("passwordLabel"));

        usernameField.setPromptText(languageManager.getString("usernamePrompt"));
        passwordField.setPromptText(languageManager.getString("passwordPrompt"));
        loginButton.setText(languageManager.getString("loginButton"));
    }
}
