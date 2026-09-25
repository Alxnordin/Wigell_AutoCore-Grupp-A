package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;


//Visar mekanikerlistan och den valda mekanikerns tilldelade arbetsordrar
public class MechanicView {
    private final Parent root;

    private Label mechanicLabel;
    private ListView<String> mechanicListView;

    private Label mechanicWorkLabel;
    private ListView<String> mechanicWorkListView;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public MechanicView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        mechanicLabel = new Label(languageManager.getString("mechanicLabel"));
        mechanicListView = new ListView<>();

        mechanicWorkLabel = new Label(languageManager.getString("mechanicWorkLabel"));
        mechanicWorkListView = new ListView<>();

        backButton = new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(mechanicLabel, mechanicListView,
                mechanicWorkLabel, mechanicWorkListView, backButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }
    public ListView<String> getMechanicListView() { return mechanicListView; }
    public ListView<String> getMechanicWorkListView() { return mechanicWorkListView; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        mechanicLabel.setText(languageManager.getString("mechanicLabel"));
        mechanicWorkLabel.setText(languageManager.getString("mechanicWorkLabel"));
        backButton.setText(languageManager.getString("backButton"));
    }
}