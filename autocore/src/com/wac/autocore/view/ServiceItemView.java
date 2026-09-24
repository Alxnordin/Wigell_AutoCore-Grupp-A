package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

//Visar listan över tillgängliga tjänster
public class ServiceItemView {
    private final Parent root;
    private ListView<String> serviceItemListView;
    private Button backButton;

    public ServiceItemView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label serviceLabel = new Label("Tjänster");
        serviceItemListView = new ListView<>();

        backButton = new Button("Tillbaka");

        box.getChildren().addAll(serviceLabel, serviceItemListView, backButton);

        this.root = box;
    }

    public Parent getView() { return root; }
    public ListView<String> getServiceItemListView() { return serviceItemListView; }
    public Button getBackButton() { return backButton; }
}