package com.wac.autocore.view.mechanic;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


//Visar mekanikerlistan och den valda mekanikerns tilldelade arbetsordrar
public class MechanicView {
    private final Parent root;

    private Label mechanicLabel;
    private Label mechanicSubtitle;
    private TableView<Mechanic> mechanicListView;

    private Label mechanicWorkLabel;
    private ListView<String> mechanicWorkListView;
    private Button backButton;

    // Sparar kolumnerna så att rubrikerna kan språkväxlas.
    private TableColumn<Mechanic, Number> idColumn;
    private TableColumn<Mechanic, String> nameColumn;
    private TableColumn<Mechanic, String> phoneColumn;
    private TableColumn<Mechanic, String> specializationColumn;
    private TableColumn<Mechanic, String> availabilityColumn;

    LanguageManager languageManager = LanguageManager.getInstance();

    public MechanicView(){
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        mechanicLabel = new Label(languageManager.getString("mechanicLabel"));
        box.getChildren().add(
                UIComponents.createPageTitle(
                        mechanicLabel,
                        "fa-wrench"
                )
        );

        // Lista med alla mekaniker.
        VBox mechanicSection = UIComponents.createSectionBox();

        // Återanvänder tabellens gemensamma styling.
        mechanicListView = UIComponents.createTable();
        mechanicListView.setPrefHeight(250);
        mechanicListView.setMaxWidth(Double.MAX_VALUE);

        mechanicSubtitle = UIComponents.createSubtitle(
                languageManager.getString("mechanicSubtitle")
        );

        // Tabellen ligger kvar i sektionsrutan.
        mechanicSection.getChildren().add(mechanicListView);

        // Underrubriken placeras direkt under huvudrubriken.
        box.getChildren().add(mechanicSubtitle);

        // Sektion för den valda mekanikerns arbete.
        VBox workSection = UIComponents.createSectionBox();

        // Skapar rubriken för arbetsordersektionen.
        mechanicWorkLabel = UIComponents.createSubtitle(
                languageManager.getString("mechanicWorkLabel")
        );

        // Skapar listan som visar mekanikerns arbetsordrar.
        mechanicWorkListView = new ListView<>();
        mechanicWorkListView.setPrefHeight(250);
        mechanicWorkListView.setMaxWidth(Double.MAX_VALUE);

        // Skapar separata kolumner för mekanikerns uppgifter.
        idColumn = new TableColumn<>(
                languageManager.getString("mechanicId"));
        idColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getId()
                )
        );

        nameColumn = new TableColumn<>(
                languageManager.getString("mechanicName"));
        nameColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getName()
                )
        );

        phoneColumn = new TableColumn<>(
                languageManager.getString("mechanicPhone"));
        phoneColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getPhone()
                )
        );

        specializationColumn = new TableColumn<>(
                        languageManager.getString("mechanicSpecialization")
                );
        specializationColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getSpecialization()
                )
        );

        availabilityColumn = new TableColumn<>(
                languageManager.getString("available"));
        availabilityColumn.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        languageManager.getString(
                                cell.getValue().isAvailable()
                                        ? "availableYes"
                                        : "availableNo"
                        )
                )
        );

        // Låter kolumnerna dela på tabellens tillgängliga bredd.
        mechanicListView.getColumns().addAll(
                idColumn,
                nameColumn,
                phoneColumn,
                specializationColumn,
                availabilityColumn
        );

        mechanicListView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        mechanicListView.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );
        mechanicListView.setPrefHeight(250);


        workSection.getChildren().addAll(
                mechanicWorkLabel,
                mechanicWorkListView
        );

        // Låter listorna växa med tillgängligt utrymme.
        VBox.setVgrow(mechanicSection, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(workSection, javafx.scene.layout.Priority.ALWAYS);

        backButton = UIComponents.createBackButton(languageManager.getString("backButton"));

        box.getChildren().addAll(
                mechanicSection,
                workSection,
                backButton
        );

        // Håller båda listorna flexibla när fönstret ändras.
        mechanicSection.setMaxHeight(Double.MAX_VALUE);
        workSection.setMaxHeight(Double.MAX_VALUE);

        this.root = box;

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            changeTextAllComponents();
        });
    }

    public Parent getView() { return root; }
    public TableView<Mechanic> getMechanicListView() { return mechanicListView; }
    public ListView<String> getMechanicWorkListView() { return mechanicWorkListView; }
    public Button getBackButton() { return backButton; }

    public void changeTextAllComponents() {
        mechanicLabel.setText(languageManager.getString("mechanicLabel"));
        mechanicSubtitle.setText(languageManager.getString("mechanicSubtitle"));
        mechanicWorkLabel.setText(languageManager.getString("mechanicWorkLabel"));
        backButton.setText(languageManager.getString("backButton"));

        idColumn.setText(languageManager.getString("mechanicId"));
        nameColumn.setText(languageManager.getString("mechanicName"));
        phoneColumn.setText(languageManager.getString("mechanicPhone"));
        specializationColumn.setText(languageManager.getString("mechanicSpecialization"));
        availabilityColumn.setText(languageManager.getString("available"));

        mechanicListView.refresh();
    }
}