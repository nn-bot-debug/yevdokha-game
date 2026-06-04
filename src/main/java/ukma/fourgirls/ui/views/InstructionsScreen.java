package ukma.fourgirls.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.ui.animation.AnimationCanvas;

import java.util.Objects;

public class InstructionsScreen {

    private final StackPane root;
    private Font font;

    public InstructionsScreen() {
        this.root = new StackPane();

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

        String BackBtnImage = String.valueOf(Objects.requireNonNull(getClass().getResource("/images/buttonBackground.jpeg")));
        String btnStyle =
                "-fx-background-image: url('" + BackBtnImage + "'); "+
                "-fx-text-fill: #d4cbb8; " +
                "-fx-border-color: #2e261b;" +
                "-fx-border-width: 1px;" +
                "-fx-background-size: cover;" +
                "-fx-background-radius: 6px;" +
                "-fx-border-radius: 6px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(60, 80, 80, 0.8), 15, 0.0, 0,4);" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10px 30px;";

        backButton.setStyle(btnStyle);

        backButton.setOnMouseEntered(e -> {
            backButton.setStyle(
                    btnStyle +
                            "-fx-text-fill: #baaa88; " +
                            "-fx-border-width: 3px;" +
                            "-fx-opacity:0.95; "
                    );
            backButton.setFont(font);
                });

        backButton.setOnMouseExited(e->{
            backButton.setStyle(btnStyle);
            backButton.setFont(font);
        });

        backButton.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());

        bottomContainer.getChildren().add(backButton);
        root.getChildren().add(uiLayer);
        root.getChildren().add(bottomContainer);
    }

    public Parent getRoot() {
        return root;
    }
}