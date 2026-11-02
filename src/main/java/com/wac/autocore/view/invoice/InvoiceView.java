package com.wac.autocore.view.invoice;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

public class InvoiceView {

    private final Parent root;

    private Label title;
    private Label subtitle;

    private Label workOrderLabel;
    private Label discountCodeLabel;
    private Label informationLabel;

    private ComboBox<WorkOrder> workOrderComboBox;
    private ComboBox<String> discountCodeComboBox;

    private Button createInvoiceButton;
    private Button backButton;

    LanguageManager languageManager = LanguageManager.getInstance();

    public InvoiceView() {
        VBox box = UIComponents.createVBoxForViews();
        title = new Label(languageManager.getString("createInvoiceTitle"));
        HBox titleBox = UIComponents.createPageTitle(title, "fa-file-text-o");
        subtitle = UIComponents.createSubtitle(languageManager.getString("createInvoiceSubtitle"));

        discountCodeComboBox = UIComponents.createComboBoxWithNoWidth();

        // Visar valbara arbetsordrar i stället för ett manuellt ID-fält.
        workOrderComboBox = UIComponents.createComboBoxWithNoWidth();
        workOrderComboBox.setPromptText(
                languageManager.getString("workOrderIdField"));
        //Om vill använda UIComponents istället
//        workOrderIdField = UIComponents.createTextField();
//        workOrderIdField.setPromptText(languageManager.getString("workOrderIdField"));

        // Skapar etiketter till formulärets två rullistor.
        workOrderLabel = new Label(
                languageManager.getString("workOrderLabel"));

        discountCodeLabel = new Label(
                languageManager.getString("discountCodeField"));

        // Lägger till ikoner som beskriver respektive val.
        StackPane workOrderComboBoxWithIcon =
                UIComponents.createComboBoxWithIcon(
                        workOrderComboBox, "fa-clipboard");

        StackPane discountComboBoxWithIcon =
                UIComponents.createComboBoxWithIcon(
                        discountCodeComboBox, "fa-percent");

        // Placerar etiketterna till vänster om fält med ikoner.
        HBox workOrderRow = UIComponents.createFormRow(
                workOrderLabel, workOrderComboBoxWithIcon);

        HBox discountCodeRow = UIComponents.createFormRow(
                discountCodeLabel, discountComboBoxWithIcon);

        // Visar arbetsorderns ID och status i rullistan.
        workOrderComboBox.setConverter(new StringConverter<WorkOrder>() {
            @Override
            public String toString(WorkOrder workOrder) {
                if (workOrder == null) {
                    return "";
                }
                return "WO-" + workOrder.getId()
                        + " (" + workOrder.getStatus() + ")";
            }

            @Override
            public WorkOrder fromString(String string) {
                // Valet görs i listan, inte genom fritext.
                return null;
            }
        });


        discountCodeComboBox.setPromptText(languageManager.getString("discountCodeField"));
        discountCodeComboBox.getItems().addAll("WELCOME10", "SERVICE200");

        createInvoiceButton = new Button(languageManager.getString("createInvoiceButton"));
        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        // Skapar en rad där knapparna placeras längst ned i formuläret.
        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        // Skapar ett mellanrum som skjuter skapa-knappen åt höger.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Placerar tillbaka-knappen till vänster och skapa-knappen till höger.
        buttonRow.getChildren().addAll(
                backButton,
                spacer,
                createInvoiceButton
        );

        // Lägger till sidans innehåll i rätt ordning.
        box.getChildren().addAll(
                titleBox,
                subtitle,
                workOrderRow,
                discountCodeRow,
                buttonRow
        );

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }

    // Ger controllern tillgång till valbara arbetsordrar.
    public ComboBox<WorkOrder> getWorkOrderComboBox() {
        return workOrderComboBox;
    }
    public ComboBox<String> getDiscountCodeComboBox() { return discountCodeComboBox; }
    public Button getCreateInvoiceButton() { return createInvoiceButton; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        title.setText(languageManager.getString("createInvoiceTitle"));
        subtitle.setText(languageManager.getString("createInvoiceSubtitle"));

        workOrderComboBox.setPromptText(languageManager.getString("workOrderIdField"));
        discountCodeComboBox.setPromptText(languageManager.getString("discountCodeField"));

        workOrderLabel.setText(languageManager.getString("workOrderLabel"));
        discountCodeLabel.setText(languageManager.getString("discountCodeField"));

        createInvoiceButton.setText(languageManager.getString("createInvoiceButton"));
        backButton.setText(languageManager.getString("backButton"));
    }


}
