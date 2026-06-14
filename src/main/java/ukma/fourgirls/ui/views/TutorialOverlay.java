package ukma.fourgirls.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ukma.fourgirls.core.AudioManager;

import java.util.Objects;

public class TutorialOverlay {
    private final StackPane overlayRoot;
    private final StackPane parentContainer;

    public TutorialOverlay(StackPane parentContainer) {
        this.parentContainer = parentContainer;
        this.overlayRoot = new StackPane();
        this.overlayRoot.getStyleClass().add("settings-overlay");

        this.overlayRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/settings.css")).toExternalForm());

        VBox tutorialBox = new VBox(20);
        tutorialBox.setAlignment(Pos.CENTER);
        tutorialBox.setMaxWidth(500);
        tutorialBox.setMaxHeight(350);
        tutorialBox.setPadding(new Insets(30, 40, 30, 40));
        tutorialBox.getStyleClass().add("settings-dialog");

        Label titleLabel = new Label("НОВА ЗДАТНІСТЬ: ОКО РОЗУМУ");
        titleLabel.getStyleClass().add("settings-title");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(
                "Шепіт лісу пробудив у Євдосі давню магію.\n\n" +
                        "Тепер у верхньому кутку екрана з'явилося Око. " +
                        "Натискайте на нього, щоб сфокусувати зір, розвіяти туман " +
                        "та помітити приховані знаки, які не видно звичайним оком."
        );
        descriptionLabel.getStyleClass().add("settings-label");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-alignment: center;");

        Button acceptButton = new Button("ПРИЙНЯТИ");
        acceptButton.getStyleClass().add("settings-button");
        acceptButton.setPrefWidth(140);

        acceptButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            parentContainer.getChildren().remove(overlayRoot);
        });

        tutorialBox.getChildren().addAll(titleLabel, descriptionLabel, acceptButton);
        overlayRoot.getChildren().add(tutorialBox);
    }

    public StackPane getRoot() {
        return overlayRoot;
    }
}
