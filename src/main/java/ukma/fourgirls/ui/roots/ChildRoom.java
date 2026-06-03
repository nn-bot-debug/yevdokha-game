package ukma.fourgirls.ui.roots;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ukma.fourgirls.NavigationPanel;
import ukma.fourgirls.SceneManager;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.DialogueManager;
import ukma.fourgirls.ui.NotificationManager;
import java.util.Objects;


public class ChildRoom extends Place {
    private static final String INTRO_IMAGE_PATH = "/images/Yevdokha_drawing.png";
    private static final String GAMEPLAY_IMAGE_PATH = "/images/Yevdokha_room.png";
    private ImageView interactiveDrawing;

    public ChildRoom() {
        super(INTRO_IMAGE_PATH);

        CameraController.setPanningEnabled(false);

        String[] introDialogue = {
                "I've been working on this drawing for so long...",
                "Every stroke, every detail... I hope Mom will definitely like it.",
                "Okay, it looks like everything is ready. I need to pick it up from the table and take it to her right now."
        };

        new DialogueManager(this.root, introDialogue, this::activateGameplay);
    }

    /**
     * Цей метод вмикає малюнок та панель навігації ПІСЛЯ діалогу
     */
    private void activateGameplay() {
        javafx.application.Platform.runLater(() -> {
            try {
                Image newBackground = new Image(Objects.requireNonNull(getClass().getResourceAsStream(GAMEPLAY_IMAGE_PATH)));
                this.roomView.setImage(newBackground);

                CameraController.setPanningEnabled(true);

                NotificationManager.showNotification(this.root, "Завдання: Підніміть малюнок зі столу.");

                Image drawingImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/drawing.png")));
                interactiveDrawing = new ImageView(drawingImg);

                interactiveDrawing.setFitWidth(70);
                interactiveDrawing.setPreserveRatio(true);
                interactiveDrawing.setRotate(-25);

                interactiveDrawing.setTranslateX(-160);
                interactiveDrawing.setTranslateY(195);

                interactiveDrawing.setPickOnBounds(true);
                interactiveDrawing.setStyle("-fx-cursor: hand;");

                // Наведення на малюнок
                interactiveDrawing.setOnMouseEntered(e -> interactiveDrawing.setEffect(new javafx.scene.effect.Glow(0.5)));
                interactiveDrawing.setOnMouseExited(e -> interactiveDrawing.setEffect(null));

                // Клік по малюнку
                interactiveDrawing.setOnMouseClicked(e -> {
                    this.root.getChildren().remove(interactiveDrawing);

                    NotificationManager.showNotification(this.root, "Ви підняли малюнок! Тепер покажіть його матері.");
                    enableNavigation();
                });
                this.roomContentLayer.getChildren().add(interactiveDrawing);
            } catch (Exception e) {
                System.err.println("Помилка активація геймплею" + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Метод, який ініціалізує та показує панель переходів між кімнатами
     */
    private void enableNavigation() {
        NavigationPanel navPanel = new NavigationPanel();
        navPanel.addNavigationTarget("Кімната матері", () ->
                SceneManager.getInstance().switchToCachedRoom("MomRoom", () -> new MomRoom().getRoot())
        );
        navPanel.addNavigationTarget("Кухня", () ->
                SceneManager.getInstance().switchToCachedRoom("Kitchen", () -> new Kitchen().getRoot())
        );
        navPanel.addNavigationTarget("Вийти на вулицю", () -> {});
        navPanel.attachTo(this.root);
    }
}