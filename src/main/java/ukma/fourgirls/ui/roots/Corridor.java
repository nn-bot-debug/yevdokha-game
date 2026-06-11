package ukma.fourgirls.ui.roots;

import javafx.animation.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.core.NotificationManager;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;

import java.util.HashMap;
import java.util.Map;

public class Corridor extends Place {
    private static final String IMAGE_PATH = "/images/corridor.png";
    private final Rectangle blackOverlay;

    public Corridor() {
        super(IMAGE_PATH);

        blackOverlay = new Rectangle();
        blackOverlay.widthProperty().bind(this.root.widthProperty());
        blackOverlay.heightProperty().bind(this.root.heightProperty());
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setOpacity(0.0);
        blackOverlay.setMouseTransparent(true);

        this.root.getChildren().add(blackOverlay);
    }

    @Override
    public void onEnter() {
        this.enableNavigation();

        PauseTransition testPause = new PauseTransition(Duration.seconds(2));
        testPause.setOnFinished(e -> this.playApproachAnimation());
        testPause.play();
    }

    public void playApproachAnimation() {
        CameraController.setPanningEnabled(false);

        double animationDuration = 3.5;

        TranslateTransition walkingBobbing = new TranslateTransition(Duration.seconds(0.35), this.roomContentLayer);
        walkingBobbing.setByY(15);
        walkingBobbing.setAutoReverse(true);
        walkingBobbing.setCycleCount(10);

        ScaleTransition zoomIn = new ScaleTransition(Duration.seconds(animationDuration), this.roomContentLayer);
        zoomIn.setFromX(1.0);
        zoomIn.setFromY(1.0);
        zoomIn.setToX(1.6);
        zoomIn.setToY(1.6);

        FadeTransition transitionToBlack = new FadeTransition(Duration.seconds(animationDuration), blackOverlay);
        transitionToBlack.setFromValue(0.0);
        transitionToBlack.setToValue(1.0);

        ParallelTransition approachAnimation = new ParallelTransition(walkingBobbing, zoomIn, transitionToBlack);

        approachAnimation.setOnFinished(e -> {
            this.startPuzzleSequence();
        });

        approachAnimation.play();
    }


    private void startPuzzleSequence() {
        System.out.println("Анімація завершена. Екран чорний. Запуск головоломки...");

        NotificationManager.showNotification(this.root, "Головоломка розпочалася!");

        // TODO: Твій виклик менеджера головоломок, наприклад:
        // PuzzleManager.getInstance().startDoorPuzzle(this.root);
        wentOut();
    }

    private void startIntroCutscene() {
        CameraController.setPanningEnabled(false);
        Map<String, Runnable> actions = new HashMap<>();
        actions.put("endCutscene", this::enableNavigation);

        StoryRunner.playScene("/story/chapter1.json", "corridor_intro", (StackPane) this.getRoot(), actions, null);
    }

    public void enableNavigation() {
        CameraController.setPanningEnabled(true);
        this.setupNavigation("Corridor");
    }

    private void wentOut() {
        ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("Yard", Yard::new);
    }
}