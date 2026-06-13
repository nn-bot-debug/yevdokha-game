package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.SceneManager;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class Yard extends Place {
    private static final String IMAGE_PATH = "/images/yard.png";
    private final Rectangle blackOverlay;
    private CharacterView actorView;

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
        actorView = new CharacterView((StackPane) this.getRoot());
        Map<String, Runnable> actions = new HashMap<>();

        actions.put("show_yard_view", () -> {
            FadeTransition fadeInYard = new FadeTransition(Duration.seconds(1.5), blackOverlay);
            fadeInYard.setFromValue(1.0);
            fadeInYard.setToValue(0.0);
            fadeInYard.play();
        });

        actions.put("showSadYevdokha", () -> {
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/Zasmuchena_evdoha.png");
            }
        });

        actions.put("showHappyYevdokha", () -> {
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/happy_Yevdokha.png");
            }
        });

        actions.put("hideActor", () -> {
            if (actorView != null)
                actorView.hide();
        });

        actions.put("enable_papyrus_pickup", () -> {
            // Тут ти потім налаштуєш клік по папірусу через InventoryManager, як у ChildRoom!
            this.enableNavigation();
        });

        actions.put("go_to_forest_automatically", () -> {
            SceneManager.getInstance().switchToCachedRoom("Forest", Forest::new);
        });

        StoryRunner.playScene("/story/chapter2.json", "yard_raven_scene", (StackPane) this.getRoot(), actions, null);
    }

    public void enableNavigation() {
        CameraController.setPanningEnabled(true);
        this.setupNavigation("Yard");
    }
}