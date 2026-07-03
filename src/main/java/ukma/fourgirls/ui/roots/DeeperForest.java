package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.core.*;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class DeeperForest extends Place {

    private static final String DEEPER_FOREST = "/images/canvas/deeper-forest.png";
    private static final String IMAGE_DEEPER_FOREST = "/images/canvas/image-deeper-forest.png";

    private final Rectangle blackOverlay;
    private CharacterView actorView;
    private CharacterView bludView;
    private final Map<String, Runnable> actions;
    private final GameSession session;

    public DeeperForest(GameContext context) {
        super(DEEPER_FOREST, context);
        this.session = context.getSession();
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
        bludView = new CharacterView((StackPane) this.getRoot());

        actions = new HashMap<>();
    }

    @Override
    public void onEnter() {
        context.getCamera().setPanningEnabled(true);
        this.setBackground(DEEPER_FOREST);

        actions.clear();
        actions.put("hideActor", () -> {
            if (actorView != null) actorView.hide();
            if (bludView != null) bludView.hide();
        });

        actions.put("showScaredYevdokha", () -> {
            if (bludView != null) bludView.hide();
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/characters/scaredYevdokhaFull.png");
            }
        });

        actions.put("showBlud", () -> {
            if (actorView != null) actorView.hide();
            if (bludView != null) {
                bludView.setPositionSide(false);
                bludView.setCharacterSprite("/images/characters/blud.png");
            }
        });

        actions.put("play-sound-scary-laugh", () -> {
            context.getAudio().buttonSound("/music/scary-laugh.mp3");
        });

        actions.put("enable-blud-eye-button", () -> {
            Button eyeButton = new Button();
            eyeButton.getStyleClass().add("eye-feature-button");
            StackPane.setAlignment(eyeButton, Pos.TOP_RIGHT);
            StackPane.setMargin(eyeButton, new javafx.geometry.Insets(20, 20, 0, 0));

            eyeButton.setOnAction(event -> {
                ((StackPane) this.getRoot()).getChildren().remove(eyeButton);
                this.setBackground(IMAGE_DEEPER_FOREST);
                StoryRunner.playScene(context, "/story/chapter3.json", "blud-intro-scene", (StackPane) this.getRoot(), actions, null);
            });
            ((StackPane) this.getRoot()).getChildren().add(eyeButton);
            eyeButton.toFront();
        });

        actions.put("setupKarmaListener", () -> {
            session.setKarmaListener((currentKarma, addedPoints) ->
                    ukma.fourgirls.core.StatNotification.show((StackPane) this.getRoot(), currentKarma, addedPoints));
        });

        actions.put("choice_refuse_game", () -> {
            session.changeKarma(-1);
            StoryRunner.playScene(context, "/story/chapter3.json", "blud-rejection-scene", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("choice_accept_game", () -> {
            session.changeKarma(1);
            StoryRunner.playScene(context, "/story/chapter3.json", "blud-agreement-scene", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("lake-scene", this::playFadeOutToLake);
      
        actions.put("enable-lake-eye-button", () -> {
            this.playFadeOutToLake();
        });
      
        actions.put("start_maze_with_timer", () -> {
            ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof ukma.fourgirls.ui.puzzles.MazePuzzle);

            ukma.fourgirls.ui.puzzles.MazePuzzle puzzle = new ukma.fourgirls.ui.puzzles.MazePuzzle(context, true, (result) -> {
                ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof ukma.fourgirls.ui.puzzles.MazePuzzle);
                this.playFadeOutToLake();
            });
            ((StackPane) this.getRoot()).getChildren().add(puzzle);
            puzzle.toFront();
        });

        actions.put("start_maze_no_timer", () -> {
            ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof ukma.fourgirls.ui.puzzles.MazePuzzle);

            ukma.fourgirls.ui.puzzles.MazePuzzle puzzle = new ukma.fourgirls.ui.puzzles.MazePuzzle(context, false, (result) -> {
                ((StackPane) this.getRoot()).getChildren().removeIf(node -> node instanceof ukma.fourgirls.ui.puzzles.MazePuzzle);
                this.playFadeOutToLake();
            });
            ((StackPane) this.getRoot()).getChildren().add(puzzle);
            puzzle.toFront();
        });

        StoryRunner.playScene(context, "/story/chapter3.json", "blud-meeting-scene", (StackPane) this.getRoot(), actions, null);
        playFadeIn();
    }

    private void playFadeOutToLake(){
        blackOverlay.toFront();
        context.getAudio().fadeOutBackgroundMusic(1.5);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        fadeOut.setFromValue(0.0);
        fadeOut.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            actions.clear();
            context.getLocations().switchTo("Lake");
        });
        fadeOut.play();
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