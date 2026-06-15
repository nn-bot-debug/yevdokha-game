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
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class Lake extends Place {

        private static final String LAKE = "/images/canvas/Lake.png";
        private static final String LAKE_MAVKY = "/images/canvas/Lake-Mavky.png";
        private static final String LAKE_DINNER = "/images/canvas/mavky-dinner-scene.png";

        private final Rectangle blackOverlay;
        private CharacterView actorView;
        private CharacterView mavkaView;
        private final Map<String, Runnable> actions;
        private final GameSession session;

        public Lake() {
            super(LAKE);
            this.session = SceneManager.getInstance().getSession();
            if (getClass().getResource("/css/settings.css") != null) {
                this.getRoot().getStylesheets().add(getClass().getResource("/css/settings.css").toExternalForm());
            }
            blackOverlay = new Rectangle();
            blackOverlay.widthProperty().bind(this.root.widthProperty());
            blackOverlay.heightProperty().bind(this.root.heightProperty());
            blackOverlay.setFill(Color.BLACK);
            blackOverlay.setOpacity(1.0);
            blackOverlay.setMouseTransparent(true);
            this.root.getChildren().add(blackOverlay);

            actorView = new CharacterView((StackPane) this.getRoot());
            mavkaView = new CharacterView((StackPane) this.getRoot());

            actions = new HashMap<>();
        }

        @Override
        public void onEnter() {
            CameraController.setPanningEnabled(true);
            this.setBackground(LAKE);

            actions.clear();

            actions.put("hideActor", () -> {
                if (actorView != null) actorView.hide();
                if (mavkaView != null) mavkaView.hide();
            });

            actions.put("showScaredYevdokha", () -> {
                if (mavkaView != null) mavkaView.hide();
                if (actorView != null) {
                    actorView.setPositionSide(true);
                    actorView.setCharacterSprite("/images/characters/scaredYevdokhaFull.png");
                }
            });

            actions.put("showMavka", () -> {
                if (actorView != null) actorView.hide();
                if (mavkaView != null) {
                    mavkaView.setPositionSide(false);
                    mavkaView.setCharacterSprite("/images/characters/Mavka.png");
                }
            });

            actions.put("showScaryMavka", () -> {
                if (actorView != null) actorView.hide();
                if (mavkaView != null) {
                    mavkaView.setPositionSide(false);
                    mavkaView.setCharacterSprite("/images/characters/scary-mavka.png");
                }
            });

            actions.put("dinner-scene", () ->{
                this.setBackground(LAKE_DINNER);
                StoryRunner.playScene(session, "/story/chapter3.json", "mavka-dinner-scene", (StackPane) this.getRoot(), actions, null);
            });

            actions.put("play-melodia-sound", () -> {
                AudioManager.getInstance().buttonSound("/music/Lake-scene-melody.mp3");
            });

            actions.put("start", () ->{
                this.setBackground(LAKE_MAVKY);});

            actions.put("enable-lake-eye-button", () -> {
                Button eyeButton = new Button();
                eyeButton.getStyleClass().add("eye-feature-button");
                StackPane.setAlignment(eyeButton, Pos.TOP_RIGHT);
                StackPane.setMargin(eyeButton, new javafx.geometry.Insets(20, 20, 0, 0));

                eyeButton.setOnAction(event -> {
                    ((StackPane) this.getRoot()).getChildren().remove(eyeButton);
                    StoryRunner.playScene(session, "/story/chapter3.json", "mavky-meeting-scene", (StackPane) this.getRoot(), actions, null);
                });
                ((StackPane) this.getRoot()).getChildren().add(eyeButton);
                eyeButton.toFront();
            });

            this.setBackground(LAKE);
            StoryRunner.playScene(session, "/story/chapter3.json", "lake-meeting-scene", (StackPane) this.getRoot(), actions, null);
            playFadeIn();
        }

    private void playFadeIn() {
            blackOverlay.setOpacity(1.0);
            blackOverlay.toFront();
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.0), blackOverlay);
            fadeIn.setFromValue(1.0);
            fadeIn.setToValue(0.0);

            fadeIn.setOnFinished(e -> blackOverlay.setMouseTransparent(true));
            fadeIn.play();
        }
}