package ukma.fourgirls.ui.roots;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import ukma.fourgirls.core.InventoryManager;
import ukma.fourgirls.core.NotificationManager;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.state.GameState;
import ukma.fourgirls.ui.CameraController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChildRoom extends Place {
    private static final String INTRO_IMAGE_PATH = "/images/Yevdokha_drawing.png";
    private static final String GAMEPLAY_IMAGE_PATH = "/images/Yevdokha_room.png";
    private ImageView interactiveDrawing;

    public ChildRoom() {
        super(INTRO_IMAGE_PATH);
        CameraController.setPanningEnabled(false);

        this.startIntroCutscene();
    }

    public void startIntroCutscene() {
        Map<String, Runnable> actions = new HashMap<>();

        actions.put("startGameplay", () -> {
            this.activateGameplay();
            NotificationManager.showNotification((StackPane) this.getRoot(),
                    "Завдання: Підніміть малюнок зі столу\nПідказка: щоб підняти річ, натисніть на неї ЛКМ)");

            Item yevdokhaDrawing = new Item("Малюнок", "/images/drawing.png");

            InventoryManager.setupPickupAction(
                    this.getInteractiveDrawing(),
                    yevdokhaDrawing,
                    (StackPane) this.getRoot(),
                    "Ви підняли малюнок! Підібрані речі ви можете побачити в інвентарі.",
                    this::onDrawingPickedUp
            );
        });

        StoryRunner.playScene("/story/chapter1.json", "child_room_intro", (StackPane) this.getRoot(), actions, null);
    }

    private void onDrawingPickedUp() {
        Map<String, Runnable> actions = new HashMap<>();

        actions.put("showInventory", () -> {
            this.showInventoryUI();
            GameState.unlockLocation("MomRoom");
            this.enableNavigation();
        });

        actions.put("showNavigationHint", () -> {
            NotificationManager.showNotification((StackPane) this.getRoot(),
                    "Підказка: Використайте панель навігації праворуч, щоб вийти з кімнати.");
        });

        StoryRunner.playScene("/story/chapter1.json", "child_room_after_pickup", (StackPane) this.getRoot(), actions, null);
    }

    public void activateGameplay() {
        this.setBackground(GAMEPLAY_IMAGE_PATH);
        CameraController.setPanningEnabled(true);

        this.interactiveDrawing = createInteractiveDrawing();

        this.roomContentLayer.getChildren().add(interactiveDrawing);
    }

    private ImageView createInteractiveDrawing() {
        Image drawingImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/drawing.png")));
        ImageView drawingView = new ImageView(drawingImg);

        drawingView.setFitWidth(160);
        drawingView.setPreserveRatio(true);

        // 3D Трансформація
        Rotate tiltX = new Rotate(-70, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(15, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(-25, Rotate.Z_AXIS);
        drawingView.getTransforms().addAll(tiltX, rotateY, rotateZ);

        drawingView.setTranslateX(-230);
        drawingView.setTranslateY(340);

        ColorAdjust darkenEffect = new ColorAdjust();
        darkenEffect.setBrightness(-0.15);
        darkenEffect.setContrast(-0.1);
        darkenEffect.setSaturation(-0.15);
        drawingView.setEffect(darkenEffect);

        // Логіка наведення миші
        drawingView.setOnMouseEntered(e -> {
            ColorAdjust hoverEffect = new ColorAdjust();
            hoverEffect.setBrightness(-0.05);
            hoverEffect.setContrast(0.0);
            drawingView.setEffect(hoverEffect);
        });
        drawingView.setOnMouseExited(e -> drawingView.setEffect(darkenEffect));

        drawingView.setPickOnBounds(true);
        drawingView.setStyle("-fx-cursor: hand;");

        return drawingView;
    }

    public ImageView getInteractiveDrawing() {
        return interactiveDrawing;
    }

    public void enableNavigation() {
        setupNavigation("ChildRoom");
    }
}