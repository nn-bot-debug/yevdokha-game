package ukma.fourgirls.ui.puzzles;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import ukma.fourgirls.state.GameSession;
import ukma.fourgirls.core.AudioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class PendantPuzzle extends StackPane {

    private final GameSession session;
    private final Consumer<Boolean> onPositionFinished;

    private static final String[] SYMBOLS = {"ᚠ", "ᚢ", "ᚦ", "ᚨ", "ᚲ", "ᚷ", "ᚹ", "ᚺ"};
    private final int TOTAL_STEPS = SYMBOLS.length;

    // Стан кілець
    private int leftIdx;
    private int centerIdx;
    private int rightIdx;

    // Комбінація, яку треба зібрати
    private int targetLeft;
    private int targetCenter;
    private int targetRight;

    // Напрямок руху середнього кільця
    private int centerDirectionMultiplier = -1;

    private Label leftSlot;
    private Label centerSlot;
    private Label rightSlot;

    // Індикатори цілі
    private Label timerLabel;
    private Label liveHintLabel;
    private VBox resultBox;
    private Label resultTitle;
    private Button resultButton;

    private HBox controlsRow;
    private final VBox mainContainer;

    private boolean isGameActive = true;
    private Timeline timeline;
    private int timeLeftSeconds = 120;
    private final List<Button> actionButtons = new ArrayList<>();

    public PendantPuzzle(GameSession session, Consumer<Boolean> onPositionFinished) {
        this.session = session;
        this.onPositionFinished = onPositionFinished;

        if (getClass().getResource("/css/puzzle.css") != null) {
            this.getStylesheets().add(getClass().getResource("/css/puzzle.css").toExternalForm());
        }
        this.getStyleClass().add("pendant-bg-container");
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: rgba(11, 19, 16, 0.96);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: #38bdf8;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 20;");
        this.setMaxSize(550, 450);

        mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setVisible(false);

        generateTargetCombination();
        initUI();
        showInstructionOverlay();
    }

    private void generateTargetCombination() {
        Random random = new Random();

        leftIdx = random.nextInt(TOTAL_STEPS);
        centerIdx = random.nextInt(TOTAL_STEPS);
        rightIdx = random.nextInt(TOTAL_STEPS);

        do {
            targetLeft = random.nextInt(TOTAL_STEPS);
            targetCenter = random.nextInt(TOTAL_STEPS);
            targetRight = random.nextInt(TOTAL_STEPS);
        } while (targetLeft == leftIdx && targetCenter == centerIdx && targetRight == rightIdx);
    }

    private void initUI() {
        Label titleLabel = new Label("Древній Шифратор Прикраси");
        titleLabel.getStyleClass().add("pendant-title");

        timerLabel = new Label("Залишилось часу: 03:00");
        timerLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        timerLabel.setTextFill(Color.web("#f87171"));

        VBox targetBox = new VBox(8);
        targetBox.getStyleClass().add("pendant-target-box");
        targetBox.setAlignment(Pos.CENTER);

        Label targetTitle = new Label("НЕОБХІДНА КОМБІНАЦІЯ РУН:");
        targetTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        targetTitle.setTextFill(Color.web("#94a3b8"));

        HBox targetRunesBox = new HBox(30);
        targetRunesBox.setAlignment(Pos.CENTER);

        targetRunesBox.getChildren().addAll(
                createTargetRuneView(SYMBOLS[targetLeft]),
                createTargetRuneView(SYMBOLS[targetCenter]),
                createTargetRuneView(SYMBOLS[targetRight])
        );
        targetBox.getChildren().addAll(targetTitle, targetRunesBox);

        // --- ГОЛОВНИЙ РЯД КІЛЕЦЬ-СЛОТІВ (ГОРИЗОНТАЛЬНИЙ РЯД) ---
        HBox slotsRow = new HBox(40);
        slotsRow.setAlignment(Pos.CENTER);
        slotsRow.setPadding(new Insets(10, 0, 10, 0));

        leftSlot = createSlotComponent(Color.web("#10b981"));
        centerSlot = createSlotComponent(Color.web("#60a5fa"));
        rightSlot = createSlotComponent(Color.web("#fbbf24"));

        slotsRow.getChildren().addAll(leftSlot, centerSlot, rightSlot);
        updateSlotDisplays();

        // --- Кнопки керування ---
        liveHintLabel = new Label("Оберіть кільце для початку дешифрування механізму...");
        liveHintLabel.setFont(Font.font("Verdana", 14));
        liveHintLabel.setTextFill(Color.web("#a7f3d0"));
        liveHintLabel.setStyle("-fx-background-color: rgba(30, 41, 59, 0.5); -fx-padding: 8 20; -fx-background-radius: 8;");
        liveHintLabel.setEffect(new DropShadow(10, Color.web("#10b981")));

        controlsRow = new HBox(25);
        controlsRow.setAlignment(Pos.CENTER);

        Button btnLeft = new Button("Обертати Ліве");
        Button btnCenter = new Button("Обертати Центр");
        Button btnRight = new Button("Обертати Праве");

        actionButtons.add(btnLeft);
        actionButtons.add(btnCenter);
        actionButtons.add(btnRight);

        for (Button b : actionButtons) {
            b.getStyleClass().add("pendant-control-button");
        }

        btnLeft.setOnMouseEntered(e -> showLiveHint("Ліве рушить вперед (+1), Центр зміщується на 2 кроки за полярністю."));
        btnCenter.setOnMouseEntered(e -> showLiveHint("Центр рушить вперед (+1), за замовчуванням штовхає Праве на (+1)."));
        btnRight.setOnMouseEntered(e -> showLiveHint("Праве міняє полярність Центру (Реверс/Прямий) та штовхає Ліве назад (-1)."));

        for (Button b : actionButtons) {
            b.setOnMouseExited(e -> updateDirectionHint());
        }

        btnLeft.setOnAction(e -> clickLeftRing());
        btnCenter.setOnAction(e -> clickCenterRing());
        btnRight.setOnAction(e -> clickRightRing());

        controlsRow.getChildren().addAll(btnLeft, btnCenter, btnRight);

        resultBox = new VBox(15);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setVisible(false);
        resultBox.setManaged(false);

        resultTitle = new Label();
        resultTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        resultButton = new Button("Продовжити");
        resultButton.getStyleClass().add("pendant-control-button");
        resultBox.getChildren().addAll(resultTitle, resultButton);

        mainContainer.getChildren().addAll(titleLabel, timerLabel, targetBox, slotsRow, liveHintLabel, controlsRow, resultBox);
        this.getChildren().add(mainContainer);
    }

    private void showInstructionOverlay() {
        VBox tutorialBox = new VBox(20);
        tutorialBox.setAlignment(Pos.CENTER);
        tutorialBox.setMaxWidth(500);
        tutorialBox.setMaxHeight(380);
        tutorialBox.setPadding(new Insets(30, 40, 30, 40));
        tutorialBox.getStyleClass().add("settings-dialog");

        Label titleLabel = new Label("Шифратор прикраси");
        titleLabel.getStyleClass().add("settings-title");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#38bdf8"));
        titleLabel.setEffect(new DropShadow(12, Color.web("#0284c7")));

        Label descriptionLabel = new Label(
                "Віднови магічний кулон, виставивши правильну комбінацію рун.\n\n" +
                        "Пам'ятай про взаємозв'язок елементів браслета:\n" +
                        "• ЛІВЕ — зміщує Центральне на 2 кроки\n" +
                        "• ЦЕНТР — тягне за собою Праве на 1 крок\n" +
                        "• ПРАВЕ — штовхає Ліве назад та змінює полярність Центру\n\n" +
                        "Прорахуй алгоритм, поки діє магія часу!"
        );
        descriptionLabel.getStyleClass().add("settings-label");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
        descriptionLabel.setTextFill(Color.web("#e2e8f0"));
        descriptionLabel.setStyle("-fx-text-alignment: center; -fx-line-spacing: 5;");

        Button acceptButton = new Button("ПОЧАТИ");
        acceptButton.getStyleClass().add("settings-button");
        acceptButton.setPrefWidth(160);
        acceptButton.setStyle("-fx-cursor: hand;");

        acceptButton.setOnAction(e -> {
            AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");
            this.getChildren().remove(tutorialBox);

            this.setMaxSize(850, 580);

            mainContainer.setVisible(true);
            updateDirectionHint();

            session.setKarmaListener((currentKarma, addedPoints) -> {
                ukma.fourgirls.core.StatNotification.show(this, currentKarma, addedPoints);
                var notification = this.getChildren().get(this.getChildren().size() - 1);
                StackPane.setAlignment(notification, Pos.TOP_CENTER);
                StackPane.setMargin(notification, new Insets(60, 0, 0, 0));
                notification.setTranslateX(0);
                notification.toFront();
            });

            startTimer();
        });

        tutorialBox.getChildren().addAll(titleLabel, descriptionLabel, acceptButton);
        this.getChildren().add(tutorialBox);
    }

    private static void StackMargin(javafx.scene.Node n, Insets i) {
        StackPane.setMargin(n, i);
    }

    private Label createSlotComponent(Color neonColor) {
        Label label = new Label();
        label.getStyleClass().add("pendant-slot");
        label.setStyle(label.getStyle() + " -fx-border-color: " + toHex(neonColor) + ";");
        label.setEffect(new DropShadow(15, neonColor));
        return label;
    }

    private Label createTargetRuneView(String rune) {
        Label label = new Label(rune);
        label.setFont(Font.font("Segoe UI Historic", FontWeight.BOLD, 28));
        label.setTextFill(Color.web("#34d399"));
        label.setStyle("-fx-padding: 0 10;");
        return label;
    }

    private void showLiveHint(String text) {
        liveHintLabel.setText(text);
    }

    private void updateDirectionHint() {
        if (centerDirectionMultiplier == -1) {
            liveHintLabel.setText("Полярність Центру: РЕВЕРС (Ліве посуне Центр на -2 кроки)");
            liveHintLabel.setTextFill(Color.web("#60a5fa"));
        } else {
            liveHintLabel.setText("Полярність Центру: ПРЯМИЙ ХІД (Ліве посуне Центр на +2 кроки)");
            liveHintLabel.setTextFill(Color.web("#c084fc"));
        }
    }

    // --- Механізм обертання та слухання стану ---

    private void clickLeftRing() {
        if (!isGameActive) return;
        AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");

        leftIdx = (leftIdx + 1) % TOTAL_STEPS;

        int influence = (2 * centerDirectionMultiplier);
        centerIdx = (centerIdx + influence + TOTAL_STEPS * 2) % TOTAL_STEPS;

        updateSlotDisplays();
        checkWinCondition();
    }

    private void clickCenterRing() {
        if (!isGameActive) return;
        AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");

        centerIdx = (centerIdx + 1) % TOTAL_STEPS;
        rightIdx = (rightIdx + 1) % TOTAL_STEPS;

        updateSlotDisplays();
        checkWinCondition();
    }

    private void clickRightRing() {
        if (!isGameActive) return;
        AudioManager.getInstance().buttonSound("/music/button-click-sound.wav");

        rightIdx = (rightIdx + 1) % TOTAL_STEPS;
        centerDirectionMultiplier *= -1;
        leftIdx = (leftIdx - 1 + TOTAL_STEPS) % TOTAL_STEPS;

        if (centerDirectionMultiplier == -1) {
            centerSlot.setEffect(new DropShadow(20, Color.web("#60a5fa")));
        } else {
            centerSlot.setEffect(new DropShadow(25, Color.web("#a855f7")));
        }

        updateSlotDisplays();
        updateDirectionHint();
        checkWinCondition();
    }

    private void updateSlotDisplays() {
        leftSlot.setText(SYMBOLS[leftIdx]);
        centerSlot.setText(SYMBOLS[centerIdx]);
        rightSlot.setText(SYMBOLS[rightIdx]);

        toggleSlotCorrectStyle(leftSlot, leftIdx == targetLeft);
        toggleSlotCorrectStyle(centerSlot, centerIdx == targetCenter);
        toggleSlotCorrectStyle(rightSlot, rightIdx == targetRight);
    }

    private void toggleSlotCorrectStyle(Label slot, boolean isCorrect) {
        slot.getStyleClass().remove("pendant-slot-correct");
        if (isCorrect) {
            slot.getStyleClass().add("pendant-slot-correct");
        }
    }

    private void checkWinCondition() {
        if (leftIdx == targetLeft && centerIdx == targetCenter && rightIdx == targetRight) {
            handleEndGame(true);
        }
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeftSeconds--;
            int mins = timeLeftSeconds / 60;
            int secs = timeLeftSeconds % 60;
            timerLabel.setText(String.format("Залишилось часу: %02d:%02d", mins, secs));

            if (timeLeftSeconds <= 0) {
                handleEndGame(false);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleEndGame(boolean isWin) {
        isGameActive = false;
        if (timeline != null) {
            timeline.stop();
            timeline.getKeyFrames().clear();
        }

        if (controlsRow != null) {
            controlsRow.setVisible(false);
            controlsRow.setManaged(false);
        }
        if (liveHintLabel != null) {
            liveHintLabel.setVisible(false);
            liveHintLabel.setManaged(false);
        }

        if (isWin) {
            session.changeKarma(1);
            resultTitle.setText("Шифратор піддався! Механізм прикраси полагоджено.");
            resultTitle.setTextFill(Color.web("#34d399"));
            resultButton.setText("Забрати прикрасу");
        } else {
            session.changeKarma(-2);
            resultTitle.setText("Час вичерпано. Механізм заклинило темною магією.");
            resultTitle.setTextFill(Color.web("#ef4444"));
            resultButton.setText("Прийняти долю");
        }

        resultButton.setOnAction(e -> {
            if (onPositionFinished != null) {
                onPositionFinished.accept(isWin);
            }
        });

        resultBox.setVisible(true);
        resultBox.setManaged(true);
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}