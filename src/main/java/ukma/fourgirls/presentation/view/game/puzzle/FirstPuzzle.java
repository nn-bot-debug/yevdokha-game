package ukma.fourgirls.presentation.view.game.puzzle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ukma.fourgirls.GameContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FirstPuzzle extends Puzzle {

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

    private final Runnable onWinCallback;

    private Timeline timeline;
    private int secondsLeft;
    private final Text timerText;
    private final Text hintText;

    private final HBox puzzleBox;
    private boolean isGameActive = true;
    private boolean hasLostOnce = false;

    private final VBox mainLayout;

    public FirstPuzzle(GameContext context, Runnable onWinCallback) {
        super(context);
        this.onWinCallback = onWinCallback;

        this.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/puzzle.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/menu/settings.css")).toExternalForm()
        );

        setupBackground("/images/canvas/door.png", 12);

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
            secondsLeft = 40;
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
        mainLayout.getChildren().add(createResultBox());

        this.getChildren().add(mainLayout);
        String title = "Головоломка: Градієнтний замок";
        String description =
                """
                        Старі двері заклинило містичним магічним механізмом.
                        
                        Перед тобою розірвані кольорові камені. Твоє завдання — міняти їх місцями так, \
                        щоб вони утворили ідеальний плавний градієнт від найсвітлішого (ліворуч) до найтемнішого (праворуч).
                        
                        Поспішай, механізм заблокується, коли час закінчиться!""";
        showInstructionOverlay(title, description, this::onStart);
    }

    private void onStart() {
        mainLayout.setVisible(true);
        setupKarmaListener(60);
        startTimer();
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

        showResultOverlay("Замок успішно відімкнено!", true, "Продовжити шлях", onWinCallback);
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
            resultTitle.setText("Час вийшов! Замок заклинило.");
        } else {
            resultTitle.setText("Час вийшов! Замок заклинило.");
        }
        showResultOverlay("Час вийшов", false, "Спробувати знову", this::restartPuzzle);
    }

    private void restartPuzzle() {
        isGameActive = true;
        resultBox.setVisible(false);
        hintText.setFill(Color.web("#9ba89e"));
        timerText.setFill(Color.web("#f87171"));

        if (session.hasItem("Ключ")) {
            secondsLeft = 80;
        } else if (session.hasItem("Брошка")) {
            secondsLeft = 40;
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
