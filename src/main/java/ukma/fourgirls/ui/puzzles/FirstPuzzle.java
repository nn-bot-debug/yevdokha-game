package ukma.fourgirls.ui.puzzles;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.core.AudioManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FirstPuzzle extends StackPane {

    private final Color[] correctOrder = {
            Color.rgb(244, 173, 189),
            Color.rgb(248, 160, 180),
            Color.rgb(244, 116, 159),
            Color.rgb(236, 76, 159),
            Color.rgb(211, 58, 125),
            Color.rgb(204, 45, 114),
            Color.rgb(213, 30, 109)
    };

    private final List<Rectangle> rectangles = new ArrayList<>();
    private Rectangle firstSelectedRectangle = null;

    private final GameSession session;
    private final Runnable onWinCallback;

    private Timeline timeline;
    private int secondsLeft;
    private final Text timerText;
    private final Text hintText;

    private final HBox puzzleBox;
    private final VBox resultBox;
    private final Text resultTitle;
    private final Button resultButton;
    private boolean isGameActive = true;
    private boolean hasLostOnce = false;

    private final VBox mainLayout;

    public  FirstPuzzle(GameSession session, Runnable onWinCallback) {
        this.session = session;
        this.onWinCallback = onWinCallback;

        this.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/puzzle.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/settings.css")).toExternalForm()
        );

        try {
            Image bgImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/canvas/door.png")));
            ImageView backgroundView = new ImageView(bgImg);

            backgroundView.setFitWidth(1920);
            backgroundView.setFitHeight(1080);
            backgroundView.setPreserveRatio(false);

            backgroundView.setEffect(new GaussianBlur(12));
            this.getChildren().add(backgroundView);
        } catch (Exception e) {
            System.err.println("Помилка завантаження фону для головоломки, заливаємо темним кольором.");
        }

        Pane dimOverlay = new Pane();
        dimOverlay.setStyle("-fx-background-color: rgba(10, 12, 15, 0.45);");
        this.getChildren().add(dimOverlay);

        mainLayout = new VBox();
        mainLayout.getStyleClass().add("gradient-puzzle-container");
        mainLayout.setVisible(false);

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.setMargin(topBox, new javafx.geometry.Insets(20, 0, 0, 0));

        hintText = new Text("Віднови градієнт, щоб відімкнути замок...");
        hintText.setFill(Color.web("#9ba89e"));

        try {
            Font customFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 24);
            hintText.setFont(customFont != null ? customFont : Font.font("Verdana", 22));
        } catch (Exception e) {
            hintText.setFont(Font.font("Verdana", 22));
        }

        timerText = new Text();
        timerText.setFill(Color.web("#f87171"));
        try {
            Font timerFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Creepster-Regular.ttf"), 42);
            timerText.setFont(timerFont != null ? timerFont : Font.font("Verdana", 36));
        } catch (Exception e) {
            timerText.setFont(Font.font("Verdana", 36));
        }

        topBox.getChildren().addAll(hintText, timerText);
        mainLayout.getChildren().add(topBox);

        if (session.hasItem("Ключ")) {
            secondsLeft = 80;
        } else if (session.hasItem("Брошка")) {
            secondsLeft = 15;
        } else {
            secondsLeft = 60;
        }
        updateTimerDisplay();

        List<Color> shuffledColors = new ArrayList<>(List.of(correctOrder));
        while (shuffledColors.equals(List.of(correctOrder))) {
            Collections.shuffle(shuffledColors);
        }

        puzzleBox = new HBox();
        puzzleBox.getStyleClass().add("puzzle-lock-frame");
        puzzleBox.setMaxSize(HBox.USE_PREF_SIZE, HBox.USE_PREF_SIZE);

        for (int i = 0; i < 7; i++) {
            Rectangle rect = new Rectangle(95, 260);
            rect.setArcWidth(12);
            rect.setArcHeight(12);
            rect.setFill(shuffledColors.get(i));
            rect.setStroke(Color.TRANSPARENT);
            rect.setStrokeWidth(4);
            rect.setStyle("-fx-cursor: hand;");

            rect.setOnMouseClicked(e -> handleRectangleClick(rect));

            rectangles.add(rect);
            puzzleBox.getChildren().add(rect);
        }
        mainLayout.getChildren().add(puzzleBox);

        resultBox = new VBox();
        resultBox.getStyleClass().add("puzzle-result-box");
        VBox.setMargin(resultBox,  new javafx.geometry.Insets(0, 0, 40, 0));

        resultBox.setVisible(false);
        resultBox.managedProperty().bind(resultBox.visibleProperty());

        resultTitle = new Text();
        try {
            Font titleFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Epoch_YP_Demo.ttf"), 26);
            resultTitle.setFont(titleFont != null ? titleFont : Font.font("Verdana", 24));
        } catch (Exception e) {
            resultTitle.setFont(Font.font("Verdana", 24));
        }

        resultButton = new Button();
        resultButton.getStyleClass().add("puzzle-action-button");

        resultBox.getChildren().addAll(resultTitle, resultButton);
        mainLayout.getChildren().add(resultBox);

        this.getChildren().add(mainLayout);

        showInstructionOverlay();
    }

    private void showInstructionOverlay() {
        VBox tutorialBox = new VBox(20);
        tutorialBox.setAlignment(Pos.CENTER);
        tutorialBox.setMaxWidth(500);
        tutorialBox.setMaxHeight(380);
        tutorialBox.setPadding(new Insets(30, 40, 30, 40));
        tutorialBox.getStyleClass().add("settings-dialog");

        Label titleLabel = new Label("Головоломка: Градієнтний замок ");
        titleLabel.getStyleClass().add("settings-title");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(
                "Старі двері заклинило містичним магічним механізмом.\n\n" +
                        "Перед тобою розірвані кольорові камені. Твоє завдання — міняти їх місцями так, " +
                        "щоб вони утворили ідеальний плавний градієнт від найсвітлішого (ліворуч) до найтемнішого (праворуч).\n\n" +
                        "Поспішай, механізм заблокується, коли час закінчиться!"
        );
        descriptionLabel.getStyleClass().add("settings-label");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 15px; -fx-text-alignment: center; -fx-line-spacing: 5;");

        Button acceptButton = new Button("ПОЧАТИ");
        acceptButton.getStyleClass().add("settings-button");
        acceptButton.setPrefWidth(160);
        acceptButton.setStyle("-fx-cursor: hand;");

        acceptButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            this.getChildren().remove(tutorialBox);
            mainLayout.setVisible(true);
            session.setKarmaListener((currentKarma, addedPoints) -> {
                ukma.fourgirls.core.StatNotification.show(this, currentKarma, addedPoints);
                var notification = this.getChildren().get(this.getChildren().size() - 1);
                StackPane.setAlignment(notification, Pos.TOP_CENTER);
                StackPane.setMargin(notification, new Insets(40, 0, 0, 0));
                notification.setTranslateX(0);
                notification.toFront();
            });
            startTimer();
        });

        tutorialBox.getChildren().addAll(titleLabel, descriptionLabel, acceptButton);
        this.getChildren().add(tutorialBox);
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            updateTimerDisplay();

            if (secondsLeft <= 0) {
                handleLose();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerDisplay() {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void handleRectangleClick(Rectangle clickedRect) {
        if (!isGameActive) return;
        if (firstSelectedRectangle == null) {
            firstSelectedRectangle = clickedRect;
            clickedRect.setStroke(Color.WHITE);
        } else {
            if (firstSelectedRectangle == clickedRect) {
                firstSelectedRectangle.setStroke(Color.TRANSPARENT);
                firstSelectedRectangle = null;
                return;
            }

            Color color1 = (Color) firstSelectedRectangle.getFill();
            Color color2 = (Color) clickedRect.getFill();

            firstSelectedRectangle.setFill(color2);
            clickedRect.setFill(color1);

            firstSelectedRectangle.setStroke(Color.TRANSPARENT);
            firstSelectedRectangle = null;

            if (checkIfGradientCorrect()) {
                System.out.println("Головоломка розгадана!");
                handleWin();
            }
        }
    }

    private void handleWin() {
        isGameActive = false;
        timeline.stop();
        timerText.setFill(Color.LIGHTGREEN);
        hintText.setFill(Color.LIGHTGREEN);

        for (Rectangle rect : rectangles) {
            rect.setStroke(Color.LIGHTGREEN);
            rect.setStyle("-fx-cursor: default;");
        }

        resultTitle.setText("Замок успішно відімкнено!");
        resultTitle.getStyleClass().removeAll("puzzle-text-lose");
        resultTitle.getStyleClass().add("puzzle-text-win");

        resultButton.setText("Продовжити шлях");
        resultButton.setOnAction(e -> {
            if (onWinCallback != null)
                onWinCallback.run();
        });
        resultBox.setVisible(true);
    }

    private void handleLose() {
        isGameActive = false;
        timeline.stop();
        timerText.setText("00:00");
        timerText.setFill(Color.RED);
        hintText.setFill(Color.RED);

        for (Rectangle rect : rectangles) {
            rect.setStyle("-fx-cursor: default;");
        }
        if (firstSelectedRectangle != null)
            firstSelectedRectangle.setStroke(Color.TRANSPARENT);

        if (!hasLostOnce) {
            hasLostOnce = true;
            session.changeKarma(-2);
            resultTitle.setText("Час вийшов! Замок заклинило. Ви втратили 2 подихи вітру.");
        } else {
            resultTitle.setText("Час вийшов! Замок заклинило.");
        }
        resultTitle.getStyleClass().removeAll("puzzle-text-win");
        resultTitle.getStyleClass().add("puzzle-text-lose");

        resultButton.setText("Спробувати знову");
        resultButton.setOnAction(e -> restartPuzzle());

        resultBox.setVisible(true);
    }

    private void restartPuzzle() {
        isGameActive = true;
        resultBox.setVisible(false);
        hintText.setFill(Color.web("#9ba89e"));
        timerText.setFill(Color.web("#f87171"));

        if (session.hasItem("Ключ")) {
            secondsLeft = 80;
        } else if (session.hasItem("Брошка")) {
            secondsLeft = 15;
        } else {
            secondsLeft = 60;
        }
        updateTimerDisplay();

        List<Color> shuffledColors = new ArrayList<>(List.of(correctOrder));
        while (shuffledColors.equals(List.of(correctOrder))) {
            Collections.shuffle(shuffledColors);
        }

        for (int i = 0; i < 7; i++) {
            rectangles.get(i).setFill(shuffledColors.get(i));
            rectangles.get(i).setStroke(Color.TRANSPARENT);
            rectangles.get(i).setStyle("-fx-cursor: hand;");
        }

        startTimer();
    }

    private boolean checkIfGradientCorrect() {
        for (int i = 0; i < rectangles.size(); i++) {
            Color currentTitleColor = (Color)  rectangles.get(i).getFill();
            Color expectedColor = correctOrder[i];

            if (!currentTitleColor.equals(expectedColor))
                return false;
        }
        return true;
    }
}
