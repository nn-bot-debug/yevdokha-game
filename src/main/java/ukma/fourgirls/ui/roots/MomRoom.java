package ukma.fourgirls.ui.roots;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import ukma.fourgirls.core.DialogueManager;
import ukma.fourgirls.state.InventoryState;
import ukma.fourgirls.ui.CameraController;

import java.util.Objects;

public class MomRoom extends Place {
    private static final String IMAGE_PATH = "/images/mother_room.png";
    private static final String SECOND_IMAGE_PATH = "/images/drawing.png";

    private Rectangle blackOverlay;

    public MomRoom() {
        super(IMAGE_PATH);

        blackOverlay = new Rectangle();
        blackOverlay.widthProperty().bind(this.root.widthProperty());
        blackOverlay.heightProperty().bind(this.root.heightProperty());
        blackOverlay.setFill(Color.BLACK);

        blackOverlay.setMouseTransparent(true);

        this.root.getChildren().add(blackOverlay);
        CameraController.setPanningEnabled(false);

        String[] momRoomDialogue = {
                "Побачене ніяк не засмутило дівчинку, навпаки, ніби вона все життя тільки цього і чекала, і от нарешті це сталося."
        };

        DialogueManager.getInstance().play(this.root, momRoomDialogue, () -> {
            playCutscene();
        });
    }

    private void playCutscene() {
        ImageView cinematicView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMAGE_PATH))));
        cinematicView.fitHeightProperty().bind(this.root.heightProperty());
        cinematicView.setPreserveRatio(true);
        cinematicView.setScaleX(1.5);
        cinematicView.setScaleY(1.5);

        this.root.getChildren().add(cinematicView);
        blackOverlay.toFront();

        TranslateTransition panImage = new TranslateTransition(Duration.seconds(5), cinematicView);
        panImage.setFromX(-200);
        panImage.setToX(-100);

        FadeTransition showCutscene = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        showCutscene.setFromValue(1.0);
        showCutscene.setToValue(0.0);

        PauseTransition hold = new PauseTransition(Duration.seconds(2));

        FadeTransition hideCutscene = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        hideCutscene.setFromValue(0.0);
        hideCutscene.setToValue(1.0);

        SequentialTransition fadeSeq = new SequentialTransition(showCutscene, hold, hideCutscene);
        ParallelTransition fullCutscene = new ParallelTransition(panImage, fadeSeq);

        fullCutscene.setOnFinished(e -> {
            this.root.getChildren().remove(cinematicView);

            ImageView cinematicView2 = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(SECOND_IMAGE_PATH))));
            cinematicView2.fitHeightProperty().bind(this.root.heightProperty());
            cinematicView2.setPreserveRatio(true);
            cinematicView2.setScaleX(1.3);
            cinematicView2.setScaleY(1.3);

            this.root.getChildren().add(cinematicView2);
            blackOverlay.toFront();

            FadeTransition revealSecondImage = new FadeTransition(Duration.seconds(1.5), blackOverlay);
            revealSecondImage.setFromValue(1.0);
            revealSecondImage.setToValue(0.0);

            TranslateTransition panImage2 = new TranslateTransition(Duration.seconds(4), cinematicView2);
            panImage2.setFromX(-100);
            panImage2.setToX(0);

            ParallelTransition part2Cutscene = new ParallelTransition(revealSecondImage, panImage2);

            part2Cutscene.setOnFinished(e2 -> {
                this.root.getChildren().remove(blackOverlay);
                showChoices(cinematicView2);
            });

            part2Cutscene.play();
        });

        fullCutscene.play();
    }

    private void showChoices(ImageView secondImage) {

        VBox choiceBox = new VBox(20);
        choiceBox.setAlignment(Pos.CENTER);
        choiceBox.setMaxSize(400, 250);

        choiceBox.setStyle(
                "-fx-background-color: rgba(20, 20, 20, 0.85);" +
                        "-fx-padding: 30px;" +
                        "-fx-border-color: #4a5c4d;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        );

        Label promptText = new Label("Що робити із малюнком?");
        promptText.setTextFill(Color.web("#d1d1d1"));
        promptText.setFont(Font.font("Arial", 22));

        Button btnPutNearMom = new Button("Покласти біля мами");
        Button btnHideInPocket = new Button("Заховати в кишеню");

        String btnStyle =
                "-fx-background-color: #2e261b; " +
                        "-fx-text-fill: #a4bfa7; " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10px 20px;";

        btnPutNearMom.setStyle(btnStyle);
        btnHideInPocket.setStyle(btnStyle);

        btnPutNearMom.setOnMouseEntered(e -> btnPutNearMom.setStyle(btnStyle + "-fx-border-color: #a4bfa7;"));
        btnPutNearMom.setOnMouseExited(e -> btnPutNearMom.setStyle(btnStyle));
        btnHideInPocket.setOnMouseEntered(e -> btnHideInPocket.setStyle(btnStyle + "-fx-border-color: #a4bfa7;"));
        btnHideInPocket.setOnMouseExited(e -> btnHideInPocket.setStyle(btnStyle));

        // 4. Логіка для першого вибору (-1 подих лісу)
        btnPutNearMom.setOnAction(e -> {
            // TODO: Додати в GameState метод зміни карми
            // GameState.modifyForestBreath(-1);

            // Забираємо малюнок з інвентарю, бо ми його віддали
            InventoryState.removeItem("Малюнок");

            closeCutscene(secondImage, choiceBox);
        });

        // 5. Логіка для другого вибору (+1 подих лісу)
        btnHideInPocket.setOnAction(e -> {
            // TODO: Додати в GameState метод зміни карми
            // GameState.modifyForestBreath(1);

            // Малюнок НЕ видаляємо з InventoryState, він залишається в кишені

            closeCutscene(secondImage, choiceBox);
        });

        choiceBox.getChildren().addAll(promptText, btnPutNearMom, btnHideInPocket);
        this.root.getChildren().add(choiceBox);
    }

    private void closeCutscene(ImageView cinematicImage, VBox menu) {
        this.root.getChildren().remove(cinematicImage);
        this.root.getChildren().remove(menu);
        activateGameplay();
    }

    private void activateGameplay() {
        CameraController.setPanningEnabled(true);
        setupNavigation("MomRoom");
    }
}