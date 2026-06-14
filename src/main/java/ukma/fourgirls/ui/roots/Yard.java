package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.core.LocationRegistry;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class Yard extends Place {
    private static final String IMAGE_PATH = "/images/canvas/yard.png";
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
                actorView.setCharacterSprite("/images/characters/Zasmuchena_evdoha.png");
            }
        });

        actions.put("showHappyYevdokha", () -> {
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/characters/happy_Yevdokha.png");
            }
        });

        actions.put("hideActor", () -> {
            if (actorView != null)
                actorView.hide();
        });

        actions.put("enable_papyrus_pickup", () -> {
            System.out.println("Папірус піднято по сюжету.");
        });

        actions.put("go_to_forest_automatically", () -> {
            session.lockLocation("ChildRoom");
            session.lockLocation("Kitchen");
            session.lockLocation("MomRoom");
            session.lockLocation("Corridor");
            session.lockLocation("Yard");
            blackOverlay.toFront();
            FadeTransition fadeToBlack = new FadeTransition(Duration.seconds(1.2), blackOverlay);
            fadeToBlack.setFromValue(0.0);
            fadeToBlack.setToValue(1.0);
            fadeToBlack.setOnFinished(e ->
                    LocationRegistry.switchTo("Forest")
            );
            fadeToBlack.play();
        });

        StoryRunner.playScene(session, "/story/chapter2.json", "yard_raven_scene", (StackPane) this.getRoot(), actions, null);
    }

    public void enableNavigation() {
        CameraController.setPanningEnabled(true);
        this.setupNavigation("Yard");
    }
}
