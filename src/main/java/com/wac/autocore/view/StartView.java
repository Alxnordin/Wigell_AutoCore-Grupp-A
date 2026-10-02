package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StartView {

    private final Parent root;

    private Label subtitle;
    private Label instruction;

    LanguageManager languageManager = LanguageManager.getInstance();

    public StartView() {

        // Huvudcontainer
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.getStyleClass().add("start-view");

        // Logotyp
        Image logoImage = new Image(getClass().getResourceAsStream("/WAC_1.png"));

        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(400);
        logoView.setPreserveRatio(true);

        // Underrubrik
        subtitle = new Label(languageManager.getString("startSubtitle"));
        subtitle.getStyleClass().add("start-subtitle");

        // Instruktion
        instruction = new Label(languageManager.getString("startInstruction"));
        instruction.getStyleClass().add("start-instruction");

        box.getChildren().addAll(
                logoView,
                subtitle,
                instruction
        );

        this.root = box;

        // Uppdatera text när språket ändras
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
                    changeTextAllComponents();}
        );
    }

    public Parent getView() {
        return root;
    }

    public void changeTextAllComponents() {
        subtitle.setText(languageManager.getString("startSubtitle"));
        instruction.setText(languageManager.getString("startInstruction"));
    }
}