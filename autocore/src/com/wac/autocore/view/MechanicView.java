package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;


//Visar mekanikerlistan och den valda mekanikerns tilldelade arbetsordrar
public class MechanicView {
    private final Parent root;
    private ListView<String> mechanicListView;
    private ListView<String> mechanicWorkListView;
    private Button backButton;

    public MechanicView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        Label mechanicLabel = new Label("Mekaniker");
        mechanicListView = new ListView<>();

        Label mechanicWorkLabel = new Label("Klicka på en mekaniker ovan för att se mekanikerns bokade arbete ");
        mechanicWorkListView = new ListView<>();

        backButton = new Button("Tillbaka");

        box.getChildren().addAll(mechanicLabel, mechanicListView,
                mechanicWorkLabel, mechanicWorkListView, backButton);

        this.root = box;
    }

    public Parent getView() { return root; }
    public ListView<String> getMechanicListView() { return mechanicListView; }
    public ListView<String> getMechanicWorkListView() { return mechanicWorkListView; }
    public Button getBackButton() { return backButton; }
}