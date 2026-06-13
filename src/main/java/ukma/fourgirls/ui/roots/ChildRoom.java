package ukma.fourgirls.ui.roots;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import ukma.fourgirls.core.InventoryManager;
import ukma.fourgirls.core.NotificationManager;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChildRoom extends Place {
    private static final String INTRO_IMAGE_PATH = "/images/Yevdokha_drawing.png";
    private static final String GAMEPLAY_IMAGE_PATH = "/images/Yevdokha_room.png";
    private ImageView interactiveDrawing;

    public ChildRoom() {
        super(SceneManager.getInstance().getSession().isChildRoomIntroPlayed() ? GAMEPLAY_IMAGE_PATH : INTRO_IMAGE_PATH);
    }

    @Override
    public void onEnter() {
        if (!session.isChildRoomIntroPlayed()) {
            CameraController.setPanningEnabled(false);
            this.startIntroCutscene();
        } else {
            CameraController.setPanningEnabled(true);

            if (!session.isDrawingPickedUp()) {
                if (interactiveDrawing == null) {
                    this.interactiveDrawing = createInteractiveDrawing();
                    this.roomContentLayer.getChildren().add(interactiveDrawing);
                }

                Item yevdokhaDrawing = new Item("Малюнок", "/images/drawing.png");
                InventoryManager.setupPickupAction(
                        session,
                        this.interactiveDrawing,
                        yevdokhaDrawing,
                        (StackPane) this.getRoot(),
                        "Ви підняли малюнок! Підібрані речі ви можете побачити в інвентарі.",
                        this::onDrawingPickedUp
                );
            }

            this.enableNavigation();
        }
    }

    public void startIntroCutscene() {
        Map<String, Runnable> actions = new HashMap<>();

        actions.put("startGameplay", () -> {
            session.setChildRoomIntroPlayed(true);

            this.activateGameplay();

            NotificationManager.showNotification(
                    (StackPane) this.getRoot(),
                    "Завдання: Підніміть малюнок зі столу\nПідказка: щоб підняти річ, натисніть на неї ЛКМ)"
            );

            Item yevdokhaDrawing = new Item("Малюнок", "/images/drawing.png");
            InventoryManager.setupPickupAction(
                    session,
                    this.getInteractiveDrawing(),
                    yevdokhaDrawing,
                    (StackPane) this.getRoot(),
                    "Ви підняли малюнок! Підібрані речі ви можете побачити в інвентарі.",
                    this::onDrawingPickedUp
            );
        });

        StoryRunner.playScene(session, "/story/chapter1.json", "child_room_intro", (StackPane) this.getRoot(), actions, null);
    }

    private void onDrawingPickedUp() {
        session.setDrawingPickedUp(true);
        Map<String, Runnable> actions = new HashMap<>();

        actions.put("showInventory", () -> {
            this.showInventoryUI();
            session.unlockLocation("MomRoom");
            this.enableNavigation();
        });

        actions.put("showNavigationHint", () -> {
            NotificationManager.showNotification(
                    (StackPane) this.getRoot(),
                    "Підказка: Використайте панель навігації праворуч, щоб вийти з кімнати."
            );
        });

        StoryRunner.playScene(session, "/story/chapter1.json", "child_room_after_pickup", (StackPane) this.getRoot(), actions, null);
    }

    public void activateGameplay() {
        this.setBackground(GAMEPLAY_IMAGE_PATH);
        CameraController.setPanningEnabled(true);

        this.interactiveDrawing = createInteractiveDrawing();
        this.roomContentLayer.getChildren().add(interactiveDrawing);
    }

    private ImageView createInteractiveDrawing() {
        Image drawingImg = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/drawing.png")));
        ImageView drawingView = new ImageView(drawingImg);

        drawingView.setFitWidth(220);
        drawingView.setPreserveRatio(true);

        Rotate tiltX = new Rotate(-65, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(15, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(-25, Rotate.Z_AXIS);
        drawingView.getTransforms().addAll(tiltX, rotateY, rotateZ);

        drawingView.setTranslateX(-180);
        drawingView.setTranslateY(320);

        if (this.getRoot() instanceof StackPane rootPane) {
            rootPane.widthProperty().addListener((obs, oldVal, newVal) ->
                    drawingView.setTranslateX(newVal.doubleValue() * (-180.0 / 1536.0))
            );
            rootPane.heightProperty().addListener((obs, oldVal, newVal) ->
                    drawingView.setTranslateY(newVal.doubleValue() * (320.0 / 960.0))
            );
        }

        ColorAdjust darkenEffect = new ColorAdjust();
        darkenEffect.setBrightness(-0.15);
        darkenEffect.setContrast(-0.1);
        darkenEffect.setSaturation(-0.15);
        drawingView.setEffect(darkenEffect);

        drawingView.setOnMouseEntered(e -> {
            ColorAdjust hoverEffect = new ColorAdjust();
            hoverEffect.setBrightness(-0.05);
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
