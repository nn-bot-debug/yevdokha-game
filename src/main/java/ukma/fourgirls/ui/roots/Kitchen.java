package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import ukma.fourgirls.core.AudioManager;
import ukma.fourgirls.core.InventoryManager;
import ukma.fourgirls.core.NotificationManager;
import ukma.fourgirls.domain.Item;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.state.InventoryState;
import ukma.fourgirls.ui.CharacterView;
import ukma.fourgirls.ui.animation.AnimationCanvas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Kitchen extends Place {
    private static final String IMAGE_PATH = "/images/kitchen.png";
    private ImageView backgroundView;
    private final ImageView interactiveBread;
    private final AnimationCanvas animationCanvas;
    private final Rectangle flashOverlay;

    private CharacterView actorView;
    private CharacterView ratView;

    public Kitchen() {
        super(IMAGE_PATH);

        this.animationCanvas = new AnimationCanvas();
        this.roomContentLayer.getChildren().add(animationCanvas);

        this.interactiveBread = createInteractiveBread();
        this.roomContentLayer.getChildren().add(interactiveBread);

        // Спалах блискавки
        this.flashOverlay = new Rectangle();
        this.flashOverlay.widthProperty().bind(this.root.widthProperty());
        this.flashOverlay.heightProperty().bind(this.root.heightProperty());
        this.flashOverlay.setOpacity(0.0);
        this.flashOverlay.setMouseTransparent(true);
        this.root.getChildren().add(flashOverlay);

        setupNavigation("Kitchen");

        this.startKitchenGameplay();
    }

    public void startKitchenGameplay() {
        NotificationManager.showNotification((StackPane) this.getRoot(), "Завдання: Знайдіть щось поїсти на кухні.");

        Item bread = new Item("Зацвілий хліб", "/images/bread.png");
        Node breadNode = this.getInteractiveBread();

        InventoryManager.setupPickupAction(
                breadNode,
                bread,
                (StackPane) this.getRoot(),
                "Ви знайшли зацвілий хліб.",
                this::onBreadPickedUp
        );
    }

    private void onBreadPickedUp() {
        actorView = new CharacterView((StackPane) this.getRoot());
        ratView = new CharacterView((StackPane) this.getRoot());

        Map<String, Runnable> actions = new HashMap<>();

        actions.put("showEatingSprite", () -> {
            InventoryState.removeItem("Зацвілий хліб");
        });

        actions.put("startStorm", () -> {
            actorView.hide();
            AudioManager.getInstance().playBackgroundMusic("/music/Злива.mp3");
            this.startStormEffects();
        });

        actions.put("showScaredSprite", () -> {
            ratView.hide();
            actorView.setPositionSide(true);
            actorView.setCharacterSprite("/images/scaredYevdokhaFull.png");
        });

        actions.put("showSadYevdokhaSprite", () -> {
            ratView.hide();
            actorView.setPositionSide(true);
            actorView.setCharacterSprite("/images/Zasmuchena_evdoha.png");
        });

        actions.put("hideActorsForWhisper", () -> {
            actorView.hide();
            ratView.hide();
        });

        actions.put("triggerLightning", () -> {
            this.triggerLightningFlash(() -> {
                System.out.println("Спалах грози відбувся!");
            });
        });

        actions.put("triggerBlackout", () -> {
            this.fadeToBlackout(() -> {
                actorView.hide();
                System.out.println("Дівчинка знепритомніла. Екран чорний.");
            });
        });

        actions.put("triggerScreamerSequence", () -> {
            this.setBackground("/images/rain_in_kitchen.png");
            AudioManager.getInstance().buttonSound("/music/window.wav");
            FadeTransition fade = new FadeTransition(Duration.millis(60), flashOverlay);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            fade.setOnFinished(event -> {
                this.triggerLightningFlash(() -> {
                    this.setBackground("/images/window_monster.png");
                });
            });

            fade.play();
        });

        actions.put("showFloorView", () -> {
            this.setBackground("/images/kitchen_floor.png");
            this.animationCanvas.setRainActive(false);

            for (javafx.scene.Node topNode : this.root.getChildren()) {
                if (topNode instanceof javafx.scene.control.ScrollPane sp) {
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);

                    if (sp.getContent() instanceof javafx.scene.layout.Region contentRegion) {
                        contentRegion.setPrefWidth(this.root.getWidth());
                        contentRegion.setPrefHeight(this.root.getHeight());
                    }
                }
            }
            ukma.fourgirls.ui.CameraController.setPanningEnabled(false);
        });

        actions.put("spawnRatNearBread", () -> {
            this.setBackground("/images/kitchen_with_rat.png");
        });

        actions.put("moveRatToDoor", () -> {
            this.setBackground("/images/rat_near_door.png");
        });

        actions.put("playRatSqueak", () -> {
            if (actorView != null) {
                actorView.hide();
            }
            this.setBackground("/images/kitchen_floor.png");
            AudioManager.getInstance().buttonSound("/music/mouse_pisk.wav");
            ratView.setPositionSide(false);
            ratView.setCharacterSprite("/images/scary_rat.png");
        });

        actions.put("riseFromFloorAndHint", () -> {
            this.setBackground("/images/rain_in_kitchen.png");

            for (javafx.scene.Node topNode : this.root.getChildren()) {
                if (topNode instanceof javafx.scene.control.ScrollPane sp) {
                    sp.setFitToWidth(false);
                    sp.setFitToHeight(false);
                    ukma.fourgirls.ui.CameraController.setPanningEnabled(true);
                }
            }

            if (actorView != null) actorView.hide();
            if (ratView != null) ratView.hide();
            StoryRunner.playScene("/story/chapter1.json", "kitchen_leave_hint", (StackPane) this.getRoot(), actions, null);
        });

        actions.put("showFindKeyHint", () -> {
            ukma.fourgirls.state.GameState.kitchenStormFinished = true;
            ukma.fourgirls.core.NotificationManager.showNotification(
                    (StackPane) this.getRoot(),
                    "Завдання: Знайди ключ у кімнаті матері."
            );

            ukma.fourgirls.state.GameState.unlockLocation("MomRoom");
            this.setupNavigation("Kitchen");
        });

        actions.put("leaveKitchenScene", () -> {
            actorView.hide();
            ratView.hide();
            System.out.println("Євдоха біжить за щуром на наступну локацію.");
        });

        StoryRunner.playScene("/story/chapter1.json", "kitchen_storm_sequence", (StackPane) this.getRoot(), actions, null);
    }

    /**
     * Вмикає зливу в системі частинок та змінює фон вікна
     */
    public void startStormEffects() {
        animationCanvas.setRainActive(true);
        this.setBackground("/images/rain_in_kitchen.png");
    }

    public void triggerLightningFlash(Runnable onFlashComplete) {
        flashOverlay.setFill(Color.WHITE);
        flashOverlay.setOpacity(0.0);

        FadeTransition strike = new FadeTransition(Duration.millis(80), flashOverlay);
        strike.setToValue(0.8);

        FadeTransition fade = new FadeTransition(Duration.millis(300), flashOverlay);
        fade.setToValue(0.0);

        SequentialTransition seq = new SequentialTransition(strike, fade);
        seq.setOnFinished(e -> onFlashComplete.run());
        seq.play();
    }

    public void fadeToBlackout(Runnable onBlackoutComplete) {
        flashOverlay.setFill(Color.BLACK);

        FadeTransition fade = new FadeTransition(Duration.millis(300), flashOverlay);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setOnFinished(e -> onBlackoutComplete.run());
        fade.play();
    }

    /**
     * Геометрія, 3D-нахил та стилізація зацвілого хліба на столі
     */
    private ImageView createInteractiveBread() {
        Image breadImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bread.png")));
        ImageView breadView = new ImageView(breadImg);

        breadView.setFitWidth(180);
        breadView.setPreserveRatio(true);

        breadView.setTranslateX(540);
        breadView.setTranslateY(170);

        ColorAdjust darkenEffect = new ColorAdjust();
        darkenEffect.setBrightness(-0.25);
        darkenEffect.setContrast(0.05);
        darkenEffect.setSaturation(-0.1);
        breadView.setEffect(darkenEffect);

        breadView.setOnMouseEntered(e -> {
            breadView.setEffect(null);
        });
        breadView.setOnMouseExited(e -> breadView.setEffect(darkenEffect));

        breadView.setPickOnBounds(true);
        breadView.setStyle("-fx-cursor: hand;");

        return breadView;
    }

    public ImageView getInteractiveBread() {
        return interactiveBread;
    }
}