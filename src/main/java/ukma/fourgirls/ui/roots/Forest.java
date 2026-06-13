package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ukma.fourgirls.core.StatNotification;
import javafx.util.Duration;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.state.GameState;
import ukma.fourgirls.ui.CameraController;
import ukma.fourgirls.ui.CharacterView;

import java.util.HashMap;
import java.util.Map;

public class Forest extends Place{
    private static final String NORMAL_FOREST = "/images/forest.png";
    private static final String MAGIC_FOREST = "/images/image-forest.png";
    private final Rectangle blackOverlay;
    private CharacterView actorView;
    private CharacterView lisovukView;
    private final Map<String, Runnable> actions;

    public Forest() {
        super(NORMAL_FOREST);

        blackOverlay = new Rectangle();
        blackOverlay.widthProperty().bind(this.root.widthProperty());
        blackOverlay.heightProperty().bind(this.root.heightProperty());
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setOpacity(1.0);
        blackOverlay.setMouseTransparent(true);
        this.root.getChildren().add(blackOverlay);

        actorView = new CharacterView((StackPane) this.getRoot());
        lisovukView = new CharacterView((StackPane) this.getRoot());

        actions = new HashMap<>();
        initBaseActions();
    }

    @Override
    public void onEnter() {
        CameraController.setPanningEnabled(true);
        if (ukma.fourgirls.state.InventoryState.hasItem("Горщик зі смолою")) {
            this.executeHealingPhase();
        } else {
            this.executeIntroPhase();
        }
    }

    /**
     * Перше прибуття до лісу та знайомство з Лісовиком
     */
    private void executeIntroPhase() {
        this.setBackground(NORMAL_FOREST);
        this.playFadeIn();

        actions.put("show_feature_tutorial", () -> {
            ukma.fourgirls.ui.views.TutorialOverlay tutorial = new ukma.fourgirls.ui.views.TutorialOverlay((StackPane) this.getRoot());
            ((StackPane) this.getRoot()).getChildren().add(tutorial.getRoot());
        });

        actions.put("enable_eye_feature_button", () -> {
            Button eyeButton = new Button();
            eyeButton.getStyleClass().add("eye-feature-button");
            StackPane.setAlignment(eyeButton, Pos.TOP_RIGHT);
            StackPane.setMargin(eyeButton, new javafx.geometry.Insets(20, 20, 0, 0));

            eyeButton.setOnAction(e -> {
                ((StackPane) this.getRoot()).getChildren().remove(eyeButton);
                this.setBackground(MAGIC_FOREST);
                StoryRunner.playScene("/story/chapter2.json", "forest_meeting", (StackPane) this.getRoot(), actions, null);
            });
            ((StackPane) this.getRoot()).getChildren().add(eyeButton);
        });

        actions.put("setupKarmaListener", () -> {
            GameState.setKarmaListener((currentKarma, addedPoints) ->
                    StatNotification.show((StackPane) this.getRoot(), currentKarma, addedPoints)
            );
        });

        actions.put("choice_say_name", () -> {
            GameState.changeKarma(1);
            StoryRunner.playScene("/story/chapter2.json", "lisovuk_quest_start", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("choice_stay_silent", () -> {
            GameState.changeKarma(-1);
            StoryRunner.playScene("/story/chapter2.json", "lisovuk_quest_start", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("give_empty_pot", () -> {
            ukma.fourgirls.domain.Item pot = new ukma.fourgirls.domain.Item("Порожній горщик", "/images/empty_pot.png");
            ukma.fourgirls.state.InventoryState.addItem(pot);
            ukma.fourgirls.core.NotificationManager.showNotification(this.root, "Ви отримали предмет: Порожній горщик");

            blackOverlay.toFront();
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.2), blackOverlay);
            fadeOut.setFromValue(0.0);
            fadeOut.setToValue(1.0);
            fadeOut.setOnFinished(e -> ukma.fourgirls.core.SceneManager.getInstance().switchToCachedRoom("Tree", Tree::new));
            fadeOut.play();
        });

        StoryRunner.playScene("/story/chapter2.json", "forest_intro_scene", (StackPane) this.getRoot(), actions, null);
    }

    /**
     * Повернення зі смолою
     */
    private void executeHealingPhase() {
        this.setBackground(MAGIC_FOREST);
        this.playFadeIn();

        actions.put("finish_lisovuk_quest_line", () -> {
            ukma.fourgirls.state.InventoryState.removeItem("Горщик зі смолою");
            ukma.fourgirls.core.NotificationManager.showNotification(this.root, "Смолу використано. Шлях углиб лісу відкрито!");
            this.enableNavigation();
        });

        StoryRunner.playScene("/story/chapter2.json", "lisovuk_healing_and_prophecy", (StackPane) this.getRoot(), actions, null);
    }

    private void initBaseActions() {
        actions.put("showSadYevdokha", () -> {
            if (lisovukView != null) lisovukView.hide();
            if (actorView != null) {
                actorView.setPositionSide(true);
                actorView.setCharacterSprite("/images/Zasmuchena_evdoha.png");
            }
        });

        actions.put("showLisovuk", () -> {
            if (actorView != null) actorView.hide();
            if (lisovukView != null) {
                lisovukView.setPositionSide(false);
                lisovukView.setCharacterSprite("/images/Lisovuk.png");
            }
        });

        actions.put("hideActor", () -> {
            if (actorView != null) actorView.hide();
            if (lisovukView != null) lisovukView.hide();
        });
    }

    private void playFadeIn() {
        blackOverlay.setOpacity(1.0);
        blackOverlay.toFront();
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), blackOverlay);
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.play();
    }

    public void enableNavigation() {
        CameraController.setPanningEnabled(true);
        this.setupNavigation("Forest");
    }
}
