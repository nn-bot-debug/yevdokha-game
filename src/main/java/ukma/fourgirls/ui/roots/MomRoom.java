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
import ukma.fourgirls.logic.StorySequence;
import ukma.fourgirls.state.InventoryState;
import ukma.fourgirls.ui.CameraController;

import java.util.Objects;

public class MomRoom extends Place {
    private static final String IMAGE_PATH = "/images/mother_room.png";
    private static final String SECOND_IMAGE_PATH = "/images/drawing.png";

    private final Rectangle blackOverlay;

    public MomRoom() {
        super(IMAGE_PATH);

        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/choices.css")).toExternalForm());

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
                    showChoices(drawingView);
                })
                .play();
    }

    // --- ДОПОМІЖНІ МЕТОДИ СТВОРЕННЯ АНІМАЦІЙ ---

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

    // --- ЛОГІКА ВИБОРУ ---

    private void showChoices(ImageView secondImage) {
        VBox choiceBox = new VBox(20);
        choiceBox.setAlignment(Pos.CENTER);
        choiceBox.setMaxSize(400, 250);
        choiceBox.getStyleClass().add("choice-box");

        Label promptText = new Label("Що робити із малюнком?");
        promptText.getStyleClass().add("choice-prompt");

        Button btnPutNearMom = new Button("Покласти біля мами");
        btnPutNearMom.getStyleClass().add("choice-button");
        btnPutNearMom.setOnAction(e -> {
            InventoryState.removeItem("Малюнок");
            closeCutscene(secondImage, choiceBox);
        });

        Button btnHideInPocket = new Button("Заховати в кишеню");
        btnHideInPocket.getStyleClass().add("choice-button");
        btnHideInPocket.setOnAction(e -> closeCutscene(secondImage, choiceBox));

        choiceBox.getChildren().addAll(promptText, btnPutNearMom, btnHideInPocket);
        this.root.getChildren().add(choiceBox);
    }

    private void closeCutscene(ImageView cinematicImage, VBox menu) {
        this.root.getChildren().remove(cinematicImage);
        this.root.getChildren().remove(menu);
        CameraController.setPanningEnabled(true);
        setupNavigation("MomRoom");
    }
}