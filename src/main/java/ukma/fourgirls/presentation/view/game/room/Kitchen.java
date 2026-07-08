package ukma.fourgirls.presentation.view.game.room;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import ukma.fourgirls.GameContext;
import ukma.fourgirls.presentation.component.NotificationManager;
import ukma.fourgirls.domain.model.Item;
import ukma.fourgirls.application.service.StoryService;
import ukma.fourgirls.presentation.component.CharacterView;
import ukma.fourgirls.presentation.animation.AnimationCanvas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Kitchen extends Place {
    private static final String IMAGE_PATH = "/images/canvas/kitchen.png";
    private final ImageView interactiveBread;
    private final AnimationCanvas animationCanvas;
    private final Rectangle flashOverlay;

    private CharacterView actorView;
    private CharacterView ratView;

    public Kitchen(GameContext context) {
        super(IMAGE_PATH, context);

        this.animationCanvas = new AnimationCanvas();
        this.roomContentLayer.getChildren().add(animationCanvas);

        this.interactiveBread = createInteractiveBread();
        this.roomContentLayer.getChildren().add(interactiveBread);

        this.flashOverlay = new Rectangle();
        this.flashOverlay.widthProperty().bind(this.root.widthProperty());
        this.flashOverlay.heightProperty().bind(this.root.heightProperty());
        this.flashOverlay.setOpacity(0.0);
        this.flashOverlay.setMouseTransparent(true);
        this.root.getChildren().add(flashOverlay);
    }

    @Override
    public void onEnter() {
        setupNavigation("Kitchen");

        if (session.isKitchenStormFinished()) {
            return;
        }

        this.startKitchenGameplay();
    }

    public void startKitchenGameplay() {
        NotificationManager.showNotification(
                (StackPane) this.getRoot(),
                "Завдання: Знайдіть щось поїсти на кухні."
        );

        Node breadNode = this.getInteractiveBread();

        interactiveBread.setOnMouseClicked(e -> {
            interactiveBread.setVisible(false);
            Item bread = new Item("Зацвілий хліб", "/images/objects/bread.png");
            context.getInventory().pickUpItem(bread);
            NotificationManager.showNotification((StackPane) this.getRoot(), "Ви знайшли зацвілий хліб.");
            onBreadPickedUp();
        });
    }

    private void onBreadPickedUp() {
        actorView = new CharacterView((StackPane) this.getRoot());
        ratView = new CharacterView((StackPane) this.getRoot());

        Map<String, Runnable> actions = new HashMap<>();

        actions.put("showEatingSprite", () ->
                session.removeItem("Зацвілий хліб")
        );

        actions.put("startStorm", () -> {
            actorView.hide();
            context.getAudio().playBackgroundMusic("/music/Злива.mp3");
            this.startStormEffects();
        });

        actions.put("showScaredSprite", () -> {
            ratView.hide();
            actorView.setPositionSide(true);
            actorView.setCharacterSprite("/images/characters/scaredYevdokhaFull.png");
        });

        actions.put("showSadYevdokhaSprite", () -> {
            ratView.hide();
            actorView.setPositionSide(true);
            actorView.setCharacterSprite("/images/characters/Zasmuchena_evdoha.png");
        });

        actions.put("hideActorsForWhisper", () -> {
            actorView.hide();
            ratView.hide();
        });

        actions.put("play-scary-laugh", () -> {
            context.getAudio().buttonSound("/music/scary-laugh.mp3");
        });

        actions.put("triggerLightning", () ->
                this.triggerLightningFlash(() ->
                        System.out.println("Спалах грози відбувся!"))
        );

        actions.put("triggerBlackout", () ->
                this.fadeToBlackout(() -> {
                    actorView.hide();
                    System.out.println("Дівчинка знепритомніла. Екран чорний.");
                })
        );

        actions.put("triggerScreamerSequence", () -> {
            this.setBackground("/images/canvas/rain_in_kitchen.png");
            context.getAudio().buttonSound("/music/skrimer-feik.mp3");
            FadeTransition fade = new FadeTransition(Duration.millis(60), flashOverlay);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(event ->
                    this.triggerLightningFlash(() ->
                            this.setBackground("/images/canvas/window_monster.png"))
            );
            fade.play();
        });

        actions.put("showFloorView", () -> {
            this.setBackground("/images/canvas/kitchen_floor.png");
            this.animationCanvas.setRainActive(false);

            for (Node topNode : this.root.getChildren()) {
                if (topNode instanceof javafx.scene.control.ScrollPane sp) {
                    sp.setFitToWidth(true);
                    sp.setFitToHeight(true);
                    if (sp.getContent() instanceof javafx.scene.layout.Region contentRegion) {
                        contentRegion.setPrefWidth(this.root.getWidth());
                        contentRegion.setPrefHeight(this.root.getHeight());
                    }
                }
            }
            context.getCamera().setPanningEnabled(false);
        });

        actions.put("spawnRatNearBread", () ->
                this.setBackground("/images/canvas/kitchen_with_rat.png")
        );

        actions.put("moveRatToDoor", () ->
                this.setBackground("/images/canvas/rat_near_door.png")
        );

        actions.put("playRatSqueak", () -> {
            if (actorView != null) actorView.hide();
            this.setBackground("/images/canvas/kitchen_floor.png");
            context.getAudio().buttonSound("/music/mouse_pisk.wav");
            ratView.setPositionSide(false);
            ratView.setCharacterSprite("/images/characters/scary_rat.png");
        });

        actions.put("riseFromFloorAndHint", () -> {
            this.setBackground("/images/canvas/rain_in_kitchen.png");

            for (Node topNode : this.root.getChildren()) {
                if (topNode instanceof javafx.scene.control.ScrollPane sp) {
                    sp.setFitToWidth(false);
                    sp.setFitToHeight(false);
                    context.getCamera().setPanningEnabled(true);
                }
            }

            if (actorView != null) actorView.hide();
            if (ratView != null) ratView.hide();
            StoryService.playScene(context, "/story/chapter1.json", "kitchen_leave_hint",
                    (StackPane) this.getRoot(), actions, null);
        });

        actions.put("showFindKeyHint", () -> {
            session.setKitchenStormFinished(true);
            NotificationManager.showNotification(
                    (StackPane) this.getRoot(),
                    "Завдання: Знайди ключ у кімнаті матері."
            );
            session.unlockLocation("MomRoom");
        });

        actions.put("leaveKitchenScene", () -> {
            actorView.hide();
            ratView.hide();
            System.out.println("Євдоха біжить за щуром на наступну локацію.");
        });

        StoryService.playScene(context, "/story/chapter1.json", "kitchen_storm_sequence",
                (StackPane) this.getRoot(), actions, null);
    }

    public void startStormEffects() {
        animationCanvas.setRainActive(true);
        this.setBackground("/images/canvas/rain_in_kitchen.png");
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

    private ImageView createInteractiveBread() {
        Image breadImg = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/objects/bread.png")));
        ImageView breadView = new ImageView(breadImg);

        breadView.setFitWidth(180);
        breadView.setPreserveRatio(true);

        if (this.getRoot() instanceof StackPane rootPane) {
            rootPane.widthProperty().addListener((obs, oldVal, newVal) ->
                    breadView.setTranslateX(newVal.doubleValue() * (540.0 / 1536.0))
            );
            rootPane.heightProperty().addListener((obs, oldVal, newVal) ->
                    breadView.setTranslateY(newVal.doubleValue() * (170.0 / 960.0))
            );
        } else {
            breadView.setTranslateX(540);
            breadView.setTranslateY(170);
        }

        ColorAdjust darkenEffect = new ColorAdjust();
        darkenEffect.setBrightness(-0.25);
        darkenEffect.setContrast(0.05);
        darkenEffect.setSaturation(-0.1);
        breadView.setEffect(darkenEffect);

        breadView.setOnMouseEntered(e -> breadView.setEffect(null));
        breadView.setOnMouseExited(e -> breadView.setEffect(darkenEffect));

        breadView.setPickOnBounds(true);
        breadView.setStyle("-fx-cursor: hand;");

        return breadView;
    }

    public ImageView getInteractiveBread() {
        return interactiveBread;
    }
}
