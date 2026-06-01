package ukma.fourgirls.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ukma.fourgirls.SceneManager;
import ukma.fourgirls.ui.animation.AnimationCanvas;

import java.util.Objects;

public class InstructionsScreen {

    private final StackPane root;
    private Font font;

    public InstructionsScreen() {
        this.root = new StackPane();

        try {
            font = Font.loadFont(getClass().getResourceAsStream("/Creepster-Regular.ttf"), 24);
        } catch (Exception e) {
            font = Font.font("Arial", 24);
        }

        // Встановлюємо фон із блокнотом
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
        // Задали відступи, щоб текст красиво сідав саме на сторінки відкритої книги
        textContainer.setPadding(new Insets(100, 320, 200, 320));
        uiLayer.getChildren().add(textContainer);

        // Кнопка "Back to Menu"
        VBox bottomContainer = new VBox();
        bottomContainer.setAlignment(Pos.BOTTOM_LEFT);
        bottomContainer.setPadding(new Insets(0, 0, 50, 100)); // Відступ від краю екрана

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> SceneManager.getInstance().switchToMainMenu());

        bottomContainer.getChildren().add(backButton);
        root.getChildren().add(bottomContainer);

        root.getChildren().add(uiLayer);
    }

    public Parent getRoot() {
        return root;
    }
}