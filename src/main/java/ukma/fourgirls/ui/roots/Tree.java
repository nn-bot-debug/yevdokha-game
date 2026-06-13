package ukma.fourgirls.ui.roots;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.StatNotification;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.state.GameState;
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

            var eyeTrack = AudioManager.getInstance().playEyeLoopSound("/music/eye-button.wav");

            eyeButton.setOnAction(e -> {
                if (eyeTrack != null)
                    AudioManager.getInstance().fadeOutAndStop(eyeTrack, 1.5);

                ((StackPane) this.getRoot()).getChildren().remove(eyeButton);
                StoryRunner.playScene("/story/chapter2.json", "ant_colony_dialogue", (StackPane) this.getRoot(), actions, null);
            });
            ((StackPane) this.getRoot()).getChildren().add(eyeButton);
            eyeButton.toFront();
        });

        actions.put("trigger_root_vision", () -> {
            StoryRunner.playScene("/story/chapter2.json", "ant_rescue_start", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("start_ant_rescue_puzzle", () -> {
            System.out.println("Запуск Головоломки 3: Рятування мурахи з коренів.");
            // ТИМЧАСОВА ЗАГЛУШКА ДЛЯ ТЕСТУ: Емулюємо, що гравець пройшов гру на 2 життя (+1 карма)
            int simulatedLives = 2;
            onPuzzleFinished(simulatedLives, actions);
        });

        actions.put("enable_resin_planning", () -> {
            System.out.println("Почався таймер підготовки. Гравець планує збір смоли.");
            // Тут буде запуск збору смоли.
            StoryRunner.playScene("/story/chapter2.json", "resin_collection_success", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("go_back_to_forest_with_resin", () -> {
            blackOverlay.toFront();
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.2), blackOverlay);
            fadeOut.setFromValue(0.0);
            fadeOut.setToValue(1.0);
            fadeOut.setOnFinished(e -> {
                ukma.fourgirls.state.InventoryState.removeItem("Порожній горщик");
                ukma.fourgirls.state.InventoryState.addItem(new ukma.fourgirls.domain.Item("Горщик зі смолою", "/images/full_pot.png"));

                ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("Forest", Forest::new);
            });
            fadeOut.play();
        });

        StoryRunner.playScene("/story/chapter2.json", "resin_tree_intro", (StackPane) this.getRoot(), actions, null);
    }


    private void onPuzzleFinished(int livesLeft, Map<String, Runnable> actions) {
        GameState.setKarmaListener((currentKarma, addedPoints) ->
                StatNotification.show((StackPane) this.getRoot(), currentKarma, addedPoints)
        );

        int karmaChange = 0;
        if (livesLeft == 0) {
            karmaChange = -1;
        } else if (livesLeft == 2) {
            karmaChange = 1;
        }
        if (karmaChange != 0) {
            GameState.changeKarma(karmaChange);
        }

        StoryRunner.playScene("/story/chapter2.json", "ant_rescue_success", (StackPane) this.getRoot(), actions, null);
    }
}
