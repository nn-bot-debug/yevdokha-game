package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;

import java.util.HashMap;
import java.util.Map;

public class Yard extends Place {
    private static final String IMAGE_PATH = "/images/yard.png";
    private final Rectangle blackOverlay;

    public Yard() {
        super(IMAGE_PATH);

        blackOverlay = new Rectangle();
        blackOverlay.widthProperty().bind(this.root.widthProperty());
        blackOverlay.heightProperty().bind(this.root.heightProperty());
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setOpacity(1.0);
        blackOverlay.setMouseTransparent(true);

        this.root.getChildren().add(blackOverlay);
    }

    @Override
    public void onEnter() {
        CameraController.setPanningEnabled(false);

        this.startYardRavenCutscene();
    }

    private void startYardRavenCutscene() {
        Map<String, Runnable> actions = new HashMap<>();

        actions.put("show_yard_view", () -> {
            FadeTransition fadeInYard = new FadeTransition(Duration.seconds(1.5), blackOverlay);
            fadeInYard.setFromValue(1.0);
            fadeInYard.setToValue(0.0);
            fadeInYard.play();
        });

        actions.put("enable_papyrus_pickup", () -> {
            // Тут ти потім налаштуєш клік по папірусу через InventoryManager, як у ChildRoom!
            this.enableNavigation();
        });

        StoryRunner.playScene("/story/chapter2.json", "yard_raven_scene", (StackPane) this.getRoot(), actions, null);
    }

    public void enableNavigation() {
        CameraController.setPanningEnabled(true);
        this.setupNavigation("Yard");
    }
}