package ukma.fourgirls.ui.roots;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class Tree extends Place{
    private static final String IMAGE_PATH = "/images/forest.png";
    private final Rectangle blackOverlay;
    private CharacterView actorView;
    private CharacterView antView;

    public Tree() {
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
        CameraController.setPanningEnabled(true); // Можна крутити головою на галявині!
        this.startTreeCutscene();
    }

    private void startTreeCutscene() {
        actorView = new CharacterView((StackPane) this.getRoot());
        antView = new CharacterView((StackPane) this.getRoot());

        Map<String, Runnable> actions = new HashMap<>();

        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.5), blackOverlay);
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.play();

        actions.put("showSadYevdokha", () -> {
            if (antView != null) antView.hide();
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/Zasmuchena_evdoha.png");
            }
        });

        actions.put("showHappyYevdokha", () -> {
            if (antView != null) antView.hide();
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/happy_Yevdokha.png");
            }
        });

        actions.put("showAnt", () -> {
            if (actorView != null) actorView.hide();
            if (antView != null) {
                antView.setPositionSide(false);
                antView.setCharacterSprite("/images/rat.png");
            }
        });

        actions.put("hideActor", () -> {
            if (actorView != null) actorView.hide();
            if (antView != null) antView.hide();
        });

        actions.put("enable_tree_eye_button", () -> {
            Button eyeButton = new Button();
            eyeButton.getStyleClass().add("eye-feature-button");

            StackPane.setAlignment(eyeButton, Pos.TOP_RIGHT);
            StackPane.setMargin(eyeButton, new javafx.geometry.Insets(20, 20, 0, 0));

            eyeButton.setOnAction(e -> {
                //AudioManager.getInstance().buttonSound("/music/magic_eye_flare.wav");
                ((StackPane) this.getRoot()).getChildren().remove(eyeButton);

                StoryRunner.playScene("/story/chapter2.json", "ant_colony_dialogue", (StackPane) this.getRoot(), actions, null);
            });

            ((StackPane) this.getRoot()).getChildren().add(eyeButton);
        });

        StoryRunner.playScene("/story/chapter2.json", "resin_tree_intro", (StackPane) this.getRoot(), actions, null);
    }
}
