package ukma.fourgirls.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.ui.animation.AnimationCanvas;

import java.util.Objects;

public class InstructionsScreen {

    private final StackPane root;
    private Font font;

    public InstructionsScreen() {
        this.root = new StackPane();

        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/buttons.css")).toExternalForm());

        try {
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 22);
        } catch (Exception e) {
            font = Font.font("Arial", 24);
        }

        try {
            Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/notebook.png")));
            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            root.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити фон інструкції: " + e.getMessage());
        }

        AnimationCanvas animationCanvas = new AnimationCanvas();
        root.getChildren().add(animationCanvas);

        StackPane uiLayer = new StackPane();

        HBox textContainer = new HBox(150);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.setPadding(new Insets(100, 320, 200, 320));
        uiLayer.getChildren().add(textContainer);

        VBox bottomContainer = new VBox();
        bottomContainer.setAlignment(Pos.TOP_LEFT);
        bottomContainer.setPadding(new Insets(14, 0, 0, 14));

        Button backButton = new Button("Back to Menu");
        backButton.setFont(font);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            SceneManager.getInstance().switchToMainMenu();
        });


        bottomContainer.getChildren().add(backButton);
        root.getChildren().add(uiLayer);
        root.getChildren().add(bottomContainer);
    }

    public Parent getRoot() {
        return root;
    }
}