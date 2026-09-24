package com.wac.autocore.view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

//UI-vyn som visar listan över befintliga bokningar.
public class BookingListView {

    private final Parent root;
    private final ListView<String> bookingListView;
    private final Button backButton;

    public BookingListView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        bookingListView = new ListView<>();

        backButton = new Button("Tillbaka");

        box.getChildren().addAll(bookingListView,backButton);
        this.root = box;
    }

    public Parent getView() {return root;}

    public ListView<String> getBookingListView() {return bookingListView;}
    public Button getBackButton() {return backButton;}
}
