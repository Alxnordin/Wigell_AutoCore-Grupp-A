package com.wac.autocore.view.service;

import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.UIComponents;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class ServiceOverviewView {

    private final LanguageManager languageManager =
            LanguageManager.getInstance();

    private final VBox view;

    private final Label titleLabel;
    private final Label subtitleLabel;

    private final Label servicesTitle;
    private final Label servicesDescription;

    private final Label packagesTitle;
    private final Label packagesDescription;

    private final Label mechanicsTitle;
    private final Label mechanicsDescription;

    private final Button servicesButton;
    private final Button packagesButton;
    private final Button mechanicsButton;

    public ServiceOverviewView() {

        // Skapar sidans rubrik och beskrivning med projektets standardstil.
        titleLabel = new Label();
        VBox page = UIComponents.createVBoxForViews();

        subtitleLabel = UIComponents.createSubtitle("");

        page.getChildren().addAll(
                UIComponents.createPageTitle(titleLabel, "fa-wrench"),
                subtitleLabel
        );

        // Skapar en gemensam sektion för de tre navigeringskorten.
        VBox section = UIComponents.createSectionBox();
        section.getChildren().add(
                UIComponents.createSectionTitle(""));

        servicesTitle = new Label();
        servicesDescription = new Label();

        packagesTitle = new Label();
        packagesDescription = new Label();

        mechanicsTitle = new Label();
        mechanicsDescription = new Label();

        // Använder befintliga menyknappar för en enhetlig knappstil.
        servicesButton = UIComponents.createMenuButton("", "fa-arrow-right");
        packagesButton = UIComponents.createMenuButton("", "fa-arrow-right");
        mechanicsButton = UIComponents.createMenuButton("", "fa-arrow-right");

        // Placerar pilen efter knapptexten.
        servicesButton.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
        packagesButton.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
        mechanicsButton.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);

        // Gör pilarna vita på samtliga tre knappar.
        setButtonIconWhite(servicesButton);
        setButtonIconWhite(packagesButton);
        setButtonIconWhite(mechanicsButton);

        VBox servicesCard = UIComponents.createOverviewCard(
                "fa-wrench",
                servicesTitle,
                servicesDescription,
                servicesButton
        );

        VBox packagesCard = UIComponents.createOverviewCard(
                "fa-cubes",
                packagesTitle,
                packagesDescription,
                packagesButton
        );

        VBox mechanicsCard = UIComponents.createOverviewCard(
                "fa-users",
                mechanicsTitle,
                mechanicsDescription,
                mechanicsButton
        );

        // Placerar korten i tre lika breda kolumner.
        GridPane cards = new GridPane();
        cards.setHgap(18);
        cards.setVgap(18);

        cards.add(servicesCard, 0, 0);
        cards.add(packagesCard, 1, 0);
        cards.add(mechanicsCard, 2, 0);

        for (int column = 0; column < 3; column++) {
            javafx.scene.layout.ColumnConstraints constraints =
                    new javafx.scene.layout.ColumnConstraints();

            constraints.setPercentWidth(100.0 / 3);
            constraints.setHgrow(Priority.ALWAYS);
            constraints.setHalignment(HPos.CENTER);

            cards.getColumnConstraints().add(constraints);

            // Låter varje kort fylla sin kolumn.
            GridPane.setHgrow(
                    column == 0 ? servicesCard
                            : column == 1 ? packagesCard : mechanicsCard,
                    Priority.ALWAYS
            );
        }

        section.getChildren().add(cards);
        page.getChildren().add(section);

        view = page;

        // Sätter texterna och uppdaterar dem när användaren byter språk.
        changeTextAllComponents();

        languageManager.localeProperty().addListener(
                (observable, oldValue, newValue) -> changeTextAllComponents()
        );

    }

    // Säkerställer att ikonen på en serviceknapp är vit.
    private void setButtonIconWhite(Button button) {
        for (Node node : button.lookupAll(".ikonli-font-icon")) {
            if (node instanceof FontIcon icon) {
                icon.setIconColor(javafx.scene.paint.Color.WHITE);
            }
        }
    }

    // Hämtar alla synliga texter från språkfilerna.
    private void changeTextAllComponents() {
        titleLabel.setText(languageManager.getString("serviceOverviewTitle"));
        subtitleLabel.setText(languageManager.getString("serviceOverviewSubtitle"));
        servicesTitle.setText(languageManager.getString("showServices"));
        servicesDescription.setText(languageManager.getString("serviceOverviewServicesDescription"));
        servicesButton.setText(languageManager.getString("showServices"));

        packagesTitle.setText(languageManager.getString("servicePackages"));
        packagesDescription.setText(languageManager.getString("serviceOverviewPackagesDescription"));
        packagesButton.setText(languageManager.getString("servicePackages"));
        mechanicsTitle.setText(languageManager.getString("showMechanicsButton"));
        mechanicsDescription.setText(languageManager.getString("serviceOverviewMechanicsDescription"));
        mechanicsButton.setText(languageManager.getString("showMechanicsButton"));
    }

    // Gör vyn tillgänglig för AutoCoreApplication.
    public Parent getView() {return view;}

    public Button getServicesButton() {return servicesButton;}

    public Button getPackagesButton() {return packagesButton;}

    public Button getMechanicsButton() {return mechanicsButton;}
}