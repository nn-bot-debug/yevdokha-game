package ukma.fourgirls.ui.roots;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import ukma.fourgirls.NavigationPanel;
import ukma.fourgirls.SceneManager;
import ukma.fourgirls.ui.DialogueManager;
import ukma.fourgirls.ui.NotificationManager;
import java.util.Objects;


public class ChildRoom extends Place {
    private static final String IMAGE_PATH = "/images/Yevdokha_room.png";
    private ImageView interactiveDrawing;   // Малюнок на столі

    public ChildRoom() {
        super(IMAGE_PATH);

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
        NotificationManager.showNotification(this.root, "Завдання: Підніміть малюнок зі столу.");

        try {
            Image drawingImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/малюнок.png")));
            interactiveDrawing = new ImageView(drawingImg);

            interactiveDrawing.setFitWidth(120);
            interactiveDrawing.setPreserveRatio(true);

            interactiveDrawing.setTranslateX(400);
            interactiveDrawing.setTranslateY(500);

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

            this.root.getChildren().add(interactiveDrawing);
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити малюнок: " + e.getMessage());
        }
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