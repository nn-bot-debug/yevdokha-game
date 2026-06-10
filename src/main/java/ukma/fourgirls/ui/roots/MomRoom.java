package ukma.fourgirls.ui.roots;

import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.core.InventoryManager;
import ukma.fourgirls.core.NotificationManager;
import ukma.fourgirls.core.StatNotification;
import ukma.fourgirls.logic.StoryRunner;
import ukma.fourgirls.state.GameState;
import ukma.fourgirls.state.InventoryState;
import ukma.fourgirls.ui.CameraController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MomRoom extends Place {
    private static final String IMAGE_PATH = "/images/mother_room.png";
    private static final String SECOND_IMAGE_PATH = "/images/drawing.png";
    private static final String SCARY_MOM_PATH = "/images/scary_mom_screamer.jpeg";

    private static final String CORNER_PATH = "/images/corner.png";
    private static final String BROOCH_PATH = "/images/brooch.png";

    private final Rectangle blackOverlay;
    private final ImageView momView;
    private final ImageView drawingView;
    private final ImageView scaryMomView;
    private ImageView cornerView;
    private ImageView broochView;
    private Rectangle whiteFlash;

    public MomRoom() {
        super(IMAGE_PATH);

        blackOverlay = new Rectangle();
        blackOverlay.widthProperty().bind(this.root.widthProperty());
        blackOverlay.heightProperty().bind(this.root.heightProperty());
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setMouseTransparent(true);

        this.root.getChildren().add(blackOverlay);
        CameraController.setPanningEnabled(false);

        momView = createCinematicView(IMAGE_PATH, 1.5);
        drawingView = createCinematicView(SECOND_IMAGE_PATH, 0.7);
        scaryMomView = createCinematicView(SCARY_MOM_PATH, 1.5);

        cornerView = createCinematicView(CORNER_PATH, 1.0);
        broochView = createCinematicView(BROOCH_PATH, 1.0);

        whiteFlash = new Rectangle();
        whiteFlash.widthProperty().bind(this.root.widthProperty());
        whiteFlash.heightProperty().bind(this.root.heightProperty());
        whiteFlash.setFill(Color.WHITE);
        whiteFlash.setOpacity(0.0);
        whiteFlash.setMouseTransparent(true);
    }

    public void onEnter() {
        //if (!GameState.momRoomVisited) {
        //    GameState.momRoomVisited = true;
        //    this.startCutscene();
        //} else if (GameState.kitchenStormFinished) {
            this.startRatKeyCutscene();
        //} else {
        //    this.finalizeCutscene();
        //}
    }

    public void startRatKeyCutscene() {
        CameraController.setPanningEnabled(true);
        this.removeBlackOverlay();
        this.root.getChildren().removeAll(momView, drawingView, scaryMomView);

        this.setBackground("/images/mom_room_with_rat.png");

        GameState.setKarmaListener((currentKarma, addedPoints) ->
                StatNotification.show((StackPane) this.getRoot(), currentKarma, addedPoints)
        );

        Map<String, Runnable> actions = new HashMap<>();
        Map<String, Animation> animations = new HashMap<>();

        actions.put("choice_ask_for_key", () -> {
            GameState.changeKarma(1);
            this.finalizeCutscene();
            // TODO: Запуск головоломки зі стандартним часом
        });

        actions.put("choice_take_by_force", () -> {
            GameState.changeKarma(-1);
            StoryRunner.playScene("/story/chapter1.json", "take_key_from_rat", (StackPane) this.getRoot(), actions, animations);
        });

        actions.put("show_corner_flash", () -> {
            this.setBackground(CORNER_PATH);

            broochView.setFitWidth(150);
            broochView.setPreserveRatio(true);
            broochView.setTranslateX(100);
            broochView.setTranslateY(100);

            if (!this.roomContentLayer.getChildren().contains(broochView)) {
                this.roomContentLayer.getChildren().add(broochView);
            }

            if (!this.root.getChildren().contains(whiteFlash)) {
                this.root.getChildren().add(whiteFlash);
            }

            broochView.setStyle("-fx-cursor: hand;");
            broochView.setPickOnBounds(true);

            ukma.fourgirls.domain.Item broochItem = new ukma.fourgirls.domain.Item("Брошка", BROOCH_PATH);
            InventoryManager.setupPickupAction(
                    broochView,
                    broochItem,
                    (StackPane) this.getRoot(),
                    "Ви підняли брошку! Тепер вона у вашому інвентарі.",
                    this::onBroochPickedUp
            );
        });

        actions.put("give_brooch", () -> {
            InventoryState.addItem(new ukma.fourgirls.domain.Item("Брошка", BROOCH_PATH));
            NotificationManager.showNotification(this.root, "Ви отримали нову річ: Брошка");
        });

        actions.put("door_interaction_brooch", () -> {
            NotificationManager.showNotification(this.root, "Двері відімкнено!\n Через ваш вибір часу на головоломку стало менше (-1.5 хв).");
            this.finalizeCutscene();
        });

        animations.put("lightning_pause", getLightningAnimation());

        StoryRunner.playScene("/story/chapter1.json", "mom_room_rat_key", (StackPane) this.getRoot(), actions, animations);
    }

    public void startCutscene() {
        Map<String, Runnable> actions = new HashMap<>();
        Map<String, Animation> animations = new HashMap<>();

        animations.put("part1Animation", this.getPart1Animation());
        animations.put("part2Animation", this.getPart2Animation());

        actions.put("showMomView", this::showMomView);
        actions.put("showDrawingView", this::showDrawingView);
        actions.put("showScaryMom", this::showScaryMom);
        actions.put("hideScaryMom", () -> {
            this.hideScaryMom();
            this.finalizeCutscene();
        });

        actions.put("setupKarmaAndOverlay", () -> {
            this.removeBlackOverlay();
            GameState.setKarmaListener((currentKarma, addedPoints) -> StatNotification.show((StackPane) this.getRoot(), currentKarma, addedPoints));
        });

        actions.put("choice_put_near_mom", () -> {
            InventoryState.removeItem("Малюнок");
            GameState.changeKarma(-1);
            this.hideDrawingView();
            StoryRunner.playScene("/story/chapter1.json", "mom_room_scary_sequence", (StackPane) this.getRoot(), actions, animations);
        });

        actions.put("choice_hide_in_pocket", () -> {
            GameState.changeKarma(1);
            this.hideDrawingView();
            StoryRunner.playScene("/story/chapter1.json", "mom_room_hide_sequence", (StackPane) this.getRoot(), actions, animations);
        });

        actions.put("goToKitchenHint", () -> {
            GameState.unlockLocation("Kitchen");
            this.finalizeCutscene();
            NotificationManager.showNotification(this.root, "Нове завдання: Знайдіть їжу на кухні");
        });

        StoryRunner.playScene("/story/chapter1.json", "mom_room_first_visit", (StackPane) this.getRoot(), actions, animations);
    }

    // --- МЕТОДИ ДЛЯ КЕРУВАННЯ СЦЕНОЮ З КОНТРОЛЕРА ---

    public void showMomView() {
        this.root.getChildren().add(momView);
        blackOverlay.toFront();
    }

    public void showDrawingView() {
        this.root.getChildren().remove(momView);
        this.root.getChildren().add(drawingView);
        blackOverlay.toFront();
    }

    public void showScaryMom() {
        if (!this.root.getChildren().contains(scaryMomView)) {
            this.root.getChildren().add(scaryMomView);
        }
        scaryMomView.toFront();
    }

    public void hideScaryMom() {
        this.root.getChildren().remove(scaryMomView);
    }

    public void removeBlackOverlay() {
        this.root.getChildren().remove(blackOverlay);
    }

    public void finalizeCutscene() {
        CameraController.setPanningEnabled(true);
        setupNavigation("MomRoom");
    }

    private Animation getLightningAnimation() {
        FadeTransition flashIn = new FadeTransition(Duration.seconds(0.1), whiteFlash);
        flashIn.setFromValue(0.0);
        flashIn.setToValue(1.0);

        FadeTransition flashOut = new FadeTransition(Duration.seconds(0.3), whiteFlash);
        flashOut.setFromValue(1.0);
        flashOut.setToValue(0.0);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.0));

        return new SequentialTransition(flashIn, flashOut, pause);
    }

    public Animation getPart1Animation() {
        TranslateTransition pan = new TranslateTransition(Duration.seconds(5), momView);
        pan.setFromX(-200);
        pan.setToX(-100);

        FadeTransition show = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        show.setFromValue(1.0);
        show.setToValue(0.0);

        PauseTransition hold = new PauseTransition(Duration.seconds(2));

        FadeTransition hide = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        hide.setFromValue(0.0);
        hide.setToValue(1.0);

        SequentialTransition fadeSeq = new SequentialTransition(show, hold, hide);
        return new ParallelTransition(pan, fadeSeq);
    }

    public Animation getPart2Animation() {
        FadeTransition reveal = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        reveal.setFromValue(1.0);
        reveal.setToValue(0.0);

        TranslateTransition pan = new TranslateTransition(Duration.seconds(4), drawingView);
        pan.setFromX(-100);
        pan.setToX(0);

        return new ParallelTransition(reveal, pan);
    }

    private ImageView createCinematicView(String path, double scale) {
        ImageView view = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
        view.fitHeightProperty().bind(this.root.heightProperty());
        view.setPreserveRatio(true);
        view.setScaleX(scale);
        view.setScaleY(scale);
        return view;
    }

    public void hideDrawingView() {
        this.root.getChildren().remove(drawingView);
    }

    private void onBroochPickedUp() {
        this.setBackground("/images/mom_room_with_rat.png");

        this.root.getChildren().remove(whiteFlash);


        Map<String, Runnable> actions = new HashMap<>();
        Map<String, Animation> animations = new HashMap<>();

        actions.put("door_interaction_brooch", () -> {
            NotificationManager.showNotification(this.root, "Двері відімкнено!\n Через ваш вибір часу на головоломку стало менше (-1.5 хв).");
            this.finalizeCutscene();
        });

        StoryRunner.playScene("/story/chapter1.json", "mom_room_after_brooch", (StackPane) this.getRoot(), actions, animations);
    }
}