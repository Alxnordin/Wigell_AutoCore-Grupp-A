package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class ServiceView {
    private final Parent root;
    private ListView<String> serviceItemListView;
    private ListView<String> mechanicListView;
    private Button backButton;

    public ServiceView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label serviceLabel = new Label("Tjänster");
        serviceItemListView = new ListView<>();

        Label mechanicLabel = new Label("Mekaniker");
        mechanicListView = new ListView<>();

        backButton= new Button("Tillbaka");

        box.getChildren().addAll(serviceLabel,serviceItemListView,
                mechanicLabel, mechanicListView, backButton
        );

        this.root = box;
    }

    public Parent getView() { return root; }
    public ListView<String> getServiceItemListView() { return serviceItemListView; }
    public ListView<String> getMechanicListView() { return mechanicListView; }
    public Button getBackButton() { return backButton; }
}
