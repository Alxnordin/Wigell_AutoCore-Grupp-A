package com.wac.autocore.view;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class ServiceItemCell extends ListCell<ServiceItem> {

    private final Label nameLabel = new Label();

    private final Label priceLabel = new Label();

    private final Button changePriceButton = new Button();

    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final HBox box = new HBox(15);
    private Consumer<ServiceItem> onChangePrice;

    public ServiceItemCell() {

        box.setPadding(new Insets(8));
        box.getChildren().addAll(nameLabel, priceLabel, changePriceButton);

        changePriceButton.setText(languageManager.getString("changePrice"));
        changePriceButton.setOnAction(event -> {
            if(getItem() != null && onChangePrice != null){
                onChangePrice.accept(getItem());
            }
        });
        setGraphic(box);

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changePriceButton.setText(languageManager.getString("changePrice"));

            updateItem(getItem(), false);

        });
    }

    @Override
    protected void updateItem(ServiceItem serviceItem, boolean empty){
        super.updateItem(serviceItem, empty);

        if (empty || serviceItem == null){
            setText(null);
            setGraphic(null);
            return;
        }

        nameLabel.setText(serviceItem.getName());
        priceLabel.setText(serviceItem.getPrice() + " kr");

        setGraphic(changePriceButton.getParent());
    }

    public Button getChangePriceButton() {
        return changePriceButton;
    }

    public ServiceItem getServiceItem(){
        return getItem();
    }
    public void setOnChangePrice(Consumer<ServiceItem> onChangePrice){
        this.onChangePrice = onChangePrice;
    }
}