package ukma.fourgirls.ui.roots;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ukma.fourgirls.GameContext;
import ukma.fourgirls.presentation.component.StatNotification;
import ukma.fourgirls.domain.model.Item;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CharacterView;
import ukma.fourgirls.ui.puzzles.FourthPuzzle;
import ukma.fourgirls.ui.puzzles.ThirdPuzzle;

import java.util.HashMap;
import java.util.Map;

public class Tree extends Place{
    private static final String NORMAL_TREE = "/images/canvas/tree.png";
    private static final String MAGIC_TREE = "/images/canvas/image-tree.png";

    private final Rectangle blackOverlay;
    private CharacterView actorView;
    private CharacterView antView;
    Map<String, Runnable> actions = new HashMap<>();

    public Tree(GameContext context) {
        super(NORMAL_TREE, context);
        this.getRoot().getStylesheets().add(getClass().getResource("/css/settings.css").toExternalForm());

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
        context.getCamera().setPanningEnabled(true);
        this.setBackground(NORMAL_TREE);
        this.startTreeCutscene();
    }

    private void startTreeCutscene() {
        actorView = new CharacterView((StackPane) this.getRoot());
        antView = new CharacterView((StackPane) this.getRoot());

        actions.clear();

        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.5), blackOverlay);
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.play();

        actions.put("showSadYevdokha", () -> {
            if (antView != null) antView.hide();
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/characters/Zasmuchena_evdoha.png");
            }
        });

        actions.put("showHappyYevdokha", () -> {
            if (antView != null) antView.hide();
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/characters/happy_Yevdokha.png");
            }
        });

        actions.put("showAntRyadovuy", () -> {
            if (actorView != null) actorView.hide();
            if (antView != null) {
                antView.setPositionSide(false);
                antView.setCharacterSprite("/images/characters/ant-ryadovuy.png");
            }
        });

        actions.put("showAntBrother", () -> {
            if (actorView != null) actorView.hide();
            if (antView != null) {
                antView.setPositionSide(false);
                antView.setCharacterSprite("/images/characters/ant-brother.png");
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

            context.getAudio().playEyeLoopSound("/music/eye-button.wav");

            eyeButton.setOnAction(e -> {
                context.getAudio().stopEyeLoopSound(1.5);
                ((StackPane) this.getRoot()).getChildren().remove(eyeButton);
                this.setBackground(MAGIC_TREE);
                StoryRunner.playScene(context, "/story/chapter2.json", "ant_colony_dialogue", (StackPane) this.getRoot(), actions, null);
            });
            ((StackPane) this.getRoot()).getChildren().add(eyeButton);
            eyeButton.toFront();
        });

        actions.put("trigger_root_vision", () -> {
            StoryRunner.playScene(context, "/story/chapter2.json", "ant_rescue_start", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("start_ant_rescue_puzzle", () -> {
            var fourthPuzzle = new ThirdPuzzle(context);

            fourthPuzzle.setOnPuzzleSolved((livesLeft) -> {
                System.out.println("Мурашка врятована з " + livesLeft + " життями. Переходимо до фінальних діалогів.");
                this.onPuzzleFinished(livesLeft, actions);
            });

            StackPane rootPane = (StackPane) this.getRoot();
            rootPane.getChildren().add(fourthPuzzle);
        });

        actions.put("enable_resin_planning", () -> {
            FourthPuzzle logicPuzzle = new FourthPuzzle(context, (puzzleResult) -> {

                this.root.getChildren().removeIf(node -> node instanceof FourthPuzzle);
                StoryRunner.playScene(context, "/story/chapter2.json", "resin_collection_success", (StackPane) this.getRoot(), actions, null);
            });
            this.root.getChildren().add(logicPuzzle);
            logicPuzzle.toFront();
        });

        actions.put("go_back_to_forest_with_resin", () -> {
            blackOverlay.toFront();
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.2), blackOverlay);
            fadeOut.setFromValue(0.0);
            fadeOut.setToValue(1.0);
            fadeOut.setOnFinished(e -> {
                session.removeItem("Порожній горщик");
                session.addItem(new Item("Горщик зі смолою", "/images/objects/full_pot.png"));

                context.getLocations().switchTo("Forest");
            });
            fadeOut.play();
        });

        StoryRunner.playScene(context, "/story/chapter2.json", "resin_tree_intro", (StackPane) this.getRoot(), actions, null);
    }


    private void onPuzzleFinished(int livesLeft, Map<String, Runnable> actions) {
        session.setKarmaListener((currentKarma, addedPoints) ->
                StatNotification.show((StackPane) this.getRoot(), currentKarma, addedPoints)
        );

        int karmaChange = 0;
        if (livesLeft == 0) {
            karmaChange = -1;
        } else if (livesLeft == 1) {
            karmaChange = 0;
        } else if (livesLeft == 2) {
            karmaChange = 1;
        } else if (livesLeft == 3) {
            karmaChange = 2;
        }

        if (karmaChange != 0) {
            session.changeKarma(karmaChange);
        }

        StoryRunner.playScene(context, "/story/chapter2.json", "ant_rescue_success", (StackPane) this.getRoot(), actions, null);
    }
}