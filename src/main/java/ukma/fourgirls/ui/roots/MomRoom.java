package ukma.fourgirls.ui.roots;

import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
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

    private final Rectangle blackOverlay;
    private final ImageView momView;
    private final ImageView drawingView;
    private final ImageView scaryMomView;

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
        drawingView = createCinematicView(SECOND_IMAGE_PATH, 1.3);
        scaryMomView = createCinematicView(SCARY_MOM_PATH, 1.5);

        this.startCutscene();
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

    // Відновлює звичайний режим гри
    public void finalizeCutscene() {
        CameraController.setPanningEnabled(true);
        setupNavigation("MomRoom");
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
}