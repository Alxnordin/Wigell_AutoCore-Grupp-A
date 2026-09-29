package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

//Visar listan över tillgängliga tjänster
public class ServiceItemView {
    private final Parent root;

    private Label serviceLabel;
    private ListView<String> serviceItemListView;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public ServiceItemView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        serviceLabel = new Label(languageManager.getString("serviceLabel"));
        serviceItemListView = new ListView<>();

        backButton= new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(serviceLabel, serviceItemListView, backButton);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }
    public ListView<String> getServiceItemListView() { return serviceItemListView; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        serviceLabel.setText(languageManager.getString("serviceLabel"));
        backButton.setText(languageManager.getString("backButton"));
    }
}