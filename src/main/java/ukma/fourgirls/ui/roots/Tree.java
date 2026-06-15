package ukma.fourgirls.ui.roots;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.LocationRegistry;
import ukma.fourgirls.core.StatNotification;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class Tree extends Place{
    private static final String NORMAL_TREE = "/images/canvas/tree.png";
    private static final String MAGIC_TREE = "/images/canvas/image-tree.png";

    private final Rectangle blackOverlay;
    private CharacterView actorView;
    private CharacterView antView;
    Map<String, Runnable> actions = new HashMap<>();

    public Tree() {
        super(NORMAL_TREE);
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
        CameraController.setPanningEnabled(true);
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

            var eyeTrack = AudioManager.getInstance().playEyeLoopSound("/music/eye-button.wav");

            eyeButton.setOnAction(e -> {
                if (eyeTrack != null)
                    AudioManager.getInstance().fadeOutAndStop(eyeTrack, 1.5);

                ((StackPane) this.getRoot()).getChildren().remove(eyeButton);
                this.setBackground(MAGIC_TREE);
                StoryRunner.playScene(session, "/story/chapter2.json", "ant_colony_dialogue", (StackPane) this.getRoot(), actions, null);
            });
            ((StackPane) this.getRoot()).getChildren().add(eyeButton);
            eyeButton.toFront();
        });

        actions.put("trigger_root_vision", () -> {
            StoryRunner.playScene(session, "/story/chapter2.json", "ant_rescue_start", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("start_ant_rescue_puzzle", () -> {
            System.out.println("Запуск Головоломки 3: Рятування мурахи з коренів.");
            // ТИМЧАСОВА ЗАГЛУШКА ДЛЯ ТЕСТУ: Емулюємо, що гравець пройшов гру на 2 життя (+1 карма)
            int simulatedLives = 2;
            onPuzzleFinished(simulatedLives, actions);
        });

        actions.put("enable_resin_planning", () -> {
            System.out.println("Почався таймер підготовки. Гравець планує збір смоли.");
            ukma.fourgirls.ui.puzzles.VentiliPuzzle logicPuzzle = new ukma.fourgirls.ui.puzzles.VentiliPuzzle(session, (puzzleResult) -> {

                this.root.getChildren().removeIf(node -> node instanceof ukma.fourgirls.ui.puzzles.VentiliPuzzle);
                StoryRunner.playScene(session, "/story/chapter2.json", "resin_collection_success", (StackPane) this.getRoot(), actions, null);
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
                session.addItem(new ukma.fourgirls.domain.Item("Горщик зі смолою", "/images/objects/full_pot.png"));

                LocationRegistry.switchTo("Forest");
            });
            fadeOut.play();
        });

        StoryRunner.playScene(session, "/story/chapter2.json", "resin_tree_intro", (StackPane) this.getRoot(), actions, null);
    }


    private void onPuzzleFinished(int livesLeft, Map<String, Runnable> actions) {
        session.setKarmaListener((currentKarma, addedPoints) ->
                StatNotification.show((StackPane) this.getRoot(), currentKarma, addedPoints)
        );

        int karmaChange = 0;
        if (livesLeft == 0) {
            karmaChange = -1;
        } else if (livesLeft == 2) {
            karmaChange = 1;
        }
        if (karmaChange != 0) {
            session.changeKarma(karmaChange);
        }

        StoryRunner.playScene(session, "/story/chapter2.json", "ant_rescue_success", (StackPane) this.getRoot(), actions, null);
    }
}
