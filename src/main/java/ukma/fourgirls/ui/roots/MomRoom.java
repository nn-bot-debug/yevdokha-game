package ukma.fourgirls.ui.roots;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ukma.fourgirls.core.ChoiceManager;
import ukma.fourgirls.logic.StorySequence;
import ukma.fourgirls.state.GameState;
import ukma.fourgirls.state.InventoryState;
import ukma.fourgirls.ui.CameraController;

import java.util.Objects;

public class MomRoom extends Place {
    private static final String IMAGE_PATH = "/images/mother_room.png";
    private static final String SECOND_IMAGE_PATH = "/images/drawing.png";

    private final Rectangle blackOverlay;

    public MomRoom() {
        super(IMAGE_PATH);

        blackOverlay = new Rectangle();
        blackOverlay.widthProperty().bind(this.root.widthProperty());
        blackOverlay.heightProperty().bind(this.root.heightProperty());
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setMouseTransparent(true);

        this.root.getChildren().add(blackOverlay);
        CameraController.setPanningEnabled(false);

        // Запускаємо наш сценарій замість старих колбеків!
        startCutscene();
    }

    private void startCutscene() {
        ImageView momView = createCinematicView(IMAGE_PATH, 1.5);
        ImageView drawingView = createCinematicView(SECOND_IMAGE_PATH, 1.3);

        // 🎬 СЦЕНАРІЙ КІМНАТИ 🎬
        StorySequence.create(this.root)
                .addDialogue("Побачене ніяк не засмутило дівчинку, навпаки, ніби вона все життя тільки цього і чекала, і от нарешті це сталося.")
                .execute(() -> {
                    this.root.getChildren().add(momView);
                    blackOverlay.toFront();
                })
                .addAnimation(createPart1Animation(momView)) // Анімація мами (рух + затемнення)
                .execute(() -> {
                    this.root.getChildren().remove(momView);
                    this.root.getChildren().add(drawingView);
                    blackOverlay.toFront();
                })
                .addAnimation(createPart2Animation(drawingView)) // Анімація малюнка (рух + проявлення)
                .execute(() -> {
                    this.root.getChildren().remove(blackOverlay);

                    ChoiceManager.Option[] options = {
                            new ChoiceManager.Option("Покласти біля мами", () -> {
                                InventoryState.removeItem("Малюнок");
                                GameState.changeKarma(-1);
                                finalizeCutscene(drawingView);
                            }),
                            new ChoiceManager.Option("Заховати в кишеню", () -> {
                                GameState.changeKarma(1);
                                finalizeCutscene(drawingView);
                            })
                    };
                    ChoiceManager.show(this.root, "Що робити із малюнком?", options);
                })
                .play();
    }

    // --- ДОПОМІЖНІ МЕТОДИ СТВОРЕННЯ АНІМАЦІЙ ---

    private void finalizeCutscene(ImageView cinematicView) {
        this.root.getChildren().remove(cinematicView);
        CameraController.setPanningEnabled(true);
        setupNavigation("MomRoom");
    }

    private ImageView createCinematicView(String path, double scale) {
        ImageView view = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
        view.fitHeightProperty().bind(this.root.heightProperty());
        view.setPreserveRatio(true);
        view.setScaleX(scale);
        view.setScaleY(scale);
        return view;
    }

    private Animation createPart1Animation(ImageView target) {
        TranslateTransition pan = new TranslateTransition(Duration.seconds(5), target);
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

    private Animation createPart2Animation(ImageView target) {
        FadeTransition reveal = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        reveal.setFromValue(1.0);
        reveal.setToValue(0.0);

        TranslateTransition pan = new TranslateTransition(Duration.seconds(4), target);
        pan.setFromX(-100);
        pan.setToX(0);

        return new ParallelTransition(reveal, pan);
    }
}