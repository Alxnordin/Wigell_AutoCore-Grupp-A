package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
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

    LanguageManager languageManager = LanguageManager.getInstance();

    public BookingListView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        bookingListView = new ListView<>();
        backButton = new Button(languageManager.getString("backButton"));

        box.getChildren().addAll(bookingListView,backButton);
        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() {return root;}

    public ListView<String> getBookingListView() {return bookingListView;}
    public Button getBackButton() {return backButton;}

    public void changeTextAllComponents() {
        backButton.setText(languageManager.getString("backButton"));
    }
}
